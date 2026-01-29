package com.thunderplay.data.repository

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.thunderplay.data.firebase.FirestoreAlbum
import com.thunderplay.data.firebase.FirestoreTrack
import com.thunderplay.data.local.dao.TrackDao
import com.thunderplay.data.local.entity.toEntity
import com.thunderplay.data.local.entity.toTrack
import com.thunderplay.data.mapper.FirestoreMapper.toAlbum
import com.thunderplay.data.mapper.FirestoreMapper.toTrack
import com.thunderplay.domain.model.Album
import com.thunderplay.domain.model.SearchResults
import com.thunderplay.domain.model.Track
import com.thunderplay.domain.repository.MusicError
import com.thunderplay.domain.repository.MusicRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val trackDao: TrackDao,
    @ApplicationContext private val context: Context
) : MusicRepository {

    companion object {
        private const val TAG = "FirebaseRepository"
        private const val TRACKS_COLLECTION = "tracks"
        private const val ALBUMS_COLLECTION = "albums"
    }

    /**
     * Search tracks by title or artist (case-insensitive prefix search)
     */
    override suspend fun searchTracks(query: String, page: Int): Result<SearchResults> {
        return withContext(Dispatchers.IO) {
            try {
                val lowerQuery = query.lowercase()
                
                // Firestore doesn't support full-text search, so we fetch all and filter
                // For a small music library this is fine. For larger libraries, consider Algolia/Typesense
                val snapshot = firestore.collection(TRACKS_COLLECTION)
                    .get()
                    .await()
                
                val tracks = snapshot.documents.mapNotNull { doc ->
                    val firestoreTrack = doc.toObject(FirestoreTrack::class.java)
                    firestoreTrack?.let {
                        // Filter by title or artist containing query
                        if (it.title.lowercase().contains(lowerQuery) ||
                            it.artist.lowercase().contains(lowerQuery) ||
                            it.album.lowercase().contains(lowerQuery)) {
                            convertToTrack(it)
                        } else null
                    }
                }
                
                Result.success(
                    SearchResults(
                        query = query,
                        tracks = tracks,
                        hasMore = false,
                        totalResults = tracks.size
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Search failed", e)
                Result.failure(MusicError.NetworkError(e.message ?: "Search failed"))
            }
        }
    }

    /**
     * Get track details by ID
     */
    override suspend fun getTrackDetails(trackId: String): Result<Track> {
        return withContext(Dispatchers.IO) {
            try {
                // Check local DB first (for liked/downloaded status)
                val localTrack = trackDao.getTrackById(trackId)
                if (localTrack != null && localTrack.isDownloaded && localTrack.localPath != null) {
                    return@withContext Result.success(localTrack.toTrack())
                }
                
                // Fetch from Firestore
                val doc = firestore.collection(TRACKS_COLLECTION)
                    .document(trackId)
                    .get()
                    .await()
                
                val firestoreTrack = doc.toObject(FirestoreTrack::class.java)
                if (firestoreTrack != null) {
                    val track = convertToTrack(firestoreTrack)
                    
                    // Preserve local liked status
                    val finalTrack = if (localTrack != null) {
                        track.copy(
                            isLiked = localTrack.isLiked,
                            isDownloaded = localTrack.isDownloaded
                        )
                    } else track
                    
                    Result.success(finalTrack)
                } else {
                    Result.failure(MusicError.NotFound)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get track details failed", e)
                // Fallback to local if offline
                val localTrack = trackDao.getTrackById(trackId)
                if (localTrack != null) {
                    Result.success(localTrack.toTrack())
                } else {
                    Result.failure(MusicError.NetworkError(e.message ?: "Network error"))
                }
            }
        }
    }

    /**
     * Get album with all tracks
     */
    override suspend fun getAlbum(albumId: String): Result<Album> {
        return withContext(Dispatchers.IO) {
            try {
                val doc = firestore.collection(ALBUMS_COLLECTION)
                    .document(albumId)
                    .get()
                    .await()
                
                val firestoreAlbum = doc.toObject(FirestoreAlbum::class.java)
                if (firestoreAlbum != null) {
                    // Fetch all tracks for this album
                    val tracks = firestoreAlbum.trackIds.mapNotNull { trackId ->
                        getTrackDetails(trackId).getOrNull()
                    }
                    
                    val imageUrl = getStorageUrl(firestoreAlbum.coverPath)
                    val album = firestoreAlbum.toAlbum(tracks, imageUrl)
                    Result.success(album)
                } else {
                    Result.failure(MusicError.NotFound)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Get album failed", e)
                Result.failure(MusicError.NetworkError(e.message ?: "Network error"))
            }
        }
    }

    /**
     * Get trending/featured tracks (all tracks for small library)
     */
    override suspend fun getTrendingTracks(): Result<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Fetching trending tracks from Firestore...")
                val snapshot = firestore.collection(TRACKS_COLLECTION)
                    .limit(20)
                    .get()
                    .await()
                
                Log.d(TAG, "Firestore returned ${snapshot.documents.size} documents")
                
                val tracks = snapshot.documents.mapNotNull { doc ->
                    Log.d(TAG, "Processing doc: ${doc.id}, data: ${doc.data}")
                    doc.toObject(FirestoreTrack::class.java)?.let { 
                        Log.d(TAG, "Parsed FirestoreTrack: title=${it.title}, storagePath=${it.storagePath}")
                        convertToTrack(it) 
                    }
                }
                
                Log.d(TAG, "Converted ${tracks.size} tracks")
                Result.success(tracks)
            } catch (e: Exception) {
                Log.e(TAG, "Get trending failed", e)
                Result.failure(MusicError.NetworkError(e.message ?: "Network error"))
            }
        }
    }

    /**
     * Get new releases (same as trending for now - most recent)
     */
    override suspend fun getNewReleases(): Result<List<Track>> {
        return getTrendingTracks()
    }

    /**
     * Toggle like status for a track
     */
    override suspend fun toggleLike(track: Track): Boolean {
        return withContext(Dispatchers.IO) {
            val current = trackDao.getTrackById(track.id)
            val isLiked = !(current?.isLiked ?: false)
            val entity = current?.copy(isLiked = isLiked) ?: track.toEntity(isLiked = isLiked)
            trackDao.insertTrack(entity)
            isLiked
        }
    }

    /**
     * Observe liked tracks from local DB
     */
    override fun observeLikedTracks(): Flow<List<Track>> {
        return trackDao.getLikedTracks().map { list -> list.map { it.toTrack() } }
    }

    /**
     * Observe downloaded tracks from local DB
     */
    override fun observeDownloadedTracks(): Flow<List<Track>> {
        return trackDao.getDownloadedTracks().map { list -> list.map { it.toTrack() } }
    }

    /**
     * Download track to local storage
     */
    override suspend fun downloadTrack(track: Track): Long {
        return withContext(Dispatchers.IO) {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(track.streamUrl))
                .setTitle(track.title)
                .setDescription(track.artist)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "Thunderplay/${track.title}.mp3")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            
            val id = downloadManager.enqueue(request)
            
            // Mark as downloaded in local DB
            val entity = trackDao.getTrackById(track.id)
            val newEntity = entity?.copy(isDownloaded = true) ?: track.toEntity(isDownloaded = true)
            trackDao.insertTrack(newEntity)
            
            id
        }
    }

    // ============== Helper Functions ==============

    /**
     * Convert Firestore track to domain Track with resolved URLs
     */
    private suspend fun convertToTrack(firestoreTrack: FirestoreTrack): Track {
        val streamUrl = firestoreTrack.streamUrl.ifBlank { getStorageUrl(firestoreTrack.storagePath) }
        val imageUrl = firestoreTrack.imageUrl.ifBlank { getStorageUrl(firestoreTrack.coverPath) }
        return firestoreTrack.toTrack(streamUrl, imageUrl)
    }

    /**
     * Get download URL for a Firebase Storage path
     */
    private suspend fun getStorageUrl(path: String): String {
        return try {
            if (path.isBlank()) return ""
            storage.reference.child(path).downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get storage URL for: $path", e)
            ""
        }
    }
}

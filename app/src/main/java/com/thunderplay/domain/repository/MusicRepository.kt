package com.thunderplay.domain.repository

import com.thunderplay.domain.model.Album
import com.thunderplay.domain.model.SearchResults
import com.thunderplay.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for music data operations
 */
interface MusicRepository {

    /**
     * Search for tracks by query
     */
    suspend fun searchTracks(query: String, page: Int = 1): Result<SearchResults>

    /**
     * Get track details including stream URL
     */
    suspend fun getTrackDetails(trackId: String): Result<Track>

    /**
     * Get album with all tracks
     */
    suspend fun getAlbum(albumId: String): Result<Album>

    /**
     * Get trending tracks for home feed
     */
    suspend fun getTrendingTracks(): Result<List<Track>>

    /**
     * Get new releases
     */
    suspend fun getNewReleases(): Result<List<Track>>

    // Offline & Favorites
    suspend fun toggleLike(track: Track): Boolean
    fun observeLikedTracks(): Flow<List<Track>>
    fun observeDownloadedTracks(): Flow<List<Track>>
    suspend fun downloadTrack(track: Track): Long
}

/**
 * Sealed class for handling results
 */
sealed class MusicError : Exception() {
    data class NetworkError(override val message: String) : MusicError()
    data class ApiError(val code: Int, override val message: String) : MusicError()
    data object NotFound : MusicError()
    data object Unknown : MusicError()
}

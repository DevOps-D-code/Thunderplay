package com.thunderplay.data.mapper

import com.thunderplay.data.firebase.FirestoreAlbum
import com.thunderplay.data.firebase.FirestoreTrack
import com.thunderplay.domain.model.Album
import com.thunderplay.domain.model.Track

/**
 * Mapper to convert Firestore models to domain models
 */
object FirestoreMapper {
    
    /**
     * Convert FirestoreTrack to domain Track
     * @param streamUrl The pre-signed download URL from Firebase Storage
     * @param imageUrl The pre-signed download URL for album art
     */
    fun FirestoreTrack.toTrack(
        streamUrl: String,
        imageUrl: String
    ): Track {
        return Track(
            id = id,
            title = title,
            artist = artist,
            artistId = artistId,
            album = album,
            albumId = albumId,
            duration = duration,
            imageUrl = imageUrl,
            imageUrlHigh = imageUrl, // Same URL, Firebase serves appropriate quality
            streamUrl = streamUrl,
            streamUrlHigh = streamUrl, // Same URL for now
            year = year,
            language = language,
            hasLyrics = hasLyrics,
            isExplicit = isExplicit,
            playCount = playCount,
            isLiked = false,      // Will be merged with local DB
            isDownloaded = false  // Will be merged with local DB
        )
    }
    
    /**
     * Convert FirestoreAlbum to domain Album
     * @param tracks List of tracks for this album
     * @param imageUrl The pre-signed download URL for album art
     */
    fun FirestoreAlbum.toAlbum(
        tracks: List<Track>,
        imageUrl: String
    ): Album {
        return Album(
            id = id,
            name = name,
            artist = artist,
            artistId = artistId,
            imageUrl = imageUrl,
            imageUrlHigh = imageUrl,
            year = year,
            songCount = tracks.size,
            tracks = tracks
        )
    }
}

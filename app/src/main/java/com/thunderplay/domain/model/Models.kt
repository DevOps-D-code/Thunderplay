package com.thunderplay.domain.model

/**
 * Domain model for a music track
 */
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String?,
    val album: String,
    val albumId: String?,
    val duration: Int, // seconds
    val imageUrl: String,
    val imageUrlHigh: String,
    val streamUrl: String,
    val streamUrlHigh: String,
    val year: String?,
    val language: String?,
    val hasLyrics: Boolean,
    val isExplicit: Boolean,
    val playCount: Long?,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false
) {
    val durationFormatted: String
        get() {
            val minutes = duration / 60
            val seconds = duration % 60
            return "%d:%02d".format(minutes, seconds)
        }
}

/**
 * Domain model for an album
 */
data class Album(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String?,
    val imageUrl: String,
    val imageUrlHigh: String,
    val year: String?,
    val songCount: Int,
    val tracks: List<Track>
)

/**
 * Domain model for an artist
 */
data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val followerCount: Long?,
    val isVerified: Boolean
)

/**
 * Domain model for a playlist
 */
data class Playlist(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String,
    val songCount: Int,
    val tracks: List<Track>
)

/**
 * Search results container
 */
data class SearchResults(
    val query: String,
    val tracks: List<Track>,
    val hasMore: Boolean,
    val totalResults: Int
)

/**
 * Home feed content
 */
data class HomeFeed(
    val trending: List<Track>,
    val newReleases: List<Album>,
    val featuredPlaylists: List<Playlist>
)

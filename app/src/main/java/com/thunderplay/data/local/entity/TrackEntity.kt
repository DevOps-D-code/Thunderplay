package com.thunderplay.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thunderplay.domain.model.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val imageUrl: String,
    val imageUrlHigh: String,
    val streamUrl: String, // Online URL
    val localPath: String? = null, // Path if downloaded
    val duration: Int,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val timestamp: Long
)

fun TrackEntity.toTrack(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        artistId = null,
        album = album,
        albumId = null,
        duration = duration,
        imageUrl = imageUrl,
        imageUrlHigh = imageUrlHigh,
        streamUrl = localPath ?: streamUrl,
        streamUrlHigh = localPath ?: streamUrl, // Use local if downloaded
        year = null,
        language = null,
        hasLyrics = false,
        isExplicit = false,
        playCount = null,
        isLiked = isLiked,
        isDownloaded = isDownloaded
    )
}

fun Track.toEntity(isLiked: Boolean = false, isDownloaded: Boolean = false, localPath: String? = null): TrackEntity {
    return TrackEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        imageUrl = imageUrl,
        imageUrlHigh = imageUrlHigh,
        streamUrl = streamUrl,
        localPath = localPath,
        duration = duration,
        isLiked = isLiked,
        isDownloaded = isDownloaded,
        timestamp = System.currentTimeMillis()
    )
}

package com.thunderplay.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Firestore document model for a track
 */
data class FirestoreTrack(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val artistId: String? = null,
    val album: String = "",
    val albumId: String? = null,
    val duration: Int = 0, // seconds
    val storagePath: String = "",  // Firebase Storage path for audio (e.g., "music/song.mp3")
    val coverPath: String = "",    // Firebase Storage path for cover (e.g., "covers/album.jpg")
    val streamUrl: String = "",    // Direct download URL
    val imageUrl: String = "",     // Direct download URL for cover
    val year: String? = null,
    val language: String? = null,
    val hasLyrics: Boolean = false,
    val isExplicit: Boolean = false,
    val playCount: Long = 0,
    val uploadedAt: Timestamp? = null,
    val uploadedBy: String? = null  // User ID of uploader
)

/**
 * Firestore document model for an album
 */
data class FirestoreAlbum(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val artistId: String? = null,
    val coverPath: String = "",
    val year: String? = null,
    val trackIds: List<String> = emptyList(),
    val uploadedAt: Timestamp? = null
)

/**
 * Firestore document model for a playlist
 */
data class FirestorePlaylist(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val coverPath: String = "",
    val trackIds: List<String> = emptyList(),
    val createdBy: String? = null,
    val createdAt: Timestamp? = null
)

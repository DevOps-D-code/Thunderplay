package com.thunderplay.player.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * Media playback service for background audio.
 * Handles ExoPlayer lifecycle and MediaSession for system integration.
 */
class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        
        // Initialize ExoPlayer with audio focus handling
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // Handle audio focus automatically
            )
            .setHandleAudioBecomingNoisy(true) // Pause when headphones disconnected
            .build()
            .apply {
                // Set up player listener
                addListener(playerListener)
            }
        
        // Create MediaSession
        mediaSession = MediaSession.Builder(this, player!!)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        player?.release()
        player = null
        super.onDestroy()
    }

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    // Loading track
                }
                Player.STATE_READY -> {
                    // Ready to play
                }
                Player.STATE_ENDED -> {
                    // Track finished - auto-advance handled by ExoPlayer
                }
                Player.STATE_IDLE -> {
                    // Player idle
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            // Update notification, etc.
        }
    }

    private inner class MediaSessionCallback : MediaSession.Callback {
        // Custom callbacks can be added here for handling specific actions
    }

    companion object {
        /**
         * Create a MediaItem from track data
         */
        fun createMediaItem(
            trackId: String,
            title: String,
            artist: String,
            albumName: String,
            artworkUri: String,
            streamUrl: String
        ): MediaItem {
            return MediaItem.Builder()
                .setMediaId(trackId)
                .setUri(streamUrl)
                .setMimeType(androidx.media3.common.MimeTypes.AUDIO_MPEG)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbumTitle(albumName)
                        .setArtworkUri(android.net.Uri.parse(artworkUri))
                        .build()
                )
                .build()
        }
    }
}

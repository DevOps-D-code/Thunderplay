package com.thunderplay.player.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.thunderplay.domain.model.Track
import com.thunderplay.player.service.PlaybackService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Player state exposed to UI
 */
data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val queue: List<Track> = emptyList(),
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)

enum class RepeatMode {
    OFF, ONE, ALL
}

/**
 * Controller for managing media playback.
 * Acts as bridge between UI and PlaybackService.
 */
@Singleton
class PlayerController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val queue = mutableListOf<Track>()

    /**
     * Connect to the PlaybackService
     */
    fun connect() {
        if (controllerFuture == null) {
            val sessionToken = SessionToken(
                context,
                ComponentName(context, PlaybackService::class.java)
            )
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture?.addListener({
                mediaController = controllerFuture?.get()
                setupPlayerListener()
            }, MoreExecutors.directExecutor())
        }
    }

    /**
     * Disconnect from PlaybackService
     */
    fun disconnect() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
        controllerFuture = null
        mediaController = null
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _playerState.value = _playerState.value.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING
                )
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.mediaId?.let { id ->
                    val track = queue.find { it.id == id }
                    _currentTrack.value = track
                    _playerState.value = _playerState.value.copy(currentTrack = track)
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                android.util.Log.e("PlayerController", "Playback error: ${error.message}", error)
            }
        })
    }

    /**
     * Play a single track
     */
    fun playTrack(track: Track) {
        queue.clear()
        queue.add(track)
        _currentTrack.value = track
        _playerState.value = _playerState.value.copy(currentTrack = track, queue = queue.toList())
        
        val urlToPlay = track.streamUrlHigh.ifEmpty { track.streamUrl }
        android.util.Log.d("PlayerController", "Attempting to play URL: '$urlToPlay'")
        
        val mediaItem = PlaybackService.createMediaItem(
            trackId = track.id,
            title = track.title,
            artist = track.artist,
            albumName = track.album,
            artworkUri = track.imageUrlHigh,
            streamUrl = urlToPlay
        )

        val playCommand = {
            mediaController?.apply {
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        }

        if (mediaController != null) {
            playCommand()
        } else {
            controllerFuture?.addListener({
                playCommand()
            }, MoreExecutors.directExecutor())
        }
    }

    /**
     * Play a list of tracks starting from index
     */
    fun playTracks(tracks: List<Track>, startIndex: Int = 0) {
        queue.clear()
        queue.addAll(tracks)
        _playerState.value = _playerState.value.copy(queue = queue.toList())

        val mediaItems = tracks.map { track ->
            PlaybackService.createMediaItem(
                trackId = track.id,
                title = track.title,
                artist = track.artist,
                albumName = track.album,
                artworkUri = track.imageUrlHigh,
                streamUrl = track.streamUrlHigh.ifEmpty { track.streamUrl }
            )
        }

        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }

        if (tracks.isNotEmpty() && startIndex < tracks.size) {
            _currentTrack.value = tracks[startIndex]
            _playerState.value = _playerState.value.copy(currentTrack = tracks[startIndex])
        }
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        mediaController?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun play() = mediaController?.play()
    fun pause() = mediaController?.pause()
    fun skipNext() = mediaController?.seekToNext()
    fun skipPrevious() = mediaController?.seekToPrevious()
    
    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
    }

    fun toggleShuffle() {
        mediaController?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
            _playerState.value = _playerState.value.copy(shuffleEnabled = it.shuffleModeEnabled)
        }
    }

    fun toggleRepeatMode() {
        mediaController?.let {
            val newMode = when (it.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
            it.repeatMode = newMode
            _playerState.value = _playerState.value.copy(
                repeatMode = when (newMode) {
                    Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                    Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                    else -> RepeatMode.OFF
                }
            )
        }
    }

    /**
     * Get current playback position
     */
    fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L
    fun getDuration(): Long = mediaController?.duration ?: 0L
}

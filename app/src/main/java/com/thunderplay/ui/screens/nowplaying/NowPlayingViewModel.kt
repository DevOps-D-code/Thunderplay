package com.thunderplay.ui.screens.nowplaying

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.thunderplay.domain.model.Track
import com.thunderplay.domain.repository.MusicRepository
import com.thunderplay.player.controller.PlayerController
import com.thunderplay.player.controller.PlayerState
import com.thunderplay.ui.navigation.NowPlayingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val trackId: String = savedStateHandle.toRoute<NowPlayingRoute>().trackId

    private val _track = MutableStateFlow<Track?>(null)
    val track: StateFlow<Track?> = _track.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _uiEvent = kotlinx.coroutines.channels.Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()
    
    val playerState: StateFlow<PlayerState> = playerController.playerState

    var sliderPosition by mutableFloatStateOf(0f)
        private set
    
    var isDragging by mutableStateOf(false)
        private set

    init {
        loadTrackAndPlay()
        startPositionUpdater()
    }

    private fun loadTrackAndPlay() {
        viewModelScope.launch {
            _isLoading.value = true
            
            musicRepository.getTrackDetails(trackId)
                .onSuccess { track ->
                    _track.value = track
                    playerController.connect()
                    playerController.playTrack(track)
                    _isLoading.value = false
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to load track"
                    _isLoading.value = false
                }
        }
    }

    fun retry() {
        loadTrackAndPlay()
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (isActive) {
                if (!isDragging) {
                    val position = playerController.getCurrentPosition()
                    val duration = playerController.getDuration()
                    if (duration > 0) {
                        sliderPosition = position.toFloat() / duration.toFloat()
                    }
                }
                delay(500)
            }
        }
    }

    fun onSliderChange(value: Float) {
        isDragging = true
        sliderPosition = value
    }

    fun onSliderChangeFinished() {
        val duration = playerController.getDuration()
        if (duration > 0) {
            playerController.seekTo((sliderPosition * duration).toLong())
        }
        isDragging = false
    }

    fun togglePlayPause() {
        playerController.togglePlayPause()
    }

    fun skipNext() {
        playerController.skipNext()
    }

    fun skipPrevious() {
        playerController.skipPrevious()
    }

    fun toggleShuffle() {
        playerController.toggleShuffle()
    }

    fun onLikeClick() {
        track.value?.let { currentTrack ->
            viewModelScope.launch {
                val isLiked = musicRepository.toggleLike(currentTrack)
                _track.value = currentTrack.copy(isLiked = isLiked)
                _uiEvent.send(if (isLiked) "Added to Favorites" else "Removed from Favorites")
            }
        }
    }

    fun onDownloadClick() {
        track.value?.let { currentTrack ->
            viewModelScope.launch {
                musicRepository.downloadTrack(currentTrack)
                _uiEvent.send("Download started: ${currentTrack.title}")
            }
        }
    }

    fun toggleRepeat() {
        playerController.toggleRepeatMode()
    }

    override fun onCleared() {
        super.onCleared()
        // Don't disconnect - keep playback going
    }
}

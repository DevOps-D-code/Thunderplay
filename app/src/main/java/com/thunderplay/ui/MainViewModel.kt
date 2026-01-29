package com.thunderplay.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thunderplay.domain.model.Track
import com.thunderplay.player.controller.PlayerController
import com.thunderplay.player.controller.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val playerController: PlayerController
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = playerController.playerState
    
    // Track current playing track
    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    var sliderPosition by mutableFloatStateOf(0f)
        private set

    init {
        // Observe player state to get current track
        viewModelScope.launch {
            playerController.currentTrack.collect { track ->
                _currentTrack.value = track
            }
        }
        
        startPositionUpdater()
    }

    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (isActive) {
                // Ideally only update if playing
                if (playerState.value.isPlaying) {
                     val position = playerController.getCurrentPosition()
                    val duration = playerController.getDuration()
                    if (duration > 0) {
                        sliderPosition = position.toFloat() / duration.toFloat()
                    }
                }
                delay(1000) // Update every second for mini player is enough
            }
        }
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
}

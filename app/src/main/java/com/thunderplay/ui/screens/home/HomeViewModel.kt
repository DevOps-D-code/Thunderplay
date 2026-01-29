package com.thunderplay.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thunderplay.domain.model.Track
import com.thunderplay.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "",
    val trendingTracks: List<Track> = emptyList(),
    val newReleases: List<Track> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateGreeting()
        loadHomeFeed()
    }

    private fun updateGreeting() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..23 -> "Good Evening"
            else -> "Good Night"
        }
        _uiState.update { it.copy(greeting = greeting) }
    }

    fun loadHomeFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Load trending
            musicRepository.getTrendingTracks()
                .onSuccess { tracks ->
                    _uiState.update { it.copy(trendingTracks = tracks.take(10)) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
            
            // Load new releases
            musicRepository.getNewReleases()
                .onSuccess { tracks ->
                    _uiState.update { it.copy(newReleases = tracks.take(10)) }
                }
            
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun refresh() {
        updateGreeting()
        loadHomeFeed()
    }
}

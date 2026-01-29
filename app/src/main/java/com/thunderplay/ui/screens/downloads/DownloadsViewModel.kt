package com.thunderplay.ui.screens.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thunderplay.domain.model.Track
import com.thunderplay.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<Track>>(emptyList())
    val uiState: StateFlow<List<Track>> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeDownloadedTracks().collect { tracks ->
                _uiState.value = tracks
            }
        }
    }
}

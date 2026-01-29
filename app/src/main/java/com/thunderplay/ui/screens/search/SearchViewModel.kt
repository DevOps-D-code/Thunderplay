package com.thunderplay.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thunderplay.domain.model.Track
import com.thunderplay.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false,
    val hasMore: Boolean = false,
    val totalResults: Int = 0
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    
    init {
        // Debounce search queries
        viewModelScope.launch {
            _searchQuery
                .debounce(400)
                .filter { it.length >= 2 }
                .distinctUntilChanged()
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        _searchQuery.value = query
    }

    fun onSearch() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                performSearch(query)
            }
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        musicRepository.searchTracks(query)
            .onSuccess { results ->
                _uiState.update {
                    it.copy(
                        tracks = results.tracks,
                        isLoading = false,
                        hasSearched = true,
                        hasMore = results.hasMore,
                        totalResults = results.totalResults,
                        error = null
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hasSearched = true,
                        error = error.message ?: "Search failed"
                    )
                }
            }
    }

    fun clearSearch() {
        _uiState.update { SearchUiState() }
        _searchQuery.value = ""
    }
}

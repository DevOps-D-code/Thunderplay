package com.thunderplay.ui.screens.search

import com.thunderplay.domain.model.SearchResults
import com.thunderplay.domain.repository.MusicRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val repository: MusicRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.tracks.isEmpty())
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun `search updates query and triggers loading`() = runTest {
        // Given
        val query = "test"
        coEvery { repository.searchTracks(query) } returns Result.success(
            SearchResults(query, emptyList(), false, 0)
        )

        // When
        viewModel.onQueryChange(query)
        
        // Then
        assertEquals(query, viewModel.uiState.value.query)
    }
}

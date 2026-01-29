package com.thunderplay.ui.screens.home

import com.thunderplay.domain.model.Track
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var repository: MusicRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads data and sets greeting`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.getTrendingTracks() } returns Result.success(emptyList())
        coEvery { repository.getNewReleases() } returns Result.success(emptyList())

        // When
        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.greeting)
        assertTrue(state.greeting.isNotEmpty())
    }

    @Test
    fun `loadHomeFeed success updates state`() = runTest(testDispatcher) {
        // Given
        val mockTrack = createTestTrack()
        coEvery { repository.getTrendingTracks() } returns Result.success(listOf(mockTrack))
        coEvery { repository.getNewReleases() } returns Result.success(listOf(mockTrack))

        // When
        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.trendingTracks.size)
        assertEquals(1, state.newReleases.size)
        assertEquals("Song", state.trendingTracks.first().title)
    }

    private fun createTestTrack(): Track {
        return Track(
            id = "1",
            title = "Song",
            artist = "Artist",
            artistId = "1",
            album = "Album",
            albumId = "1",
            duration = 180,
            imageUrl = "url",
            imageUrlHigh = "url2",
            streamUrl = "stream",
            streamUrlHigh = "stream2",
            year = "2024",
            language = "en",
            hasLyrics = false,
            isExplicit = false,
            playCount = 100L
        )
    }

    @Test
    fun `loadHomeFeed failure updates error state`() = runTest(testDispatcher) {
        // Given
        coEvery { repository.getTrendingTracks() } returns Result.failure(Exception("Network error"))
        coEvery { repository.getNewReleases() } returns Result.success(emptyList())

        // When
        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("Network error", state.error)
        assertFalse(state.isLoading)
    }
}

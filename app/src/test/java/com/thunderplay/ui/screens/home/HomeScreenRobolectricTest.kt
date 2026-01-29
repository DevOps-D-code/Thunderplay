package com.thunderplay.ui.screens.home

import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.thunderplay.domain.model.Track
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = "src/main/AndroidManifest.xml")
@org.junit.Ignore("Requires local environment setup with proper Manifest merging for Robolectric")
class HomeScreenRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysStaticContent() {
        ShadowLog.stream = System.out

        val emptyState = HomeUiState(
            isLoading = false,
            error = null,
            trendingTracks = emptyList(),
            newReleases = emptyList()
        )

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = emptyState,
                onNavigateToSearch = {},
                onNavigateToNowPlaying = {},
                onNavigateToLibrary = {}
            )
        }

        // Verify Header
        composeTestRule.onNodeWithText("Thunderplay").assertIsDisplayed()

        // Verify Hero Section
        composeTestRule.onNodeWithText("Discover New Music").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Listening").assertIsDisplayed()

        // Verify Sections
        composeTestRule.onNodeWithText("Quick Play").assertIsDisplayed()
        
        // Since list is empty, Trending/New Releases might show placeholders which have text "Artist Name" or similar
        // Or if empty list in the code shows PlaceholderRow()
        composeTestRule.onNodeWithText("Trending Now").assertIsDisplayed()
        composeTestRule.onNodeWithText("New Releases").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysTracks() {
        val testTrack = Track(
            id = "1",
            title = "Test Song",
            artist = "Test Artist",
            artistId = null,
            album = "Test Album",
            albumId = null,
            duration = 100,
            imageUrl = "",
            imageUrlHigh = "",
            streamUrl = "",
            streamUrlHigh = "",
            year = "2024",
            language = "En",
            hasLyrics = false,
            isExplicit = false,
            playCount = 0
        )

        val populatedState = HomeUiState(
            isLoading = false,
            error = null,
            trendingTracks = listOf(testTrack),
            newReleases = emptyList()
        )

        composeTestRule.setContent {
            HomeScreenContent(
                uiState = populatedState,
                onNavigateToSearch = {},
                onNavigateToNowPlaying = {},
                onNavigateToLibrary = {}
            )
        }

        // Verify track title is displayed
        composeTestRule.onNodeWithText("Test Song").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
    }
}

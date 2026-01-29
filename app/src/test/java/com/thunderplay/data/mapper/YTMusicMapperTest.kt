package com.thunderplay.data.mapper

import com.thunderplay.data.api.yt.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class YTMusicMapperTest {

    @Test
    fun `toTracks parses standard song items correctly`() {
        val songItem = MusicResponsiveListItemRenderer(
            navigationEndpoint = NavigationEndpoint(watchEndpoint = WatchEndpoint(videoId = "song123")),
            flexColumns = listOf(
                FlexColumn(MusicResponsiveListItemFlexColumnRenderer(TextRunContainer(listOf(TextRun("Song Title"))))),
                FlexColumn(MusicResponsiveListItemFlexColumnRenderer(TextRunContainer(listOf(TextRun("Artist Name"), TextRun(" • 3:30")))))
            )
        )
        
        val response = InnertubeResponse(
            contents = Contents(
                singleColumnBrowseResultsRenderer = SingleColumnBrowseResultsRenderer(
                    tabs = listOf(
                        Tab(
                            tabRenderer = TabRenderer(
                                content = TabContent(
                                    sectionListRenderer = SectionListRenderer(
                                        contents = listOf(
                                            SectionContent(
                                                musicShelfRenderer = MusicShelfRenderer(
                                                    contents = listOf(MusicShelfContent(musicResponsiveListItemRenderer = songItem))
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        
        val tracks = response.toTracks()
        assertEquals(1, tracks.size)
        assertEquals("song123", tracks[0].id)
        assertEquals("Song Title", tracks[0].title)
        assertEquals("Artist Name", tracks[0].artist)
    }

    @Test
    fun `toTracks parses TwoRowItemRenderer (Albums) using browseId fallback`() {
        val albumItem = MusicTwoRowItemRenderer(
            navigationEndpoint = NavigationEndpoint(browseEndpoint = BrowseEndpoint(browseId = "MPRE123")),
            title = TextRunContainer(listOf(TextRun("Album Title"))),
            subtitle = TextRunContainer(listOf(TextRun("Artist Name"))),
            thumbnailOverlay = null // No play button
        )
        
         val response = InnertubeResponse(
            contents = Contents(
                singleColumnBrowseResultsRenderer = SingleColumnBrowseResultsRenderer(
                    tabs = listOf(
                        Tab(
                            tabRenderer = TabRenderer(
                                content = TabContent(
                                    sectionListRenderer = SectionListRenderer(
                                        contents = listOf(
                                            SectionContent(
                                                musicCarouselShelfRenderer = MusicCarouselShelfRenderer(
                                                    contents = listOf(
                                                        MusicCarouselShelfContent(musicTwoRowItemRenderer = albumItem)
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        
        val tracks = response.toTracks()
        assertEquals(1, tracks.size)
        assertEquals("MPRE123", tracks[0].id) // Should use browseId
        assertEquals("Album Title", tracks[0].title)
    }

    @Test
    fun `toTracks extracts videoId from Overlay if present`() {
        val albumItemWithPlay = MusicTwoRowItemRenderer(
            navigationEndpoint = NavigationEndpoint(browseEndpoint = BrowseEndpoint(browseId = "MPRE123")),
            title = TextRunContainer(listOf(TextRun("Album Title"))),
            thumbnailOverlay = MusicItemThumbnailOverlayRenderer(
                musicItemThumbnailOverlayRenderer = MusicItemThumbnailOverlayRendererContent(
                    content = MusicPlayButtonRenderer(
                        musicPlayButtonRenderer = MusicPlayButtonRendererContent(
                            playNavigationEndpoint = NavigationEndpoint(watchEndpoint = WatchEndpoint(videoId = "firstSongId"))
                        )
                    )
                )
            )
        )
        
         val response = InnertubeResponse(
            contents = Contents(
                singleColumnBrowseResultsRenderer = SingleColumnBrowseResultsRenderer(
                    tabs = listOf(
                        Tab(
                            tabRenderer = TabRenderer(
                                content = TabContent(
                                    sectionListRenderer = SectionListRenderer(
                                        contents = listOf(
                                            SectionContent(
                                                musicCarouselShelfRenderer = MusicCarouselShelfRenderer(
                                                    contents = listOf(
                                                        MusicCarouselShelfContent(musicTwoRowItemRenderer = albumItemWithPlay)
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        
        val tracks = response.toTracks()
        assertEquals(1, tracks.size)
        assertEquals("firstSongId", tracks[0].id) // Should prioritize videoId from overlay
    }
}

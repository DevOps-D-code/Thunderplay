package com.thunderplay.data.mapper

import com.thunderplay.data.api.model.ImageDto
import com.thunderplay.data.api.model.SongDto
import com.thunderplay.data.mapper.MusicMapper.toTrack
import org.junit.Assert.assertEquals
import org.junit.Test

class MusicMapperTest {

    @Test
    fun `toTrack selects 150x150 image for low quality if available`() {
        // Given
        val songDto = SongDto(
            id = "1",
            name = "Test Song",
            type = "song",
            year = "2023",
            releaseDate = "2023-01-01",
            duration = 300,
            label = "Test Label",
            explicitContent = false,
            playCount = 1000,
            language = "English",
            hasLyrics = false,
            url = "http://test.com",
            copyright = "C",
            album = null,
            artists = null,
            image = listOf(
                ImageDto("500x500", "http://500.jpg"),
                ImageDto("150x150", "http://150.jpg"),
                ImageDto("50x50", "http://50.jpg")
            ),
            downloadUrl = null
        )

        // When
        val track = songDto.toTrack()

        // Then
        assertEquals("http://150.jpg", track.imageUrl)
        assertEquals("http://500.jpg", track.imageUrlHigh)
    }

    @Test
    fun `toTrack falls back to high quality if 150x150 is missing`() {
        // Given
        val songDto = SongDto(
            id = "1",
            name = "Test Song",
            type = "song",
            year = "2023",
            releaseDate = "2023-01-01",
            duration = 300,
            label = "Test Label",
            explicitContent = false,
            playCount = 1000,
            language = "English",
            hasLyrics = false,
            url = "http://test.com",
            copyright = "C",
            album = null,
            artists = null,
            image = listOf(
                ImageDto("500x500", "http://500.jpg"),
                ImageDto("50x50", "http://50.jpg")
            ),
            downloadUrl = null
        )

        // When
        val track = songDto.toTrack()

        // Then
        assertEquals("http://500.jpg", track.imageUrl) // Should use high quality
    }
}

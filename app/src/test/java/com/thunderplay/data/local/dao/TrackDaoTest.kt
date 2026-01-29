package com.thunderplay.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.thunderplay.data.local.AppDatabase
import com.thunderplay.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TrackDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var trackDao: TrackDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        trackDao = database.trackDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetTrack() = runTest {
        val track = TrackEntity(
            id = "1", title = "Test", artist = "Artist", album = "Album",
            imageUrl = "", imageUrlHigh = "", streamUrl = "", duration = 100,
            isLiked = true, timestamp = 0
        )
        trackDao.insertTrack(track)
        val loaded = trackDao.getTrackById("1")
        assertNotNull(loaded)
        assertEquals("Test", loaded?.title)
        assertEquals(true, loaded?.isLiked)
    }

    @Test
    fun getLikedTracks() = runTest {
        val t1 = TrackEntity(
            id = "1", title = "T1", artist = "A1", album = "", imageUrl = "", imageUrlHigh = "", streamUrl = "", duration = 100,
            isLiked = true, timestamp = 1
        )
        val t2 = TrackEntity(
            id = "2", title = "T2", artist = "A2", album = "", imageUrl = "", imageUrlHigh = "", streamUrl = "", duration = 100,
            isLiked = false, timestamp = 2
        )
        trackDao.insertTrack(t1)
        trackDao.insertTrack(t2)

        val liked = trackDao.getLikedTracks().first()
        assertEquals(1, liked.size)
        assertEquals("T1", liked[0].title)
    }
}

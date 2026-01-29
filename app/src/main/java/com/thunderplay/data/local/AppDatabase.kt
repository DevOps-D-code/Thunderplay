package com.thunderplay.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thunderplay.data.local.dao.TrackDao
import com.thunderplay.data.local.entity.TrackEntity

@Database(entities = [TrackEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
}

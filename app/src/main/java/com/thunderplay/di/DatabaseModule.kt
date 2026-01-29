package com.thunderplay.di

import android.content.Context
import androidx.room.Room
import com.thunderplay.data.local.AppDatabase
import com.thunderplay.data.local.dao.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "thunderplay_db"
        ).build()
    }

    @Provides
    fun provideTrackDao(database: AppDatabase): TrackDao {
        return database.trackDao()
    }
}

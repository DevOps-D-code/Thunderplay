package com.thunderplay.di

import com.thunderplay.data.repository.FirebaseRepository
import com.thunderplay.domain.repository.MusicRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        firebaseRepository: FirebaseRepository
    ): MusicRepository
}

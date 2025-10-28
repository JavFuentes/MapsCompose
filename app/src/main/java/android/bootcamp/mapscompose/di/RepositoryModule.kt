package android.bootcamp.mapscompose.di

import android.bootcamp.mapscompose.data.local.dao.MarkerDao
import android.bootcamp.mapscompose.data.repository.MarkerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMarkerRepository(
        markerDao: MarkerDao
    ): MarkerRepository{
        return MarkerRepository(markerDao)
    }
}
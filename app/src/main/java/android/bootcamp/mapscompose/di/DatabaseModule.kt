package android.bootcamp.mapscompose.di

import android.bootcamp.mapscompose.data.local.AppDatabase
import android.bootcamp.mapscompose.data.local.dao.MarkerDao
import android.content.Context
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
    ): AppDatabase{
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideMarkerDao(database: AppDatabase): MarkerDao{
        return database.markerDao()
    }
}
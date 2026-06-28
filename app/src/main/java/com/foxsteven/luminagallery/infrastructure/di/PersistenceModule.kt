package com.foxsteven.luminagallery.infrastructure.di

import android.content.Context
import androidx.room.Room
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
import com.foxsteven.luminagallery.infrastructure.persistence.LuminaDatabase
import com.foxsteven.luminagallery.infrastructure.persistence.SavedCriteriaDao
import com.foxsteven.luminagallery.infrastructure.persistence.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LuminaDatabase {
        return Room.databaseBuilder(
            context,
            LuminaDatabase::class.java,
            "lumina_gallery.db"
        ).build()
    }

    @Provides
    fun provideImageDao(database: LuminaDatabase): ImageDao {
        return database.imageDao()
    }

    @Provides
    fun provideTagDao(database: LuminaDatabase): TagDao {
        return database.tagDao()
    }

    @Provides
    fun provideSavedCriteriaDao(database: LuminaDatabase): SavedCriteriaDao {
        return database.savedCriteriaDao()
    }
}

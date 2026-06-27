package com.foxsteven.luminagallery.infrastructure.persistence

import androidx.room.*
import com.foxsteven.luminagallery.data.model.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Delete
    suspend fun deleteImage(image: ImageEntity)

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getImageById(id: Long): ImageEntity?

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun deleteImageById(id: Long)
}

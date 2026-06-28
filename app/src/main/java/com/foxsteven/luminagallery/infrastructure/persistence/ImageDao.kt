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

    @Query("""
        SELECT i.* FROM images i
        WHERE (:query IS NULL OR i.description LIKE '%' || :query || '%')
        AND (:startDate IS NULL OR i.timestamp >= :startDate)
        AND (:endDate IS NULL OR i.timestamp <= :endDate)
        AND (:tagCount = 0 OR i.id IN (
            SELECT imageId FROM image_tag_cross_ref 
            WHERE tagId IN (:tagIds)
            GROUP BY imageId
            HAVING COUNT(DISTINCT tagId) = :tagCount
        ))
        ORDER BY i.timestamp DESC
    """)
    fun getFilteredImages(
        query: String?,
        startDate: Long?,
        endDate: Long?,
        tagIds: List<Long>,
        tagCount: Int
    ): Flow<List<ImageEntity>>

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun deleteImageById(id: Long)
}

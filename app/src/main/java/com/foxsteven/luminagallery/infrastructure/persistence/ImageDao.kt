package com.foxsteven.luminagallery.infrastructure.persistence

import androidx.room.*
import com.foxsteven.luminagallery.data.model.ImageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)

    @Query("SELECT * FROM images WHERE source = :source AND identifier = :identifier")
    suspend fun getImageByIdentifier(source: String, identifier: UUID): ImageEntity?

    @Query("SELECT * FROM images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<ImageEntity>>

    @Query("""
        SELECT i.* FROM images i
        WHERE (:query IS NULL OR i.description LIKE '%' || :query || '%')
        AND (:startDate IS NULL OR i.timestamp >= :startDate)
        AND (:endDate IS NULL OR i.timestamp <= :endDate)
        AND (:tagCount = 0 OR (i.source, i.identifier) IN (
            SELECT imageSource, imageIdentifier FROM image_tag_cross_ref 
            WHERE tagName IN (:tagNames)
            GROUP BY imageSource, imageIdentifier
            HAVING COUNT(DISTINCT tagName) = :tagCount
        ))
        ORDER BY i.timestamp DESC
    """)
    fun getFilteredImages(
        query: String?,
        startDate: Long?,
        endDate: Long?,
        tagNames: List<String>,
        tagCount: Int
    ): Flow<List<ImageEntity>>

    @Query("DELETE FROM images WHERE source = :source AND identifier = :identifier")
    suspend fun deleteImageByIdentifier(source: String, identifier: UUID)
}

package com.foxsteven.luminagallery.infrastructure.persistence

import androidx.room.*
import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageTagCrossRef(crossRef: ImageTagCrossRef)

    @Delete
    suspend fun deleteImageTagCrossRef(crossRef: ImageTagCrossRef)

    @Query("""
        SELECT tags.* FROM tags 
        INNER JOIN image_tag_cross_ref ON tags.id = image_tag_cross_ref.tagId 
        WHERE image_tag_cross_ref.imageId = :imageId
    """)
    fun getTagsForImage(imageId: Long): Flow<List<TagEntity>>
}

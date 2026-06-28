package com.foxsteven.luminagallery.application

import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.TagEntity
import com.foxsteven.luminagallery.infrastructure.persistence.TagDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagService @Inject constructor(
    private val tagDao: TagDao
) {
    val allTags: Flow<List<TagEntity>> get() = tagDao.getAllTags()

    suspend fun createTag(name: String): Long {
        return tagDao.insertTag(TagEntity(name = name))
    }

    suspend fun deleteTag(tag: TagEntity) {
        tagDao.deleteTag(tag)
    }

    fun getTagsForImage(imageId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagsForImage(imageId)
    }

    suspend fun addTagToImage(imageId: Long, tagId: Long) {
        tagDao.insertImageTagCrossRef(ImageTagCrossRef(imageId, tagId))
    }

    suspend fun removeTagFromImage(imageId: Long, tagId: Long) {
        tagDao.deleteImageTagCrossRef(ImageTagCrossRef(imageId, tagId))
    }
}

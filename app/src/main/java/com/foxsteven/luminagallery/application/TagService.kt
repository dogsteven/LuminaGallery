package com.foxsteven.luminagallery.application

import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.TagEntity
import com.foxsteven.luminagallery.infrastructure.persistence.TagDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagService @Inject constructor(
    private val tagDao: TagDao
) {
    val allTags: Flow<List<TagEntity>> get() = tagDao.getAllTags()

    suspend fun createTag(name: String) {
        tagDao.insertTag(TagEntity(name = name))
    }

    suspend fun deleteTag(tag: TagEntity) {
        tagDao.deleteTag(tag)
    }

    fun getTagsForImage(source: String, identifier: UUID): Flow<List<TagEntity>> {
        return tagDao.getTagsForImage(source, identifier)
    }

    suspend fun addTagToImage(source: String, identifier: UUID, tagName: String) {
        tagDao.insertImageTagCrossRef(ImageTagCrossRef(source, identifier, tagName))
    }

    suspend fun removeTagFromImage(source: String, identifier: UUID, tagName: String) {
        tagDao.deleteImageTagCrossRef(ImageTagCrossRef(source, identifier, tagName))
    }
}

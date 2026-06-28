package com.foxsteven.luminagallery.infrastructure.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.TagEntity
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
import com.foxsteven.luminagallery.infrastructure.persistence.LuminaDatabase
import com.foxsteven.luminagallery.infrastructure.persistence.TagDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TagDaoTest {
    private lateinit var tagDao: TagDao
    private lateinit var imageDao: ImageDao
    private lateinit var db: LuminaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LuminaDatabase::class.java
        ).build()
        tagDao = db.tagDao()
        imageDao = db.imageDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTags() = runBlocking {
        val tag1 = TagEntity(name = "Nature")
        val tag2 = TagEntity(name = "Family")
        tagDao.insertTag(tag1)
        tagDao.insertTag(tag2)

        val allTags = tagDao.getAllTags().first()
        assertEquals(2, allTags.size)
        assertEquals("Family", allTags[0].name) // Ordered by name ASC
        assertEquals("Nature", allTags[1].name)
    }

    @Test
    @Throws(Exception::class)
    fun tagImageAndRetrieve() = runBlocking {
        val image = ImageEntity(
            originalPath = "path",
            thumbnailPath = "thumb",
            description = "Nature Photo",
            timestamp = 0L
        )
        val imageId = imageDao.insertImage(image)

        val tag = TagEntity(name = "Nature")
        val tagId = tagDao.insertTag(tag)

        tagDao.insertImageTagCrossRef(ImageTagCrossRef(imageId, tagId))

        val tagsForImage = tagDao.getTagsForImage(imageId).first()
        assertEquals(1, tagsForImage.size)
        assertEquals("Nature", tagsForImage[0].name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteTagCleansUpCrossRef() = runBlocking {
        val image = ImageEntity(
            originalPath = "path",
            thumbnailPath = "thumb",
            description = "Nature Photo",
            timestamp = 0L
        )
        val imageId = imageDao.insertImage(image)

        val tag = TagEntity(name = "Nature")
        val tagId = tagDao.insertTag(tag)
        val tagObj = TagEntity(id = tagId, name = "Nature")

        tagDao.insertImageTagCrossRef(ImageTagCrossRef(imageId, tagId))

        // Delete the tag
        tagDao.deleteTag(tagObj)

        val tagsForImage = tagDao.getTagsForImage(imageId).first()
        assertTrue(tagsForImage.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun deleteImageCleansUpCrossRef() = runBlocking {
        val image = ImageEntity(
            originalPath = "path",
            thumbnailPath = "thumb",
            description = "Nature Photo",
            timestamp = 0L
        )
        val imageId = imageDao.insertImage(image)
        val imageObj = image.copy(id = imageId)

        val tag = TagEntity(name = "Nature")
        val tagId = tagDao.insertTag(tag)

        tagDao.insertImageTagCrossRef(ImageTagCrossRef(imageId, tagId))

        // Delete the image
        imageDao.deleteImage(imageObj)

        val tagsForImage = tagDao.getTagsForImage(imageId).first()
        assertTrue(tagsForImage.isEmpty())
    }
}

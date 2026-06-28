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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ImageDaoTest {
    private lateinit var imageDao: ImageDao
    private lateinit var tagDao: TagDao
    private lateinit var db: LuminaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LuminaDatabase::class.java
        ).build()
        imageDao = db.imageDao()
        tagDao = db.tagDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetImage() = runBlocking {
        val identifier = UUID.randomUUID()
        val image = ImageEntity(
            source = "LOCAL",
            identifier = identifier,
            originalPath = "path/to/original.jpg",
            thumbnailPath = "path/to/thumb.jpg",
            description = "Test Image",
            timestamp = System.currentTimeMillis()
        )
        imageDao.insertImage(image)
        val retrieved = imageDao.getImageByIdentifier("LOCAL", identifier)
        assertNotNull(retrieved)
        assertEquals(image.description, retrieved?.description)
    }

    @Test
    @Throws(Exception::class)
    fun getAllImages() = runBlocking {
        val image1 = ImageEntity(
            source = "LOCAL",
            identifier = UUID.randomUUID(),
            originalPath = "path/1",
            thumbnailPath = "thumb/1",
            description = "Image 1",
            timestamp = 1000L
        )
        val image2 = ImageEntity(
            source = "LOCAL",
            identifier = UUID.randomUUID(),
            originalPath = "path/2",
            thumbnailPath = "thumb/2",
            description = "Image 2",
            timestamp = 2000L
        )
        imageDao.insertImage(image1)
        imageDao.insertImage(image2)

        val allImages = imageDao.getAllImages().first()
        assertEquals(2, allImages.size)
        // Check order (timestamp DESC)
        assertEquals("Image 2", allImages[0].description)
        assertEquals("Image 1", allImages[1].description)
    }

    @Test
    @Throws(Exception::class)
    fun deleteImage() = runBlocking {
        val identifier = UUID.randomUUID()
        val image = ImageEntity(
            source = "LOCAL",
            identifier = identifier,
            originalPath = "path",
            thumbnailPath = "thumb",
            description = "To Delete",
            timestamp = 0L
        )
        imageDao.insertImage(image)
        imageDao.deleteImageByIdentifier("LOCAL", identifier)
        val retrieved = imageDao.getImageByIdentifier("LOCAL", identifier)
        assertNull(retrieved)
    }

    @Test
    fun filterImagesByDescription() = runBlocking {
        imageDao.insertImage(createImage("Apple", 1000L))
        imageDao.insertImage(createImage("Banana", 2000L))

        val results = imageDao.getFilteredImages("Apple", null, null, emptyList(), 0).first()
        assertEquals(1, results.size)
        assertEquals("Apple", results[0].description)
    }

    @Test
    fun filterImagesByDate() = runBlocking {
        imageDao.insertImage(createImage("Old", 1000L))
        imageDao.insertImage(createImage("Mid", 2000L))
        imageDao.insertImage(createImage("New", 3000L))

        val results = imageDao.getFilteredImages(null, 1500L, 2500L, emptyList(), 0).first()
        assertEquals(1, results.size)
        assertEquals("Mid", results[0].description)
    }

    @Test
    fun filterImagesByTags() = runBlocking {
        val img1 = createImage("Img 1", 1000L)
        val img2 = createImage("Img 2", 2000L)
        imageDao.insertImage(img1)
        imageDao.insertImage(img2)
        
        tagDao.insertTag(TagEntity(name = "Tag 1"))
        tagDao.insertTag(TagEntity(name = "Tag 2"))
        
        tagDao.insertImageTagCrossRef(ImageTagCrossRef(img1.source, img1.identifier, "Tag 1"))
        tagDao.insertImageTagCrossRef(ImageTagCrossRef(img1.source, img1.identifier, "Tag 2"))
        tagDao.insertImageTagCrossRef(ImageTagCrossRef(img2.source, img2.identifier, "Tag 1"))

        // Should find Img 1 (has both Tag 1 and Tag 2)
        val bothTags = imageDao.getFilteredImages(null, null, null, listOf("Tag 1", "Tag 2"), 2).first()
        assertEquals(1, bothTags.size)
        assertEquals("Img 1", bothTags[0].description)

        // Should find both (both have Tag 1)
        val oneTag = imageDao.getFilteredImages(null, null, null, listOf("Tag 1"), 1).first()
        assertEquals(2, oneTag.size)
    }

    private fun createImage(description: String, timestamp: Long) = ImageEntity(
        source = "LOCAL",
        identifier = UUID.randomUUID(),
        originalPath = "path",
        thumbnailPath = "thumb",
        description = description,
        timestamp = timestamp
    )
}

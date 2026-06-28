package com.foxsteven.luminagallery.application

import android.net.Uri
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
import com.foxsteven.luminagallery.infrastructure.persistence.SavedCriteriaDao
import com.foxsteven.luminagallery.infrastructure.storage.FileVaultManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GalleryServiceTest {

    private lateinit var galleryService: GalleryService
    private val fileVaultManager = mockk<FileVaultManager>()
    private val imageDao = mockk<ImageDao>()
    private val savedCriteriaDao = mockk<SavedCriteriaDao>()

    @Before
    fun setup() {
        every { imageDao.getAllImages() } returns mockk()
        every { savedCriteriaDao.getAllSavedCriteria() } returns mockk()
        galleryService = GalleryService(fileVaultManager, imageDao, savedCriteriaDao)
    }

    @Test
    fun `importImage should call vault manager and dao with description`() = runTest {
        // Arrange
        val uri = mockk<Uri>()
        val description = "Test description"
        val originalPath = "vault/image.jpg"
        val thumbPath = "thumbnails/thumb_image.jpg"

        every { fileVaultManager.saveOriginal(uri) } returns originalPath
        every { fileVaultManager.generateThumbnail(originalPath) } returns thumbPath
        coEvery { imageDao.insertImage(any()) } returns 1L

        // Act
        galleryService.importImage(uri, description)

        // Assert
        coVerify { fileVaultManager.saveOriginal(uri) }
        coVerify { fileVaultManager.generateThumbnail(originalPath) }
        coVerify { imageDao.insertImage(match { 
            it.originalPath == originalPath && 
            it.thumbnailPath == thumbPath &&
            it.description == description
        }) }
    }

    @Test
    fun `deleteImage should call vault manager to delete files and dao to delete entry`() = runTest {
        // Arrange
        val image = ImageEntity(
            id = 1,
            originalPath = "vault/1.jpg",
            thumbnailPath = "thumbnails/thumb_1.jpg",
            description = "Test",
            timestamp = 1000L
        )
        every { fileVaultManager.deleteFile(any()) } returns Unit
        coEvery { imageDao.deleteImage(any()) } returns Unit

        // Act
        galleryService.deleteImage(image)

        // Assert
        coVerify { fileVaultManager.deleteFile(image.originalPath) }
        coVerify { fileVaultManager.deleteFile(image.thumbnailPath) }
        coVerify { imageDao.deleteImage(image) }
    }
}

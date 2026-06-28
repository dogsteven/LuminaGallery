package com.foxsteven.luminagallery.application

import android.net.Uri
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
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

    @Before
    fun setup() {
        every { imageDao.getAllImages() } returns mockk()
        galleryService = GalleryService(fileVaultManager, imageDao)
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
}

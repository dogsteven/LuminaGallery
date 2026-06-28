package com.foxsteven.luminagallery.infrastructure.storage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
class FileVaultManagerTest {

    private lateinit var context: Context
    private lateinit var fileVaultManager: FileVaultManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        fileVaultManager = FileVaultManager(context)
    }

    @Test
    fun `saveOriginal should copy file to vault`() {
        // Arrange
        val tempFile = File(context.cacheDir, "test_image.jpg")
        FileOutputStream(tempFile).use { 
            Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        val uri = Uri.fromFile(tempFile)

        // Act
        val relativePath = fileVaultManager.saveOriginal(uri)

        // Assert
        assertTrue(relativePath.startsWith("vault/"))
        val savedFile = fileVaultManager.getAbsoluteFile(relativePath)
        assertTrue(savedFile.exists())
        assertTrue(savedFile.length() > 0)
    }

    @Test
    fun `generateThumbnail should create a smaller image with unique name`() {
        // Arrange
        val tempFile = File(context.cacheDir, "test_image_2.jpg")
        FileOutputStream(tempFile).use { 
            Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        val uri = Uri.fromFile(tempFile)
        val originalRelativePath = fileVaultManager.saveOriginal(uri)

        // Act
        val thumbRelativePath1 = fileVaultManager.generateThumbnail(originalRelativePath)
        val thumbRelativePath2 = fileVaultManager.generateThumbnail(originalRelativePath)

        // Assert
        assertTrue(thumbRelativePath1.startsWith("thumbnails/"))
        assertTrue(thumbRelativePath2.startsWith("thumbnails/"))
        assertTrue(thumbRelativePath1 != thumbRelativePath2)
        
        val thumbFile1 = fileVaultManager.getAbsoluteFile(thumbRelativePath1)
        val thumbFile2 = fileVaultManager.getAbsoluteFile(thumbRelativePath2)
        assertTrue(thumbFile1.exists())
        assertTrue(thumbFile2.exists())
    }

    @Test
    fun `deleteFile should remove file from storage`() {
        // Arrange
        val tempFile = File(context.cacheDir, "test_image_3.jpg")
        tempFile.createNewFile()
        val uri = Uri.fromFile(tempFile)
        val relativePath = fileVaultManager.saveOriginal(uri)
        assertTrue(fileVaultManager.getAbsoluteFile(relativePath).exists())

        // Act
        fileVaultManager.deleteFile(relativePath)

        // Assert
        assertTrue(!fileVaultManager.getAbsoluteFile(relativePath).exists())
    }
}

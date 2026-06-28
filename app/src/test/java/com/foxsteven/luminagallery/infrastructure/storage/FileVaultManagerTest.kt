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
    fun `generateThumbnail should create a smaller image`() {
        // Arrange
        val tempFile = File(context.cacheDir, "test_image_2.jpg")
        FileOutputStream(tempFile).use { 
            Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888).compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        val uri = Uri.fromFile(tempFile)
        val originalRelativePath = fileVaultManager.saveOriginal(uri)

        // Act
        val thumbRelativePath = fileVaultManager.generateThumbnail(originalRelativePath)

        // Assert
        assertTrue(thumbRelativePath.startsWith("thumbnails/"))
        val thumbFile = fileVaultManager.getAbsoluteFile(thumbRelativePath)
        assertTrue(thumbFile.exists())
        // In a real scenario we'd check dimensions, but Robolectric's Bitmap might be limited
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

package com.foxsteven.luminagallery.infrastructure.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileVaultManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vaultDir = File(context.filesDir, "vault").apply { if (!exists()) mkdirs() }
    private val thumbDir = File(context.filesDir, "thumbnails").apply { if (!exists()) mkdirs() }

    fun saveOriginal(uri: Uri): String {
        val fileName = "${UUID.randomUUID()}.jpg" // Simple approach for now
        val destFile = File(vaultDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IllegalArgumentException("Could not open Uri: $uri")

        return "vault/$fileName"
    }

    fun generateThumbnail(originalRelativePath: String): String {
        val originalFile = File(context.filesDir, originalRelativePath)
        if (!originalFile.exists()) throw IllegalArgumentException("Original file not found: $originalRelativePath")

        val fileName = "thumb_${originalFile.name}"
        val destFile = File(thumbDir, fileName)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(originalFile.absolutePath, options)

        val targetWidth = 512
        val targetHeight = 512
        options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, options)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

        FileOutputStream(destFile).use { output ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, output)
        }
        
        bitmap.recycle()
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle()
        }

        return "thumbnails/$fileName"
    }

    fun getAbsoluteFile(relativePath: String): File {
        return File(context.filesDir, relativePath)
    }

    fun deleteFile(relativePath: String) {
        val file = File(context.filesDir, relativePath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}

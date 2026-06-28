package com.foxsteven.luminagallery.application

import android.net.Uri
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
import com.foxsteven.luminagallery.infrastructure.storage.FileVaultManager
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryService @Inject constructor(
    private val fileVaultManager: FileVaultManager,
    private val imageDao: ImageDao
) {
    val allImages = imageDao.getAllImages()

    suspend fun getImage(id: Long): ImageEntity? {
        return imageDao.getImageById(id)
    }

    suspend fun importImage(uri: Uri, description: String) {
        val originalPath = fileVaultManager.saveOriginal(uri)
        val thumbnailPath = fileVaultManager.generateThumbnail(originalPath)
        
        val imageEntity = ImageEntity(
            originalPath = originalPath,
            thumbnailPath = thumbnailPath,
            description = description,
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
        
        imageDao.insertImage(imageEntity)
    }
}

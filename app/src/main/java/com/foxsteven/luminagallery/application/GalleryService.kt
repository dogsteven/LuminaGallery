package com.foxsteven.luminagallery.application

import android.net.Uri
import com.foxsteven.luminagallery.data.model.FilterCriteria
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import com.foxsteven.luminagallery.infrastructure.persistence.ImageDao
import com.foxsteven.luminagallery.infrastructure.persistence.SavedCriteriaDao
import com.foxsteven.luminagallery.infrastructure.storage.FileVaultManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GalleryService @Inject constructor(
    private val fileVaultManager: FileVaultManager,
    private val imageDao: ImageDao,
    private val savedCriteriaDao: SavedCriteriaDao
) {
    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria = _filterCriteria.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val allImages: Flow<List<ImageEntity>> = _filterCriteria.flatMapLatest { criteria ->
        if (criteria.isEmpty()) {
            imageDao.getAllImages()
        } else {
            imageDao.getFilteredImages(
                query = criteria.query?.takeIf { it.isNotBlank() },
                startDate = criteria.startDate,
                endDate = criteria.endDate,
                tagIds = criteria.tagIds.toList(),
                tagCount = criteria.tagIds.size
            )
        }
    }

    val savedCriteria: Flow<List<SavedCriteriaEntity>> = savedCriteriaDao.getAllSavedCriteria()

    suspend fun setFilter(criteria: FilterCriteria) {
        _filterCriteria.value = criteria
    }

    suspend fun clearFilter() {
        _filterCriteria.value = FilterCriteria()
    }

    suspend fun saveCriteria(name: String) {
        val criteria = _filterCriteria.value
        val entity = SavedCriteriaEntity(
            name = name,
            query = criteria.query ?: "",
            startDate = criteria.startDate,
            endDate = criteria.endDate,
            tagIds = criteria.tagIds.toList()
        )
        savedCriteriaDao.insertSavedCriteria(entity)
    }

    suspend fun deleteSavedCriteria(criteria: SavedCriteriaEntity) {
        savedCriteriaDao.deleteSavedCriteria(criteria)
    }

    suspend fun applySavedCriteria(criteria: SavedCriteriaEntity) {
        _filterCriteria.value = FilterCriteria(
            query = criteria.query.takeIf { it.isNotBlank() },
            startDate = criteria.startDate,
            endDate = criteria.endDate,
            tagIds = criteria.tagIds.toSet()
        )
    }

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

    suspend fun deleteImage(image: ImageEntity) {
        fileVaultManager.deleteFile(image.originalPath)
        fileVaultManager.deleteFile(image.thumbnailPath)
        imageDao.deleteImage(image)
    }
}

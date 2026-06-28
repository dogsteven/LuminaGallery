package com.foxsteven.luminagallery.presentation.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.FilterCriteria
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import com.foxsteven.luminagallery.data.model.TagEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryService: GalleryService,
    private val tagService: TagService
) : ViewModel() {

    val uiState: StateFlow<GalleryUiState> = combine(
        galleryService.allImages,
        galleryService.filterCriteria
    ) { images, filter ->
        if (images.isEmpty()) {
            if (filter.isEmpty()) GalleryUiState.Empty else GalleryUiState.NoResults
        } else {
            GalleryUiState.Success(images)
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GalleryUiState.Loading
    )

    val filterCriteria: StateFlow<FilterCriteria> = galleryService.filterCriteria
    val savedCriteria: StateFlow<List<SavedCriteriaEntity>> = galleryService.savedCriteria
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val allTags: StateFlow<List<TagEntity>> = tagService.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isPickerMode = MutableStateFlow(value = false)
    val isPickerMode: StateFlow<Boolean> = _isPickerMode.asStateFlow()

    private val _pendingImportUri = MutableStateFlow<Uri?>(null)
    val pendingImportUri: StateFlow<Uri?> = _pendingImportUri.asStateFlow()

    fun onImagePicked(uri: Uri) {
        _pendingImportUri.value = uri
    }

    fun onImportConfirm(description: String) {
        val uri = _pendingImportUri.value ?: return
        viewModelScope.launch {
            galleryService.importImage(uri, description)
            _pendingImportUri.value = null
        }
    }

    fun onImportCancel() {
        _pendingImportUri.value = null
    }

    fun setPickerMode(enabled: Boolean) {
        _isPickerMode.value = enabled
    }

    fun onFilterUpdate(criteria: FilterCriteria) {
        viewModelScope.launch {
            galleryService.setFilter(criteria)
        }
    }

    fun onFilterClear() {
        viewModelScope.launch {
            galleryService.clearFilter()
        }
    }

    fun onSaveCriteria(name: String) {
        viewModelScope.launch {
            galleryService.saveCriteria(name)
        }
    }

    fun onDeleteSavedCriteria(criteria: SavedCriteriaEntity) {
        viewModelScope.launch {
            galleryService.deleteSavedCriteria(criteria)
        }
    }

    fun onApplySavedCriteria(criteria: SavedCriteriaEntity) {
        viewModelScope.launch {
            galleryService.applySavedCriteria(criteria)
        }
    }
}

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Empty : GalleryUiState
    data object NoResults : GalleryUiState
    data class Success(val images: List<ImageEntity>) : GalleryUiState
}

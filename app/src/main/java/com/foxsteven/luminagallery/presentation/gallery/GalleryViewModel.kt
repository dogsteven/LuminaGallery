package com.foxsteven.luminagallery.presentation.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.data.model.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val galleryService: GalleryService
) : ViewModel() {

    val uiState: StateFlow<GalleryUiState> = galleryService.allImages
        .map { images ->
            if (images.isEmpty()) {
                GalleryUiState.Empty
            } else {
                GalleryUiState.Success(images)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GalleryUiState.Loading
        )

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
}

sealed interface GalleryUiState {
    data object Loading : GalleryUiState
    data object Empty : GalleryUiState
    data class Success(val images: List<ImageEntity>) : GalleryUiState
}

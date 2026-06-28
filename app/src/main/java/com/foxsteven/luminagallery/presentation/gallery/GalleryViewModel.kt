package com.foxsteven.luminagallery.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.data.model.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
}

sealed interface GalleryUiState {
    object Loading : GalleryUiState
    object Empty : GalleryUiState
    data class Success(val images: List<ImageEntity>) : GalleryUiState
}

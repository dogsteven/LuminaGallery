package com.foxsteven.luminagallery.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.presentation.navigation.ImageDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ImageDetailUiState {
    object Loading : ImageDetailUiState
    data class Success(val image: ImageEntity) : ImageDetailUiState
    object Error : ImageDetailUiState
}

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val galleryService: GalleryService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ImageDetailRoute>()
    private val imageId = route.imageId

    private val _uiState = MutableStateFlow<ImageDetailUiState>(ImageDetailUiState.Loading)
    val uiState: StateFlow<ImageDetailUiState> = _uiState.asStateFlow()

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            _uiState.value = ImageDetailUiState.Loading
            val image = galleryService.getImage(imageId)
            if (image != null) {
                _uiState.value = ImageDetailUiState.Success(image)
            } else {
                _uiState.value = ImageDetailUiState.Error
            }
        }
    }
}

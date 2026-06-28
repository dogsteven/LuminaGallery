package com.foxsteven.luminagallery.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.data.model.TagEntity
import com.foxsteven.luminagallery.presentation.navigation.ImageDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ImageDetailUiState {
    object Loading : ImageDetailUiState
    data class Success(
        val image: ImageEntity,
        val assignedTags: List<TagEntity>,
        val availableTags: List<TagEntity>
    ) : ImageDetailUiState
    object Error : ImageDetailUiState
}

@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val galleryService: GalleryService,
    private val tagService: TagService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ImageDetailRoute>()
    private val imageId = route.imageId

    private val _image = MutableStateFlow<ImageEntity?>(null)
    private val _isError = MutableStateFlow(false)
    private val _assignedTags = tagService.getTagsForImage(imageId)
    private val _allTags = tagService.allTags

    val uiState: StateFlow<ImageDetailUiState> = combine(_image, _isError, _assignedTags, _allTags) { image, isError, assigned, all ->
        when {
            isError -> ImageDetailUiState.Error
            image != null -> ImageDetailUiState.Success(
                image = image,
                assignedTags = assigned,
                availableTags = all.filter { tag -> assigned.none { it.id == tag.id } }
            )
            else -> ImageDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageDetailUiState.Loading
    )

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            val image = galleryService.getImage(imageId)
            if (image != null) {
                _image.value = image
            } else {
                _isError.value = true
            }
        }
    }

    fun addTag(tagId: Long) {
        viewModelScope.launch {
            tagService.addTagToImage(imageId, tagId)
        }
    }

    fun removeTag(tagId: Long) {
        viewModelScope.launch {
            tagService.removeTagFromImage(imageId, tagId)
        }
    }

    fun deleteImage(onComplete: () -> Unit) {
        val image = _image.value ?: return
        viewModelScope.launch {
            galleryService.deleteImage(image)
            onComplete()
        }
    }
}

package com.foxsteven.luminagallery.presentation.detail

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
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
import java.io.File
import java.util.UUID
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
    private val source = route.source
    private val identifier = UUID.fromString(route.identifier)

    private val _image = MutableStateFlow<ImageEntity?>(null)
    private val _isError = MutableStateFlow(value = false)
    private val _assignedTags = tagService.getTagsForImage(source, identifier)
    private val _allTags = tagService.allTags
    private val _rotation = MutableStateFlow(0f)

    val uiState: StateFlow<ImageDetailUiState> = combine(_image, _isError, _assignedTags, _allTags) { image, isError, assigned, all ->
        when {
            isError -> ImageDetailUiState.Error
            image != null -> ImageDetailUiState.Success(
                image = image,
                assignedTags = assigned,
                availableTags = all.filter { tag -> assigned.none { it.name == tag.name } }
            )
            else -> ImageDetailUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageDetailUiState.Loading
    )

    val rotation = _rotation.asStateFlow()

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            val image = galleryService.getImage(source, identifier)
            if (image != null) {
                _image.value = image
            } else {
                _isError.value = true
            }
        }
    }

    fun addTag(tagName: String) {
        viewModelScope.launch {
            tagService.addTagToImage(source, identifier, tagName)
        }
    }

    fun removeTag(tagName: String) {
        viewModelScope.launch {
            tagService.removeTagFromImage(source, identifier, tagName)
        }
    }

    fun rotateImage() {
        _rotation.value = (_rotation.value + 90f) % 360f
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            galleryService.updateImageDescription(source, identifier, description)
            _image.value = _image.value?.copy(description = description)
        }
    }

    fun deleteImage(onComplete: () -> Unit) {
        val image = _image.value ?: return
        viewModelScope.launch {
            galleryService.deleteImage(image)
            onComplete()
        }
    }

    fun shareImage(context: Context) {
        val image = _image.value ?: return
        val file = File(context.filesDir, image.originalPath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Image"))
    }
}

package com.foxsteven.luminagallery.presentation.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.TagEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TagUiState {
    object Loading : TagUiState
    data class Success(val tags: List<TagEntity>) : TagUiState
}

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagService: TagService
) : ViewModel() {

    val uiState: StateFlow<TagUiState> = tagService.allTags
        .map { tags -> TagUiState.Success(tags) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TagUiState.Loading
        )

    fun addTag(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            tagService.createTag(name)
        }
    }

    fun deleteTag(tag: TagEntity) {
        viewModelScope.launch {
            tagService.deleteTag(tag)
        }
    }
}

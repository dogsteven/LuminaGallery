package com.foxsteven.luminagallery.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.presentation.navigation.ImageDetailRoute
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ImageDetailViewModelTest {

    private lateinit var galleryService: GalleryService
    private lateinit var tagService: TagService
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        galleryService = mockk()
        tagService = mockk()
        savedStateHandle = mockk()
        
        // Mock the toRoute extension function
        mockkStatic("androidx.navigation.SavedStateHandleKt")
        every { savedStateHandle.toRoute<ImageDetailRoute>() } returns ImageDetailRoute(1L)
        
        // Use flows that don't emit immediately to test Loading state
        every { tagService.getTagsForImage(1L) } returns flowOf(emptyList())
        every { tagService.allTags } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should set state to Success when image is found`() = runTest {
        val image = ImageEntity(id = 1L, originalPath = "p1", thumbnailPath = "t1", description = "", timestamp = 0)
        coEvery { galleryService.getImage(1L) } returns image

        val viewModel = ImageDetailViewModel(galleryService, tagService, savedStateHandle)
        
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ImageDetailUiState.Success)
        val state = viewModel.uiState.value as ImageDetailUiState.Success
        assertTrue(state.image.id == 1L)
    }

    @Test
    fun `init should set state to Error when image is not found`() = runTest {
        coEvery { galleryService.getImage(1L) } returns null

        val viewModel = ImageDetailViewModel(galleryService, tagService, savedStateHandle)
        
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ImageDetailUiState.Error)
    }
}

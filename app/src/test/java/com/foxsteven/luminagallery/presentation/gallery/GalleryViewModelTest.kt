package com.foxsteven.luminagallery.presentation.gallery

import android.net.Uri
import com.foxsteven.luminagallery.application.GalleryService
import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.FilterCriteria
import com.foxsteven.luminagallery.data.model.ImageEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModelTest {

    private lateinit var viewModel: GalleryViewModel
    private val galleryService = mockk<GalleryService>()
    private val tagService = mockk<TagService>()
    private val imagesFlow = MutableStateFlow<List<ImageEntity>>(emptyList())
    private val filterFlow = MutableStateFlow(FilterCriteria())
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { galleryService.allImages } returns imagesFlow
        every { galleryService.filterCriteria } returns filterFlow
        every { galleryService.savedCriteria } returns MutableStateFlow(emptyList())
        every { tagService.allTags } returns MutableStateFlow(emptyList())
        viewModel = GalleryViewModel(galleryService, tagService)
    }

    @Test
    fun `initial state should be Empty`() = runTest {
        advanceUntilIdle()
        assertEquals(GalleryUiState.Empty, viewModel.uiState.value)
        assertNull(viewModel.pendingImportUri.value)
    }

    @Test
    fun `empty list with active filter should result in NoResults state`() = runTest {
        imagesFlow.value = emptyList()
        filterFlow.value = FilterCriteria(query = "something")
        advanceUntilIdle()
        assertEquals(GalleryUiState.NoResults, viewModel.uiState.value)
    }

    @Test
    fun `onFilterClear should call service clearFilter`() = runTest {
        coEvery { galleryService.clearFilter() } returns Unit
        viewModel.onFilterClear()
        advanceUntilIdle()
        coVerify { galleryService.clearFilter() }
    }

    @Test
    fun `images from service should result in Success state`() = runTest {
        val images = listOf(
            ImageEntity(id = 1, originalPath = "p1", thumbnailPath = "t1", description = "", timestamp = 0)
        )
        imagesFlow.value = images
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertTrue(state is GalleryUiState.Success)
        assertEquals(images, (state as GalleryUiState.Success).images)
    }

    @Test
    fun `onImagePicked should set pendingImportUri`() = runTest {
        val uri = mockk<Uri>()
        viewModel.onImagePicked(uri)
        assertEquals(uri, viewModel.pendingImportUri.value)
    }

    @Test
    fun `onImportCancel should clear pendingImportUri`() = runTest {
        val uri = mockk<Uri>()
        viewModel.onImagePicked(uri)
        viewModel.onImportCancel()
        assertNull(viewModel.pendingImportUri.value)
    }

    @Test
    fun `onImportConfirm should call service and clear pendingImportUri`() = runTest {
        val uri = mockk<Uri>()
        val description = "Test description"
        coEvery { galleryService.importImage(uri, description) } returns Unit
        
        viewModel.onImagePicked(uri)
        viewModel.onImportConfirm(description)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { galleryService.importImage(uri, description) }
        assertNull(viewModel.pendingImportUri.value)
    }
}

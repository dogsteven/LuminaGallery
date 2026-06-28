package com.foxsteven.luminagallery.presentation.gallery

import android.net.Uri
import com.foxsteven.luminagallery.application.GalleryService
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
    private val imagesFlow = MutableStateFlow<List<ImageEntity>>(emptyList())
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { galleryService.allImages } returns imagesFlow
        viewModel = GalleryViewModel(galleryService)
    }

    @Test
    fun `initial state should be Empty`() = runTest {
        assertEquals(GalleryUiState.Empty, viewModel.uiState.value)
        assertNull(viewModel.pendingImportUri.value)
    }

    @Test
    fun `empty list from service should result in Empty state`() = runTest {
        imagesFlow.value = emptyList()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(GalleryUiState.Empty, viewModel.uiState.value)
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

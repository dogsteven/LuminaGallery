package com.foxsteven.luminagallery.presentation.tags

import com.foxsteven.luminagallery.application.TagService
import com.foxsteven.luminagallery.data.model.TagEntity
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TagViewModelTest {

    private lateinit var tagService: TagService
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tagService = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should set state to Success with tags`() = runTest {
        val tagsFlow = MutableSharedFlow<List<TagEntity>>(replay = 1)
        every { tagService.allTags } returns tagsFlow

        val viewModel = TagViewModel(tagService)
        
        // Use backgroundScope to keep the StateFlow active
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { 
            viewModel.uiState.collect {} 
        }
        
        tagsFlow.emit(listOf(TagEntity(name = "Family")))
        runCurrent()
        
        assertTrue(viewModel.uiState.value is TagUiState.Success)
        val state = viewModel.uiState.value as TagUiState.Success
        assertTrue(state.tags.first().name == "Family")
    }

    @Test
    fun `addTag should call service createTag`() = runTest {
        every { tagService.allTags } returns flowOf(emptyList())
        coEvery { tagService.createTag("New") } returns Unit

        val viewModel = TagViewModel(tagService)
        viewModel.addTag("New")
        
        advanceUntilIdle()
        
        coEvery { tagService.createTag("New") }
    }
}

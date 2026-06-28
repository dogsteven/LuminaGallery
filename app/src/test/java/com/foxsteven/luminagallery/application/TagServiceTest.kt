package com.foxsteven.luminagallery.application

import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.TagEntity
import com.foxsteven.luminagallery.infrastructure.persistence.TagDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.util.UUID

class TagServiceTest {

    private lateinit var tagDao: TagDao
    private lateinit var tagService: TagService

    @Before
    fun setup() {
        tagDao = mockk()
        tagService = TagService(tagDao)
    }

    @Test
    fun `allTags should return flow from dao`() = runTest {
        val tags = listOf(TagEntity(name = "Test"))
        every { tagDao.getAllTags() } returns flowOf(tags)

        tagService.allTags.collect {
            assert(it == tags)
        }
    }

    @Test
    fun `createTag should call dao insert`() = runTest {
        coEvery { tagDao.insertTag(any()) } returns Unit
        tagService.createTag("New Tag")
        coVerify { tagDao.insertTag(match { it.name == "New Tag" }) }
    }

    @Test
    fun `deleteTag should call dao delete`() = runTest {
        val tag = TagEntity(name = "Test")
        coEvery { tagDao.deleteTag(tag) } returns Unit
        tagService.deleteTag(tag)
        coVerify { tagDao.deleteTag(tag) }
    }

    @Test
    fun `addTagToImage should call dao insertCrossRef`() = runTest {
        val identifier = UUID.randomUUID()
        val source = "LOCAL"
        val tagName = "TestTag"
        coEvery { tagDao.insertImageTagCrossRef(any()) } returns Unit
        tagService.addTagToImage(source, identifier, tagName)
        coVerify { tagDao.insertImageTagCrossRef(ImageTagCrossRef(source, identifier, tagName)) }
    }
}

package com.foxsteven.luminagallery.infrastructure.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import com.foxsteven.luminagallery.infrastructure.persistence.LuminaDatabase
import com.foxsteven.luminagallery.infrastructure.persistence.SavedCriteriaDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SavedCriteriaDaoTest {
    private lateinit var savedCriteriaDao: SavedCriteriaDao
    private lateinit var db: LuminaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, LuminaDatabase::class.java
        ).build()
        savedCriteriaDao = db.savedCriteriaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetSavedCriteria() = runBlocking {
        val criteria = SavedCriteriaEntity(
            code = "filter_1",
            name = "My Filter",
            query = "search",
            startDate = 100L,
            endDate = 200L,
            tagNames = listOf("Nature", "Family")
        )
        savedCriteriaDao.insertSavedCriteria(criteria)
        val retrieved = savedCriteriaDao.getSavedCriteriaByCode("filter_1")
        assertNotNull(retrieved)
        assertEquals(criteria.name, retrieved?.name)
        assertEquals(criteria.tagNames, retrieved?.tagNames)
    }

    @Test
    @Throws(Exception::class)
    fun getAllSavedCriteria() = runBlocking {
        val criteria1 = SavedCriteriaEntity(code = "b", name = "B Filter", query = "", tagNames = emptyList())
        val criteria2 = SavedCriteriaEntity(code = "a", name = "A Filter", query = "", tagNames = emptyList())
        
        savedCriteriaDao.insertSavedCriteria(criteria1)
        savedCriteriaDao.insertSavedCriteria(criteria2)

        val all = savedCriteriaDao.getAllSavedCriteria().first()
        assertEquals(2, all.size)
        // Check alphabetical order
        assertEquals("A Filter", all[0].name)
        assertEquals("B Filter", all[1].name)
    }

    @Test
    @Throws(Exception::class)
    fun deleteSavedCriteria() = runBlocking {
        val criteria = SavedCriteriaEntity(code = "del", name = "To Delete", query = "", tagNames = emptyList())
        savedCriteriaDao.insertSavedCriteria(criteria)
        val entity = savedCriteriaDao.getSavedCriteriaByCode("del")!!
        
        savedCriteriaDao.deleteSavedCriteria(entity)
        val retrieved = savedCriteriaDao.getSavedCriteriaByCode("del")
        assertNull(retrieved)
    }
}

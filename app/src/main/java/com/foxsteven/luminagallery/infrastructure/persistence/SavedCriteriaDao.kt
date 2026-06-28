package com.foxsteven.luminagallery.infrastructure.persistence

import androidx.room.*
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCriteriaDao {
    @Query("SELECT * FROM saved_criteria ORDER BY name ASC")
    fun getAllSavedCriteria(): Flow<List<SavedCriteriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCriteria(criteria: SavedCriteriaEntity): Long

    @Delete
    suspend fun deleteSavedCriteria(criteria: SavedCriteriaEntity)
    
    @Query("SELECT * FROM saved_criteria WHERE id = :id")
    suspend fun getSavedCriteriaById(id: Long): SavedCriteriaEntity?
}

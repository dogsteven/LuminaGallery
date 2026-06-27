package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_criteria")
data class SavedCriteriaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val query: String,
    val startDate: Long?,
    val endDate: Long?,
    val tagIds: List<Long>
)

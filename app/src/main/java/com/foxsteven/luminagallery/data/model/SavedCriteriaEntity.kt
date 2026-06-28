package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_criteria")
data class SavedCriteriaEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val query: String,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val tagNames: List<String>
)

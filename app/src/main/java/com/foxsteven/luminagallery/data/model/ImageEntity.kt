package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalPath: String,
    val thumbnailPath: String,
    val description: String,
    val timestamp: Long
)

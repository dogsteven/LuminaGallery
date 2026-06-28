package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import java.util.UUID

@Entity(
    tableName = "images",
    primaryKeys = ["source", "identifier"]
)
data class ImageEntity(
    val source: String,
    val identifier: UUID,
    val originalPath: String,
    val thumbnailPath: String,
    val description: String,
    val timestamp: Long
)

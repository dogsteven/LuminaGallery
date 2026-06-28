package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

@Entity(
    tableName = "image_tag_cross_ref",
    primaryKeys = ["imageSource", "imageIdentifier", "tagName"],
    foreignKeys = [
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["source", "identifier"],
            childColumns = ["imageSource", "imageIdentifier"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["name"],
            childColumns = ["tagName"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["imageSource", "imageIdentifier"]),
        Index(value = ["tagName"])
    ]
)
data class ImageTagCrossRef(
    val imageSource: String,
    val imageIdentifier: UUID,
    val tagName: String
)

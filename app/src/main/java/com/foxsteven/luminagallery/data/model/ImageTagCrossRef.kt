package com.foxsteven.luminagallery.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "image_tag_cross_ref",
    primaryKeys = ["imageId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = ImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["imageId"]),
        Index(value = ["tagId"])
    ]
)
data class ImageTagCrossRef(
    val imageId: Long,
    val tagId: Long
)

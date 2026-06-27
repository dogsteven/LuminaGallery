package com.foxsteven.luminagallery.infrastructure.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.foxsteven.luminagallery.data.model.ImageEntity
import com.foxsteven.luminagallery.data.model.ImageTagCrossRef
import com.foxsteven.luminagallery.data.model.SavedCriteriaEntity
import com.foxsteven.luminagallery.data.model.TagEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [
        ImageEntity::class,
        TagEntity::class,
        ImageTagCrossRef::class,
        SavedCriteriaEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(LuminaTypeConverters::class)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun tagDao(): TagDao
}

class LuminaTypeConverters {
    @TypeConverter
    fun fromList(list: List<Long>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toList(json: String): List<Long> {
        return Json.decodeFromString(json)
    }
}

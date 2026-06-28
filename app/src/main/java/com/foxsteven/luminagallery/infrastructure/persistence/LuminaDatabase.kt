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
import java.util.UUID

@Database(
    entities = [
        ImageEntity::class,
        TagEntity::class,
        ImageTagCrossRef::class,
        SavedCriteriaEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(LuminaTypeConverters::class)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun tagDao(): TagDao
    abstract fun savedCriteriaDao(): SavedCriteriaDao
}

class LuminaTypeConverters {
    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return Json.encodeToString(list)
    }

    @TypeConverter
    fun toStringList(json: String): List<String> {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }
}

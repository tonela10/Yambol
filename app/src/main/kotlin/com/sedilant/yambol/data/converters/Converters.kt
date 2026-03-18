package com.sedilant.yambol.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

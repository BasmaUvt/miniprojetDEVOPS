package com.example.tunisavia.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.*

/**
 * Converter class to map unknown data type for ROOM.
 */
object Converters {
    val GSON = Gson()

    @TypeConverter
    fun toDate(timestamp: Long?): Date? = if (timestamp == null) null else Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun toList(str: String): List<Any>? = try {
        GSON.fromJson(str, object : TypeToken<List<Any>?>() {}.type)
    } catch (error: IOException) {
        null
    }

    @TypeConverter
    fun toString(list: List<Long>?): String? = if (list != null) {
        try {
            GSON.toJson(list)
        } catch (e: IOException) {
            null
        }
    } else {
        null
    }
}

package com.whalert.app.util

import androidx.room.TypeConverter
import com.whalert.app.model.ReportCategory
import com.whalert.app.model.ReportStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Date

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromReportCategory(value: String?): ReportCategory? {
        return value?.let {
            try {
                ReportCategory.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun reportCategoryToString(category: ReportCategory?): String? {
        return category?.name
    }

    @TypeConverter
    fun fromReportStatus(value: String?): ReportStatus? {
        return value?.let {
            try {
                ReportStatus.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    @TypeConverter
    fun reportStatusToString(status: ReportStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String>? {
        if (value == null) return null
        val type: Type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, type)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun fromIntList(value: String?): List<Int>? {
        if (value == null) return null
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return try {
            gson.fromJson(value, type)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun intListToString(list: List<Int>?): String? {
        return list?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun fromLongList(value: String?): List<Long>? {
        if (value == null) return null
        val type: Type = object : TypeToken<List<Long>>() {}.type
        return try {
            gson.fromJson(value, type)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun longListToString(list: List<Long>?): String? {
        return list?.let { gson.toJson(it) }
    }
}

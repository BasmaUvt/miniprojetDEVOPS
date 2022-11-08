package com.example.tunisavia.local.db

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

import java.io.IOException

open class SharedPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("mobelite$NAME", Context.MODE_PRIVATE)

    fun containKey(key: String): Boolean = sharedPreferences.contains(key)

    fun putBoolean(key: String, value: Boolean) {
        val edit = sharedPreferences.edit()
        edit.putBoolean(key, value)
        edit.apply()
    }

    fun putFloat(key: String, value: Float) {
        val edit = sharedPreferences.edit()
        edit.putFloat(key, value)
        edit.apply()
    }

    fun putInt(key: String, value: Int) {
        val edit = sharedPreferences.edit()
        edit.putInt(key, value)
        edit.apply()
    }

    fun putLong(key: String, value: Long) {
        val edit = sharedPreferences.edit()
        edit.putLong(key, value)
        edit.apply()
    }

    fun putDouble(key: String, value: Double) {
        val edit = sharedPreferences.edit()
        edit.putString(key, value.toString())
        edit.apply()
    }

    fun putString(key: String, value: String) {
        val edit = sharedPreferences.edit()
        edit.putString(key, value)
        edit.apply()
    }

    fun <T> putObject(key: String, obj: T) {
        val edit = sharedPreferences.edit()
        val value = convertToString(obj)
        edit.putString(key, value)
        edit.apply()
    }

    fun putStringSet(key: String, value: Set<String>) {
        val edit = sharedPreferences.edit()
        edit.putStringSet(key, value)
        edit.apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defValue)

    fun getFloat(key: String, defValue: Float): Float = sharedPreferences.getFloat(key, defValue)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun getDouble(key: String, defValue: Double): Double {
        val valueStr = sharedPreferences.getString(key, defValue.toString())
        return if (valueStr.isNullOrEmpty()) valueStr?.toDouble() ?: 0.0 else 0.0
    }

    fun getInt(key: String, defValue: Int): Int = sharedPreferences.getInt(key, defValue)

    fun getLong(key: String, defValue: Long): Long = sharedPreferences.getLong(key, defValue)

    fun getString(key: String, defValue: String): String? =
        sharedPreferences.getString(key, defValue)

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? =
        sharedPreferences.getStringSet(key, defValue)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun <T> getObject(key: String, valueType: Class<T>): T? {
        val str = sharedPreferences.getString(key, null)
        return if (str.isNullOrEmpty()) convertToObject(str, valueType) else null
    }

    operator fun contains(key: String): Boolean = sharedPreferences.contains(key)

    fun remove(key: String) {
        val edit = sharedPreferences.edit()
        edit.remove(key)
        edit.apply()
    }

    fun clearAll() {
        val preferences = sharedPreferences.all
        val edit = sharedPreferences.edit()
        for (key1 in preferences) {
            edit.remove(key1.key)
        }
        edit.apply()
    }

    private fun <T> convertToObject(src: String?, valueType: Class<T>): T? = try {
        Gson().fromJson(src, valueType)
    } catch (error: IOException) {
        null
    }

    private fun <T> convertToString(value: T?): String? = if (value != null) {
        try {
            Gson().toJson(value)
        } catch (e: IOException) {
            null
        }
    } else {
        null
    }

    companion object {
        private const val NAME = "SharedPreferences"
    }
}

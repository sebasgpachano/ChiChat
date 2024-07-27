package com.team2.chitchat.data.repository.preferences

import android.content.SharedPreferences
import android.util.Log
import com.team2.chitchat.ui.extensions.TAG
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun <T : Any?> set(key: String, value: T) {
        setValue(key, value)
    }

    private fun setValue(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value.toInt()) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value.toFloat()) }
            is Long -> edit { it.putLong(key, value.toLong()) }
            else -> {
                Log.e(TAG, "l> SharedPreferenceExtensions Unsupported Type: $value")
            }
        }
    }

    private fun edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.sharedPreferences.edit()
        operation(editor)
        editor.apply()
    }

    fun saveBooleanSharedPreferences(key: String, value: Boolean) {
        set(key, value)
    }

    fun getBooleanSharedPreferences(key: String, defaultValue: Boolean = false): Boolean {
        val boolean = sharedPreferences.getBoolean(key, defaultValue)
        return boolean
    }
}
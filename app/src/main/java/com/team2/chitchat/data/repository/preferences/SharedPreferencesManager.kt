package com.team2.chitchat.data.repository.preferences

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import com.team2.chitchat.ui.extensions.TAG
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val PROFILE_PICTURE_KEY = "profile_picture"
    }

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
                Log.e(TAG, "l> SharedPrefeExtensions Unsupported Type: $value")
            }
        }
    }
    private fun edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.sharedPreferences.edit()
        operation(editor)
        editor.apply()
    }
    fun saveStringSharedPreferences(key: String, value: String) {
        set(key, value)
    }
    fun saveBooleanSharedPreferences(key: String, value: Boolean) {
        set(key, value)
    }
    fun getBooleanSharedPreferences(key: String, defaultValue: Boolean = false): Boolean {
        val boolean = sharedPreferences.getBoolean(key, defaultValue)
        return boolean
    }

    fun getStringSharedPreferences(userLogin: String): String {
        val string = sharedPreferences.getString(userLogin, "")
        return string!!
    }

    fun saveProfilePicture(imageView: CircleImageView?) {
        val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        with(sharedPreferences.edit()) {
            putString(PROFILE_PICTURE_KEY, encodedImage)
            apply()
        }
    }

    fun loadProfilePicture(): Bitmap? {
        val encodedImage = sharedPreferences.getString(PROFILE_PICTURE_KEY, null)
        return if (encodedImage != null) {
            val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }
    }
}
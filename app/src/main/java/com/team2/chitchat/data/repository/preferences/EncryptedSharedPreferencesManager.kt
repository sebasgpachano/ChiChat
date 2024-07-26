package com.team2.chitchat.data.repository.preferences

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE
import com.team2.chitchat.ui.extensions.TAG
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class EncryptedSharedPreferencesManager @Inject constructor(
    private val encryptedSharedPreferences: EncryptedSharedPreferences
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
        val editor = this.encryptedSharedPreferences.edit()
        operation(editor)
        editor.apply()
    }

    fun saveStringEncryptedSharedPreferences(key: String, value: String) {
        val start = System.currentTimeMillis()
        set(key, value)
        Log.d(
            TAG,
            "l> time - saveStringToDataStore key: $key, ${System.currentTimeMillis() - start}ms"
        )
    }

    fun getStringEncryptedSharedPreferences(key: String, defaultValue: String = ""): String {
        val start = System.currentTimeMillis()
        val test = encryptedSharedPreferences.getString(key, defaultValue) ?: ""
        Log.d(
            TAG,
            "l> time - getStringFromDataStore key: $key, ${System.currentTimeMillis() - start}ms"
        )
        return test
    }

    fun saveProfilePicture(imageView: CircleImageView?) {
        imageView?.drawable?.let { drawable ->
            val bitmap = (drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            with(encryptedSharedPreferences.edit()) {
                putString(ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE, encodedImage)
                apply()
            }
        }
    }

    fun loadProfilePicture(): Bitmap? {
        val encodedImage = encryptedSharedPreferences.getString(
            ENCRYPTED_SHARED_PREFERENCES_KEY_PROFILE_IMAGE,
            null
        )
        return encodedImage?.let {
            val byteArray = Base64.decode(it, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}
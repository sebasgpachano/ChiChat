package com.team2.chitchat.data.repository.preferences

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.Log
import com.team2.chitchat.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            context.getString(R.string.profile_shared_preferences),
            Context.MODE_PRIVATE
        )

    companion object {
        private const val PROFILE_PICTURE_KEY = "profile_picture"

        private var instance: SharedPreferencesManager? = null

        fun getInstance(context: Context): SharedPreferencesManager {
            if (instance == null) {
                instance = SharedPreferencesManager(context.applicationContext)
            }
            return instance!!
        }

        fun saveProfilePicture(context: Context, imageView: CircleImageView?) {
            val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val sharedPreferences = getInstance(context).sharedPreferences
            with(sharedPreferences.edit()) {
                putString(PROFILE_PICTURE_KEY, encodedImage)
                apply()
            }
        }

        fun loadProfilePicture(context: Context): Bitmap? {
            val sharedPreferences = getInstance(context).sharedPreferences
            val encodedImage = sharedPreferences.getString(PROFILE_PICTURE_KEY, null)
            return if (encodedImage != null) {
                val byteArray = Base64.decode(encodedImage, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            } else {
                null
            }
        }
    }

    fun <T : Any?> set(key: String, value: T) {
        setValue(key, value)
    }

    private fun setValue(key: String, value: Any?) {
        when (value) {
            is String? -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> {
                Log.e(TAG, "l> SharedPrefeExtensions Unsupported Type: $value")
            }
        }
    }

    private fun edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = sharedPreferences.edit()
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
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getStringSharedPreferences(key: String): String? {
        return sharedPreferences.getString(key, "")
    }
}
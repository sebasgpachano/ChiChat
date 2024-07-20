package com.team2.chitchat.data.repository.preferences

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
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
}
package com.team2.chitchat.data.repository.preferences

import android.graphics.Bitmap
import com.team2.chitchat.data.domain.model.error.ErrorModel
import com.team2.chitchat.data.repository.remote.response.BaseResponse
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class PreferencesDataSource @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager
) {
    //EncryptedSharedPreferences
    fun saveAuthToken(token: String) {
        encryptedSharedPreferencesManager.saveStringEncryptedSharedPreferences(
            EncryptedSharedPreferencesKeys.ENCRYPTED_SHARED_PREFERENCES_KEY_LOGIN_AUTH,
            token
        )
    }

    fun getAuthToken(): String {
        return encryptedSharedPreferencesManager.getStringEncryptedSharedPreferences(
            EncryptedSharedPreferencesKeys.ENCRYPTED_SHARED_PREFERENCES_KEY_LOGIN_AUTH
        )
    }

    fun saveUserID(userID: String) {
        encryptedSharedPreferencesManager.saveStringEncryptedSharedPreferences(
            EncryptedSharedPreferencesKeys.ENCRYPTED_SHARED_PREFERENCES_USER_ID,
            userID
        )
    }

    fun getUserID(): String {
        return encryptedSharedPreferencesManager.getStringEncryptedSharedPreferences(
            EncryptedSharedPreferencesKeys.ENCRYPTED_SHARED_PREFERENCES_USER_ID
        )
    }

    //SharedPreferences
    fun saveAccessBiometric(access: Boolean) {
        sharedPreferencesManager.saveBooleanSharedPreferences(
            EncryptedSharedPreferencesKeys.ACCESS_BIOMETRIC,
            access
        )
    }

    fun getAccessBiometric(): Boolean {
        return sharedPreferencesManager.getBooleanSharedPreferences(EncryptedSharedPreferencesKeys.ACCESS_BIOMETRIC)
    }

    fun saveProfilePicture(imageView: CircleImageView?) {
        sharedPreferencesManager.saveProfilePicture(imageView)
    }

    fun loadProfilePicture(): Bitmap? {
        return sharedPreferencesManager.loadProfilePicture()
    }

}
package com.team2.chitchat.data.repository.preferences

import android.graphics.Bitmap
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

    fun getIvParam(): String {
        return encryptedSharedPreferencesManager.getStringEncryptedSharedPreferences("IvParam")
    }

    fun saveIvParam(token: String) {
        encryptedSharedPreferencesManager.saveStringEncryptedSharedPreferences(
            "IvParam",
            token
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
        encryptedSharedPreferencesManager.saveProfilePicture(imageView)
    }

    fun loadProfilePicture(): Bitmap? {
        return encryptedSharedPreferencesManager.loadProfilePicture()
    }

    fun clearPreferences() {
        encryptedSharedPreferencesManager.clearAllPreferences()
        sharedPreferencesManager.clearAllPreferences()
    }

}
package com.team2.chitchat.hilt

import android.app.Application
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleApplication : Application() {
    @Inject
    lateinit var encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager

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
    fun getBearerTokenDes(): String {
        return "Desarrollo"
    }

    fun getBearerTokenPro(): String {
        return "Produccion"
    }
}
package com.team2.chitchat.hilt

import android.app.Application
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesManager
import com.team2.chitchat.data.repository.preferences.SharedPreferencesManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SimpleApplication : Application() {
    @Inject
    lateinit var encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

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

    fun saveAccessBiometric(access: Boolean) {
        sharedPreferencesManager.saveBooleanSharedPreferences(EncryptedSharedPreferencesKeys.ACCESS_BIOMETRIC, access)
    }
    fun getAccessBiometric(): Boolean {
        return sharedPreferencesManager.getBooleanSharedPreferences(EncryptedSharedPreferencesKeys.ACCESS_BIOMETRIC)
    }

    fun setUserLogin(userLogin: String) {
        encryptedSharedPreferencesManager.saveStringEncryptedSharedPreferences(EncryptedSharedPreferencesKeys.USER_LOGIN, userLogin)
    }

    fun getUserLogin(): String {
        return encryptedSharedPreferencesManager.getStringEncryptedSharedPreferences(EncryptedSharedPreferencesKeys.USER_LOGIN)
    }

    fun setUserPassword(userPassword: String) {
        sharedPreferencesManager.saveStringSharedPreferences(EncryptedSharedPreferencesKeys.USER_PASSWORD, userPassword)
    }
    fun getUserPassword(): String {
        return sharedPreferencesManager.getStringSharedPreferences(EncryptedSharedPreferencesKeys.USER_PASSWORD)
    }
}
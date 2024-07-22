package com.team2.chitchat.data.repository.preferences

class EncryptedSharedPreferencesKeys {
    companion object {
        const val USER_LOGIN: String = "user_login"
        const val USER_PASSWORD: String = "user_password"
        const val ENCRYPTED_SHARED_PREFERENCES_KEY_LOGIN_AUTH = "encryptedSharedPreferencesKeyLoginAuth"
        const val ENCRYPTED_SHARED_PREFERENCES_USER_ID = "encryptedSharedPreferencesKeyUserID"
        const val ACCESS_BIOMETRIC = "sharedPreferencesKeyAccessBiometric"
        const val ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_UUID = "firebase_uuid"
        const val ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_MESSAGING_TOKEN =
            "firebase_messaging_token"
    }
}
package com.team2.chitchat.hilt

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_MESSAGING_TOKEN
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_UUID
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesManager
import com.team2.chitchat.data.repository.preferences.PreferencesDataSource
import com.team2.chitchat.data.session.DataUserSession
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SimpleApplication : Application() {

    @Inject
    lateinit var preferencesDataSource: PreferencesDataSource

    @Inject
    lateinit var dataUserSession: DataUserSession

    @Inject
    lateinit var encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager
    override fun onCreate() {
        super.onCreate()
        initSession()
        configFirebase()
    }

    private fun initSession() {
        if (preferencesDataSource.getAuthToken().isNotBlank()
            && preferencesDataSource.getAuthToken().isNotEmpty()
        ) {
            dataUserSession.userId = preferencesDataSource.getUserID()
            dataUserSession.tokenIb = preferencesDataSource.getAuthToken()
        }
    }

    private fun configFirebase() {
        FirebaseApp.initializeApp(this.applicationContext)
        FirebaseInstallations.getInstance().id.addOnCompleteListener { idResult ->
            CoroutineScope(Dispatchers.IO).launch {
                val uuid = if (!idResult.result.isNullOrEmpty()) {
                    idResult.result.toString()
                } else {
                    ""
                }
                encryptedSharedPreferencesManager.putString(
                    ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_UUID,
                    uuid
                )
                Log.d(TAG, "firebase> configFirebase uuid: $uuid")
            }
        }
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messagingToken = task.result
                Log.d(TAG, "firebase> token: $messagingToken")
                encryptedSharedPreferencesManager.putString(
                    ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_MESSAGING_TOKEN,
                    messagingToken
                )
            } else {
                Log.w(TAG, "firebase> fallo en obtener el token")
            }
        }
    }
}
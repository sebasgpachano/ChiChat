package com.team2.chitchat.data.notifications

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesKeys.Companion.ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_MESSAGING_TOKEN
import com.team2.chitchat.data.repository.preferences.EncryptedSharedPreferencesManager
import com.team2.chitchat.ui.extensions.TAG
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChitChatFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var encryptedSharedPreferencesManager: EncryptedSharedPreferencesManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "%> Message data payload" + remoteMessage.data)
        }
        remoteMessage.notification?.let {
            Log.d(TAG, "%> Message notification body: ${it.body}")
        }
        NotificationHelper.createSimpleNotification(
            this,
            remoteMessage.notification?.title,
            remoteMessage.notification?.body,
            remoteMessage.notification?.imageUrl
        )
    }

    override fun onNewToken(token: String) {
        encryptedSharedPreferencesManager.saveStringEncryptedSharedPreferences(
            ENCRYPTED_SHARED_PREFERENCES_KEY_FIREBASE_MESSAGING_TOKEN,
            token
        )
    }

    override fun handleIntent(intent: Intent?) {
        super.handleIntent(intent)
        val title = intent?.getStringExtra("gcm.notification.title") ?: ""
        val message = intent?.getStringExtra("gcm.notification.body") ?: ""
        val image = intent?.getStringExtra("gcm.notification.image")
        val uriImage = if (image.isNullOrBlank()) null else Uri.parse(image)

        if (title.isNotEmpty() || message.isNotEmpty()) {
            NotificationHelper.createSimpleNotification(this, title, message, uriImage)
        }
    }
}
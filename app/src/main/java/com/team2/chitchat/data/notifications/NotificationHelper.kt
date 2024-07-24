package com.team2.chitchat.data.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.team2.chitchat.R
import com.team2.chitchat.data.constants.GeneralConstants.Companion.INTENT_KEY_PUSH_NOTIFICATION_BODY
import com.team2.chitchat.data.constants.GeneralConstants.Companion.INTENT_KEY_PUSH_NOTIFICATION_TITLE
import com.team2.chitchat.ui.main.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "CHITCHAT_FIREBASE_NOTIFICATION_CHANNEL_ID"
    private const val CHANNEL_NAME = "CHITCHAT_FIREBASE_NOTIFICATION_CHANNEL_NAME"

    @SuppressLint("MissingPermission")
    fun createSimpleNotification(
        context: Context,
        title: String?,
        body: String?,
        imageUrl: Uri?
    ) {
        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.putExtra(INTENT_KEY_PUSH_NOTIFICATION_TITLE, title)
        resultIntent.putExtra(INTENT_KEY_PUSH_NOTIFICATION_BODY, body)

        val requestCode = System.currentTimeMillis().toInt()
        val resultPendingIntent: PendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context,
                    requestCode,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context,
                    requestCode,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)

        val bmIcon = BitmapFactory.decodeFile(imageUrl?.path ?: "")
        val mBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        mBuilder.setSmallIcon(R.drawable.icon_toolbar_notification_off)
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(body)
        mBuilder.setLargeIcon(bmIcon)
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        mBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(body))
        mBuilder.setContentIntent(resultPendingIntent)
        mBuilder.setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(requestCode, mBuilder.build())
    }
}
package com.example.tunisavia

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    // generate notification
    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "My channel description"
                enableLights(true)
                lightColor = Color.GREEN
            }
            notificationManager.createNotificationChannel(notificationChannel)
        } else Log.i("MyFirebaseMessagingService", "generateNotification: error")

        notificationManager.notify(0, builder.build())
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived (message)
        if (message.data.isNotEmpty()) generateNotification(message.data["title"] ?: "", message.data["message"] ?: "")
    }

    @SuppressLint("RemoteViewLayout")
    fun getRemote(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews(channelName, R.layout.notification)
        remoteView.setTextViewText(R.id.title, title)
        remoteView.setTextViewText(R.id.description, message)
        remoteView.setImageViewResource(R.id.logo_app, R.drawable.logo)

        return remoteView
    }

    companion object {
        const val channelId = "notification_channel"
        const val channelName = "com.example.tunisavia"
    }

}
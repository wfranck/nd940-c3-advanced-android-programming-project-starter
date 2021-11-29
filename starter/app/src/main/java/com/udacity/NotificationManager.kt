package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    context: Context,
    url: String = "",
    status: String = "",
    message: String
) {
    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val detailIntent = Intent(context, DetailActivity::class.java).apply {
        this.putExtra("URL", url.toString())
        this.putExtra("STATUS", status)
    }

    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val action = NotificationCompat.Action.Builder(
        0,
        "See Download",
        detailPendingIntent
    ).build()

    val builder = NotificationCompat.Builder(context, "ChannelLoadApp")
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle("Udacity: Android Kotlin Nanodegree")
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setOnlyAlertOnce(true)
        .addAction(action)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications(){
    cancelAll()
}

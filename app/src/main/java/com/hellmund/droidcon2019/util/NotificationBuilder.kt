package com.hellmund.droidcon2019.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Talk
import com.hellmund.droidcon2019.ui.MainActivity

object NotificationBuilder {

    fun buildNotification(
        context: Context,
        id: Int,
        event: Talk
    ): Notification {
        val launchIntent = MainActivity.newNotificationIntent(context, event)
        val launchPendingIntent = PendingIntent.getActivity(
            context, id, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val color = ContextCompat.getColor(context, R.color.colorAccent)

        return NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setContentTitle(event.title)
            .setContentText("starts in 15 minutes on stage ${event.stage}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(launchPendingIntent)
            .setColor(color)
            .setAutoCancel(true)
            .build()
    }

}

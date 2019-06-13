package com.hellmund.droidcon2019.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.hellmund.droidcon2019.BuildConfig
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.ui.schedule.EventsRepository
import org.threeten.bp.ZoneId

class NotificationScheduler(
    private val context: Context
) {

    private val alarmManager: AlarmManager
        get() = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(event: Session) {
        val time = event.day.toDate()
            .atStartOfDay()
            .withHour(event.startTime.hour)
            .withMinute(event.startTime.minute)
            .minusMinutes(15)

        var notificationTime = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (BuildConfig.DEBUG) {
            notificationTime = System.currentTimeMillis() + 3_000
        }

        val alarmIntent = getAlarmIntent(event)
        alarmManager.setExact(AlarmManager.RTC, notificationTime, alarmIntent)
    }

    fun remove(event: Session) {
        val alarmIntent = getAlarmIntent(event)
        alarmManager.cancel(alarmIntent)
    }

    private fun getAlarmIntent(event: Session): PendingIntent {
        val eventsRepository = EventsRepository.getInstance(context)

        val id = eventsRepository.getEventId(event)
        val notification = NotificationBuilder.buildNotification(context, id, event)

        val intent = NotificationReceiver.newIntent(context, id, notification)
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    companion object {

        const val CHANNEL_ID = "eventRemindersChannel"

        @RequiresApi(Build.VERSION_CODES.O)
        fun setupNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Event notifications",
                NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }

            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

}

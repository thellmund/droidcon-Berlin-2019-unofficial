package com.hellmund.droidcon2019.util

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(KEY_ID, -1)
        val notification = intent.getParcelableExtra<Notification>(KEY_EVENT)

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }

    companion object {

        private const val KEY_EVENT = "KEY_EVENT"
        private const val KEY_ID = "KEY_ID"

        fun newIntent(
            context: Context,
            id: Int,
            notification: Notification
        ): Intent {
            return Intent(context, NotificationReceiver::class.java).apply {
                putExtra(KEY_ID, id)
                putExtra(KEY_EVENT, notification)
            }
        }

    }

}

package com.hellmund.droidcon2019

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.appcompat.app.AppCompatDelegate
import com.hellmund.droidcon2019.util.NotificationScheduler
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AndroidThreeTen.init(this)

        val picasso = Picasso.Builder(this)
            .downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
            .build()
        Picasso.setSingletonInstance(picasso)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        if (SDK_INT >= O) {
            NotificationScheduler.setupNotificationChannel(this)
        }
    }

}

package com.toploans.cashOcean

import android.app.Application
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.onesignal.OneSignal


class CashOcean() : Application() {
    private val ONESIGNAL_APP_ID = "65b70913-de83-496b-b413-5c6d43b3c3b8"
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}


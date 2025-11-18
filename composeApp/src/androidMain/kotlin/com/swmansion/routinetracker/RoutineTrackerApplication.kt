package com.swmansion.routinetracker

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.DefaultAppContainer

class RoutineTrackerApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        NotifierManager.initialize(
            NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = true
            )
        )
    }
}

package com.swmansion.routinetracker

import android.app.Application
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.DefaultAppContainer

class RoutineTrackerApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer(this) }
}

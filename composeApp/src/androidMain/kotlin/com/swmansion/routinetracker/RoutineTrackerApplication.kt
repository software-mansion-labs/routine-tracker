package com.swmansion.routinetracker

import android.app.Application
import com.swmansion.routinetracker.di.AppContainer

class RoutineTrackerApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}

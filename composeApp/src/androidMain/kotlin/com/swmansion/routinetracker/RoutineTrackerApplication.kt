package com.swmansion.routinetracker

import android.app.Application
import com.swmansion.routinetracker.di.DefaultAppContainer

class RoutineTrackerApplication : Application() {
    val defaultAppContainer: DefaultAppContainer by lazy { DefaultAppContainer(this) }
}

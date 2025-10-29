package com.swmansion.routinetracker

import android.app.Application
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.DataFactory

class RoutineTrackerApplication : Application() {
    val appContainer: AppContainer by lazy {
        val dataFactory = DataFactory(this)
        val database = dataFactory.createRoomDatabase()
        AppContainer(database)
    }
}

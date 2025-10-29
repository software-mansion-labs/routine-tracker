package com.swmansion.routinetracker.di

import android.app.Application
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDatabase

actual class AppContainer(private val application: Application) {
    actual val database: RoutineDatabase by lazy {
        val dataFactory = DataFactory(application)
        dataFactory.createRoomDatabase()
    }

    actual val repository: DataRepository by lazy {
        DataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }
}

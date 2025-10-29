package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDatabase

actual class AppContainer {
    actual val database: RoutineDatabase by lazy {
        val dataFactory = DataFactory()
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

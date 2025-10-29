package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDatabase

class AppContainer(val database: RoutineDatabase) {
    val repository: DataRepository by lazy {
        DataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }
}

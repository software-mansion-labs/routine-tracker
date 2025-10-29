package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDatabase

expect class AppContainer {
    val database: RoutineDatabase
    val repository: DataRepository
}

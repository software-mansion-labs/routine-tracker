package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.database.RoutineDatabase

expect class DataFactory {
    fun createRoomDatabase(): RoutineDatabase
}
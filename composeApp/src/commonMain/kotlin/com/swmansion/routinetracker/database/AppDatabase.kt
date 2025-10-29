package com.swmansion.routinetracker.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.Task

@Database(entities = [Routine::class, Task::class, RoutineRecurrence::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class RoutineDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao

    abstract fun taskDao(): TaskDao

    abstract fun routineRecurrenceDao(): RoutineRecurrenceDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<RoutineDatabase> {
    override fun initialize(): RoutineDatabase
}

internal const val DB_FILE_NAME = "routine.db"

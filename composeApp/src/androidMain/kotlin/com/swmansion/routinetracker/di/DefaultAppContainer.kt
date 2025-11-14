package com.swmansion.routinetracker.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.DefaultDataRepository
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.coroutines.Dispatchers

actual class DefaultAppContainer(private val application: Any?) :
    AppContainer {

    private val database: RoutineDatabase by lazy {
        val app =
            application as? Application
                ?: throw IllegalStateException("Application is required for AppContainer")
        val dbFile = app.getDatabasePath(DB_FILE_NAME)
        Room.databaseBuilder<RoutineDatabase>(context = app, name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    actual override val repository: DataRepository by lazy {
        DefaultDataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }
}

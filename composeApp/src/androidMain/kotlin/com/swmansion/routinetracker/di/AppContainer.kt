package com.swmansion.routinetracker.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.coroutines.Dispatchers

actual class AppContainer(private val application: Application) {
    actual val database: RoutineDatabase by lazy {
        val dbFile = application.getDatabasePath(DB_FILE_NAME)
        Room.databaseBuilder<RoutineDatabase>(context = application, name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    actual val repository: DataRepository by lazy {
        DataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }
}

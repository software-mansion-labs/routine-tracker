package com.swmansion.routinetracker.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.IDataRepository
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.coroutines.Dispatchers

actual class AppContainer(private val application: Any?) : IAppContainer {

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

    actual override val repository: IDataRepository by lazy {
        DataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }
}

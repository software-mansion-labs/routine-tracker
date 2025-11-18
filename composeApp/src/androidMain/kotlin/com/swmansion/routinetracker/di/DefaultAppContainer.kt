package com.swmansion.routinetracker.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.DefaultDataRepository
import com.swmansion.routinetracker.data.AndroidUserPreferencesRepository
import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual class DefaultAppContainer(private val application: Application) : AppContainer {

    private val database: RoutineDatabase by lazy {
        val dbFile = application.getDatabasePath(DB_FILE_NAME)
        Room.databaseBuilder<RoutineDatabase>(context = application, name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    private val appScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    actual override val repository: DataRepository by lazy {
        DefaultDataRepository(
            routineDao = database.routineDao(),
            taskDao = database.taskDao(),
            routineRecurrenceDao = database.routineRecurrenceDao(),
        )
    }

    actual override val userPreferencesRepository: UserPreferencesRepository by lazy {
        AndroidUserPreferencesRepository(application, appScope)
    }
}

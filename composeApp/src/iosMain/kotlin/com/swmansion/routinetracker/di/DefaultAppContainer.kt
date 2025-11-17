package com.swmansion.routinetracker.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.DefaultDataRepository
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class DefaultAppContainer : AppContainer {
    private val database: RoutineDatabase by lazy {
        val dbFile = "${fileDirectory()}/$DB_FILE_NAME"
        Room.databaseBuilder<RoutineDatabase>(name = dbFile)
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

    @OptIn(ExperimentalForeignApi::class)
    private fun fileDirectory(): String {
        val documentDirectory: NSURL? =
            NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null,
            )
        return requireNotNull(documentDirectory).path!!
    }
}

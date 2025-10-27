package com.swmansion.routinetracker.di

import androidx.room.Room
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual class DataFactory {
    actual fun createRoomDatabase(): RoutineDatabase {
        val dbFile = "${fileDirectory()}/$DB_FILE_NAME"
        return Room.databaseBuilder<RoutineDatabase>(name = dbFile)
            .setDriver(_root_ide_package_.androidx.sqlite.driver.bundled.BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
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

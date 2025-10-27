package com.swmansion.routinetracker.di

import android.app.Application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.swmansion.routinetracker.database.DB_FILE_NAME
import com.swmansion.routinetracker.database.RoutineDatabase
import kotlinx.coroutines.Dispatchers

actual class DataFactory(private val app: Application) {
    actual fun createRoomDatabase(): RoutineDatabase {
        val dbFile = app.getDatabasePath(DB_FILE_NAME)
        return Room.databaseBuilder<RoutineDatabase>(context = app, name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

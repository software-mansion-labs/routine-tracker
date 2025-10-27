package com.swmansion.routinetracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.swmansion.routinetracker.screen.TestDatabaseScreen

var globalDatabase: com.swmansion.routinetracker.database.RoutineDatabase? = null

@Composable
fun App() {
    MaterialTheme {
        val db = globalDatabase
        if (db != null) {
            TestDatabaseScreen(db)
        } else {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                androidx.compose.material3.Text(
                    "Database not initialized",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

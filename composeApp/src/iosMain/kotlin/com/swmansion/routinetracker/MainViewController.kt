package com.swmansion.routinetracker

import androidx.compose.ui.window.ComposeUIViewController
import com.swmansion.routinetracker.di.DataFactory

fun MainViewController() = ComposeUIViewController {
    if (globalDatabase == null) {
        val dataFactory = DataFactory()
        globalDatabase = dataFactory.createRoomDatabase()
    }
    App()
}

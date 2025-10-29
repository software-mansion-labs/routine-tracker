package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.DataFactory
import com.swmansion.routinetracker.di.LocalAppContainer

fun MainViewController() = ComposeUIViewController {
    val dataFactory = DataFactory()
    val database = dataFactory.createRoomDatabase()
    val appContainer = AppContainer(database)

    CompositionLocalProvider(LocalAppContainer provides appContainer) { App() }
}

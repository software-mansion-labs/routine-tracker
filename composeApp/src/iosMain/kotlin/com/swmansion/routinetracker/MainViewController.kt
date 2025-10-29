package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.LocalAppContainer

fun MainViewController() = ComposeUIViewController {
    val appContainer = AppContainer()

    CompositionLocalProvider(LocalAppContainer provides appContainer) { App() }
}

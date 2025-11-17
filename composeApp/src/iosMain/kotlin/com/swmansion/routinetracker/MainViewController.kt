package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.swmansion.routinetracker.di.DefaultAppContainer
import com.swmansion.routinetracker.di.LocalAppContainer

fun MainViewController() = ComposeUIViewController {
    val defaultAppContainer = DefaultAppContainer()

    CompositionLocalProvider(LocalAppContainer provides defaultAppContainer) { App() }
}

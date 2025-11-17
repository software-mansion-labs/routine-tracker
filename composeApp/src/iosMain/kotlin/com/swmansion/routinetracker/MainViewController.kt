package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.swmansion.routinetracker.di.DefaultAppContainer
import com.swmansion.routinetracker.di.LocalAppContainer

fun MainViewController() = ComposeUIViewController {
    val defaultAppContainer = remember { DefaultAppContainer() }

    DisposableEffect(Unit) {
        onDispose { defaultAppContainer.close() }
    }

    CompositionLocalProvider(LocalAppContainer provides defaultAppContainer) { App() }
}

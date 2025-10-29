package com.swmansion.routinetracker.di

import androidx.compose.runtime.compositionLocalOf

val LocalAppContainer = compositionLocalOf<AppContainer> { error("AppContainer not provided") }

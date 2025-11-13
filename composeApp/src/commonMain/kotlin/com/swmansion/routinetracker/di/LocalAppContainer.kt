package com.swmansion.routinetracker.di

import androidx.compose.runtime.compositionLocalOf

val LocalAppContainer = compositionLocalOf<IAppContainer> { error("AppContainer not provided") }

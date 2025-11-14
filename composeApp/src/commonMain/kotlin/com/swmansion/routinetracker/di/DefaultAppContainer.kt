package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository

interface AppContainer {
    val repository: DataRepository
}

expect class DefaultAppContainer : AppContainer {
    override val repository: DataRepository
}

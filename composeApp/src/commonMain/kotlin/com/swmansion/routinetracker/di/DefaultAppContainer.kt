package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.data.UserPreferencesRepository

interface AppContainer {
    val repository: DataRepository
    val userPreferencesRepository: UserPreferencesRepository
}

expect class DefaultAppContainer : AppContainer {
    override val repository: DataRepository
    override val userPreferencesRepository: UserPreferencesRepository
}

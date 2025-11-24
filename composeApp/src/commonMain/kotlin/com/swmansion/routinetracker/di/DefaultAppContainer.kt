package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.tweener.alarmee.AlarmeeService

interface AppContainer {
    val repository: DataRepository
    val userPreferencesRepository: UserPreferencesRepository
    val alarmeeService: AlarmeeService
}

expect class DefaultAppContainer : AppContainer {
    override val repository: DataRepository
    override val userPreferencesRepository: UserPreferencesRepository
    override val alarmeeService: AlarmeeService
}

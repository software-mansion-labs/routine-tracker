package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository

expect class AppContainer {
    val repository: DataRepository
}

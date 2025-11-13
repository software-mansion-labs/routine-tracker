package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.IDataRepository

interface IAppContainer {
    val repository: IDataRepository
}

expect class AppContainer : IAppContainer {
    override val repository: IDataRepository
}

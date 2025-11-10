package com.swmansion.routinetracker.di

import com.swmansion.routinetracker.DataRepository

expect open class AppContainer {
    open val repository: DataRepository
    
    constructor()
    constructor(application: Any?)
}

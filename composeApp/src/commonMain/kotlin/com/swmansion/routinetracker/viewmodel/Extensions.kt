package com.swmansion.routinetracker.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    update { it.update() }
}

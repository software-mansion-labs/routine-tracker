package com.swmansion.routinetracker.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    update { it.update() }
}

fun durationToString(duration: Int?): String? =
    duration?.let { seconds ->
        val totalMinutes = seconds / 60
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            m > 0 -> "${m}m"
            else -> "0m"
        }
    }

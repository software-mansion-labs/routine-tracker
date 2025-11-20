package com.swmansion.routinetracker.viewmodel

import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.TimeZone

internal fun <T> MutableStateFlow<T>.updateState(update: T.() -> T) {
    update { it.update() }
}

fun durationToString(duration: Int): String {
    val totalMinutes = duration / 60
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        m > 0 -> "${m}m"
        else -> "0m"
    }
}

fun getReminderOffset(option: String) =
    when (option) {
        "5 min" -> 5.minutes
        "15 min" -> 15.minutes
        "30 min" -> 30.minutes
        "1 hour" -> 1.hours
        "4 hours" -> 4.hours
        else -> 15.minutes
    }

fun getCurrentTimeZone() = TimeZone.currentSystemDefault()

@OptIn(ExperimentalTime::class) fun getCurrentInstant() = Clock.System.now()

fun parseHourMinute(time: String): Pair<Int, Int> {
    val parts = time.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return h to m
}

fun formatTime(hour: Int, minute: Int): String =
    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

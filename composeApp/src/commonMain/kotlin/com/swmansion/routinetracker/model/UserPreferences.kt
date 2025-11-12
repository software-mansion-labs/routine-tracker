package com.swmansion.routinetracker.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val remindersEnabled: Boolean = false,
    val specifiedTimeOption: String = "15 min",
    val unspecifiedReminderHour: Int = 9,
    val unspecifiedReminderMinute: Int = 0,
)

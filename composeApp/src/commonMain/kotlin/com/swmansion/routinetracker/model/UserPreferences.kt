package com.swmansion.routinetracker.model

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val remindersEnabled: Boolean = false,
    val specifiedTimeOption: String = DEFAULT_SPECIFIED_TIME_OPTION,
    val unspecifiedReminderHour: Int = DEFAULT_UNSPECIFIED_HOUR,
    val unspecifiedReminderMinute: Int = DEFAULT_UNSPECIFIED_MINUTE,
) {
    companion object {
        const val DEFAULT_SPECIFIED_TIME_OPTION: String = "15 min"
        const val DEFAULT_UNSPECIFIED_HOUR: Int = 9
        const val DEFAULT_UNSPECIFIED_MINUTE: Int = 0
    }
}

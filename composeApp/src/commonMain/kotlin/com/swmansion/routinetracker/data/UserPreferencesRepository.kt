package com.swmansion.routinetracker.data

import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.StateFlow

expect class UserPreferencesRepository {
    val preferences: StateFlow<UserPreferences>

    suspend fun setRemindersEnabled(enabled: Boolean)

    suspend fun setSpecifiedTimeOption(option: String)

    suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int)
}

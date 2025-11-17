package com.swmansion.routinetracker.mock.data

import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockUserPreferencesRepository(initial: UserPreferences = UserPreferences()) :
    UserPreferencesRepository {
    private val mutable = MutableStateFlow(initial)

    override val preferences: StateFlow<UserPreferences> = mutable

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        mutable.value = mutable.value.copy(remindersEnabled = enabled)
    }

    override suspend fun setSpecifiedTimeOption(option: String) {
        mutable.value = mutable.value.copy(specifiedTimeOption = option)
    }

    override suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        mutable.value =
            mutable.value.copy(unspecifiedReminderHour = hour, unspecifiedReminderMinute = minute)
    }

    suspend fun setPreferences(preferences: UserPreferences) {
        mutable.value = preferences
    }
}

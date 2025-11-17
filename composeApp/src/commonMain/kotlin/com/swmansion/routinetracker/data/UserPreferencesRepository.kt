package com.swmansion.routinetracker.data

import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface UserPreferencesRepository {
    val preferences: StateFlow<UserPreferences>

    suspend fun setRemindersEnabled(enabled: Boolean)

    suspend fun setSpecifiedTimeOption(option: String)

    suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int)
}

fun createUserPreferencesRepository(): UserPreferencesRepository =
    DefaultUserPreferencesRepository()

class DefaultUserPreferencesRepository(initial: UserPreferences = UserPreferences()) :
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
}

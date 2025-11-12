package com.swmansion.routinetracker.data

import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSUserDefaults

actual class UserPreferencesRepository {
    private val defaults = NSUserDefaults.Companion.standardUserDefaults()
    private val _preferences = MutableStateFlow(load())
    actual val preferences: StateFlow<UserPreferences>
        get() = _preferences

    private fun load(): UserPreferences =
        UserPreferences(
            remindersEnabled = defaults.boolForKey("reminders_enabled"),
            specifiedTimeOption = defaults.stringForKey("specified_time_option") ?: "15 min",
            unspecifiedReminderHour =
                defaults.integerForKey("unspecified_hour").toInt().let { if (it == 0) 9 else it },
            unspecifiedReminderMinute = defaults.integerForKey("unspecified_minute").toInt(),
        )

    private fun update(transform: (UserPreferences) -> UserPreferences) {
        val newValue = transform(_preferences.value)
        _preferences.value = newValue
    }

    actual suspend fun setRemindersEnabled(enabled: Boolean) {
        defaults.setBool(enabled, "reminders_enabled")
        update { it.copy(remindersEnabled = enabled) }
    }

    actual suspend fun setSpecifiedTimeOption(option: String) {
        defaults.setObject(option, "specified_time_option")
        update { it.copy(specifiedTimeOption = option) }
    }

    actual suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        defaults.setInteger(hour.toLong(), "unspecified_hour")
        defaults.setInteger(minute.toLong(), "unspecified_minute")
        update { it.copy(unspecifiedReminderHour = hour, unspecifiedReminderMinute = minute) }
    }
}

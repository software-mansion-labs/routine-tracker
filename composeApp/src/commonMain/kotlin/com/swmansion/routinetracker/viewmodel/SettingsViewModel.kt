package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
) {
    val prefs: StateFlow<UserPreferences> = repository.preferences

    fun toggleReminders(enabled: Boolean) {
        scope.launch { repository.setRemindersEnabled(enabled) }
    }

    fun setSpecified(option: String) {
        scope.launch { repository.setSpecifiedTimeOption(option) }
    }

    fun setUnspecified(hour: Int, minute: Int) {
        scope.launch { repository.setUnspecifiedReminderTime(hour, minute) }
    }
}

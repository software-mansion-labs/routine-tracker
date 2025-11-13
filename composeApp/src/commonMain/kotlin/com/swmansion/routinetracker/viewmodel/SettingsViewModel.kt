package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
): ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val prefs: StateFlow<UserPreferences> = repository.preferences
    val uiState: StateFlow<SettingsUiState> =
        repository.preferences
            .map { p ->
                SettingsUiState(
                    remindersEnabled = p.remindersEnabled,
                    specifiedOptions = SPECIFIED_OPTIONS,
                    specifiedSelected =
                        p.specifiedTimeOption.ifEmpty { SPECIFIED_OPTIONS.first() },
                    unspecifiedReminderHour = p.unspecifiedReminderHour,
                    unspecifiedReminderMinute = p.unspecifiedReminderMinute,
                    unspecifiedFormatted = formatTime(p.unspecifiedReminderHour, p.unspecifiedReminderMinute),
                )
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState(),
            )

    fun toggleReminders(enabled: Boolean) {
        scope.launch { repository.setRemindersEnabled(enabled) }
    }

    fun setSpecified(option: String) {
        scope.launch { repository.setSpecifiedTimeOption(option) }
    }

    fun setUnspecified(hour: Int, minute: Int) {
        scope.launch { repository.setUnspecifiedReminderTime(hour, minute) }
    }

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<UserPreferencesRepository> {}
        var Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val prefsRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("UserPreferencesRepository not provided")

                SettingsViewModel(prefsRepository)
            }
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String =
    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

private val SPECIFIED_OPTIONS = listOf("5 min", "15 min", "30 min", "1 hour", "4 hours")

data class SettingsUiState(
    val remindersEnabled: Boolean = false,
    val specifiedOptions: List<String> = SPECIFIED_OPTIONS,
    val specifiedSelected: String = SPECIFIED_OPTIONS.first(),
    val unspecifiedReminderHour: Int = 9,
    val unspecifiedReminderMinute: Int = 0,
    val unspecifiedFormatted: String = formatTime(9, 0),
)

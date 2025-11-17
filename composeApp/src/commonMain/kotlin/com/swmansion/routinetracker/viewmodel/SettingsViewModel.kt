package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        repository.preferences
            .map { p ->
                SettingsUiState(
                    remindersEnabled = p.remindersEnabled,
                    specifiedOptions = SPECIFIED_OPTIONS,
                    specifiedSelected = p.specifiedTimeOption.ifEmpty { SPECIFIED_OPTIONS.first() },
                    unspecifiedReminderHour = p.unspecifiedReminderHour,
                    unspecifiedReminderMinute = p.unspecifiedReminderMinute,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState(),
            )

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch { repository.setRemindersEnabled(enabled) }
    }

    fun setSpecified(option: String) {
        viewModelScope.launch { repository.setSpecifiedTimeOption(option) }
    }

    fun setUnspecified(hour: Int, minute: Int) {
        viewModelScope.launch { repository.setUnspecifiedReminderTime(hour, minute) }
    }

    companion object {
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<UserPreferencesRepository> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val prefsRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("UserPreferencesRepository not provided")

                SettingsViewModel(prefsRepository)
            }
        }
    }
}

fun formatTime(hour: Int, minute: Int): String =
    hour.toString().padStart(2, '0') + ":" + minute.toString().padStart(2, '0')

private val SPECIFIED_OPTIONS = listOf("5 min", "15 min", "30 min", "1 hour", "4 hours")

data class SettingsUiState(
    val remindersEnabled: Boolean = false,
    val specifiedOptions: List<String> = SPECIFIED_OPTIONS,
    val specifiedSelected: String = SPECIFIED_OPTIONS.first(),
    val unspecifiedReminderHour: Int = 9,
    val unspecifiedReminderMinute: Int = 0,
)

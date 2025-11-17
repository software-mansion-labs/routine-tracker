package com.swmansion.routinetracker.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val DATASTORE_NAME = "user_prefs"

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

private val KEY_REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
private val KEY_SPECIFIED_OPTION = stringPreferencesKey("specified_time_option")
private val KEY_UNSPECIFIED_HOUR = intPreferencesKey("unspecified_hour")
private val KEY_UNSPECIFIED_MINUTE = intPreferencesKey("unspecified_minute")

actual class UserPreferencesRepository(
    private val appContext: Context,
    private val scope: CoroutineScope,
) {
    private val flow =
        appContext.dataStore.data.map { prefs ->
            UserPreferences(
                remindersEnabled = prefs[KEY_REMINDERS_ENABLED] ?: false,
                specifiedTimeOption =
                    prefs[KEY_SPECIFIED_OPTION] ?: UserPreferences.DEFAULT_SPECIFIED_TIME_OPTION,
                unspecifiedReminderHour =
                    prefs[KEY_UNSPECIFIED_HOUR] ?: UserPreferences.DEFAULT_UNSPECIFIED_HOUR,
                unspecifiedReminderMinute =
                    prefs[KEY_UNSPECIFIED_MINUTE] ?: UserPreferences.DEFAULT_UNSPECIFIED_MINUTE,
            )
        }

    actual val preferences: StateFlow<UserPreferences> =
        flow.stateIn(scope, SharingStarted.Eagerly, UserPreferences())

    actual suspend fun setRemindersEnabled(enabled: Boolean) {
        appContext.dataStore.edit { it[KEY_REMINDERS_ENABLED] = enabled }
    }

    actual suspend fun setSpecifiedTimeOption(option: String) {
        appContext.dataStore.edit { it[KEY_SPECIFIED_OPTION] = option }
    }

    actual suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        appContext.dataStore.edit {
            it[KEY_UNSPECIFIED_HOUR] = hour
            it[KEY_UNSPECIFIED_MINUTE] = minute
        }
    }
}

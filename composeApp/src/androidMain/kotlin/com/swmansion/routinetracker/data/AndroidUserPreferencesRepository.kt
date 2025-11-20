package com.swmansion.routinetracker.data

import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.swmansion.routinetracker.R
import com.swmansion.routinetracker.model.UserPreferences
import com.tweener.alarmee.channel.AlarmeeNotificationChannel
import com.tweener.alarmee.configuration.AlarmeeAndroidPlatformConfiguration
import com.tweener.alarmee.configuration.AlarmeePlatformConfiguration
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

class AndroidUserPreferencesRepository(
    private val appContext: Context,
    private val scope: CoroutineScope,
) : UserPreferencesRepository {
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

    override val preferences: StateFlow<UserPreferences> =
        flow.stateIn(scope, SharingStarted.Eagerly, UserPreferences())

    override suspend fun setRemindersEnabled(enabled: Boolean) {
        appContext.dataStore.edit { it[KEY_REMINDERS_ENABLED] = enabled }
    }

    override suspend fun setSpecifiedTimeOption(option: String) {
        appContext.dataStore.edit { it[KEY_SPECIFIED_OPTION] = option }
    }

    override suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        appContext.dataStore.edit {
            it[KEY_UNSPECIFIED_HOUR] = hour
            it[KEY_UNSPECIFIED_MINUTE] = minute
        }
    }
}

actual fun createAlarmeePlatformConfiguration(): AlarmeePlatformConfiguration =
    AlarmeeAndroidPlatformConfiguration(
        notificationIconResId = R.drawable.ic_launcher_foreground,
        notificationIconColor = Color.Blue,
        useExactScheduling = true,
        notificationChannels =
            listOf(
                AlarmeeNotificationChannel(
                    id = "routineChannel",
                    name = "Routine notifications",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                    soundFilename = "notifications_sound",
                )
            ),
    )

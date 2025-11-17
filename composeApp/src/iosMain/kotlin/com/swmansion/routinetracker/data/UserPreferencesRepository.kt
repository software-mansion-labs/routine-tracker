package com.swmansion.routinetracker.data

import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUbiquitousKeyValueStore
import platform.Foundation.NSUbiquitousKeyValueStoreDidChangeExternallyNotification
import platform.darwin.NSObjectProtocol

actual class UserPreferencesRepository {
    private val store = NSUbiquitousKeyValueStore.defaultStore()
    private val _preferences = MutableStateFlow(load())
    actual val preferences: StateFlow<UserPreferences>
        get() = _preferences

    private val observer: NSObjectProtocol
    private var disposed = false

    init {
        observer =
            NSNotificationCenter.defaultCenter.addObserverForName(
                name = NSUbiquitousKeyValueStoreDidChangeExternallyNotification,
                `object` = null,
                queue = null,
            ) { _ ->
                _preferences.value = load()
            }
        store.synchronize()
    }

    fun dispose() {
        if (disposed) return
        NSNotificationCenter.defaultCenter.removeObserver(observer)
        disposed = true
    }

    private fun load(): UserPreferences =
        UserPreferences(
            remindersEnabled = store.boolForKey("reminders_enabled"),
            specifiedTimeOption =
                store.stringForKey("specified_time_option")
                    ?: UserPreferences.DEFAULT_SPECIFIED_TIME_OPTION,
            unspecifiedReminderHour =
                store.longLongForKey("unspecified_hour").let {
                    if (it == -1L) UserPreferences.DEFAULT_UNSPECIFIED_HOUR else it.toInt()
                },
            unspecifiedReminderMinute = store.longLongForKey("unspecified_minute").toInt(),
        )

    actual suspend fun setRemindersEnabled(enabled: Boolean) {
        _preferences.update { it.copy(remindersEnabled = enabled) }
        store.setBool(enabled, "reminders_enabled")
        store.synchronize()
    }

    actual suspend fun setSpecifiedTimeOption(option: String) {
        _preferences.update { it.copy(specifiedTimeOption = option) }
        store.setString(option, "specified_time_option")
        store.synchronize()
    }

    actual suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        _preferences.update {
            it.copy(unspecifiedReminderHour = hour, unspecifiedReminderMinute = minute)
        }
        store.setLongLong(hour.toLong(), "unspecified_hour")
        store.setLongLong(minute.toLong(), "unspecified_minute")
        store.synchronize()
    }
}

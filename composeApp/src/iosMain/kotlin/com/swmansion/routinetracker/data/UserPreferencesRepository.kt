package com.swmansion.routinetracker.data

import com.swmansion.routinetracker.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            specifiedTimeOption = store.stringForKey("specified_time_option") ?: "15 min",
            unspecifiedReminderHour =
                store.longLongForKey("unspecified_hour").let { if (it == 0L) 9 else it.toInt() },
            unspecifiedReminderMinute = store.longLongForKey("unspecified_minute").toInt(),
        )

    private fun update(transform: (UserPreferences) -> UserPreferences) {
        val newValue = transform(_preferences.value)
        _preferences.value = newValue
    }

    actual suspend fun setRemindersEnabled(enabled: Boolean) {
        store.setBool(enabled, "reminders_enabled")
        store.synchronize()
        update { it.copy(remindersEnabled = enabled) }
    }

    actual suspend fun setSpecifiedTimeOption(option: String) {
        store.setString(option, "specified_time_option")
        store.synchronize()
        update { it.copy(specifiedTimeOption = option) }
    }

    actual suspend fun setUnspecifiedReminderTime(hour: Int, minute: Int) {
        store.setLongLong(hour.toLong(), "unspecified_hour")
        store.setLongLong(minute.toLong(), "unspecified_minute")
        store.synchronize()
        update { it.copy(unspecifiedReminderHour = hour, unspecifiedReminderMinute = minute) }
    }
}

package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.tweener.alarmee.AlarmeeService
import com.tweener.alarmee.model.Alarmee
import com.tweener.alarmee.model.AndroidNotificationConfiguration
import com.tweener.alarmee.model.AndroidNotificationPriority
import com.tweener.alarmee.model.IosNotificationConfiguration
import com.tweener.alarmee.model.RepeatInterval
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    private val dataRepository: DataRepository,
    private val alarmeeService: AlarmeeService,
) : ViewModel() {
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

    private fun cancel(uuid: String) {
        alarmeeService.local.cancel(uuid)
    }

    private fun specifiedUuid(routineId: Long, dayOfWeek: Int) =
        "$UUID_SPECIFIED-$routineId-$dayOfWeek"

    private suspend fun cancelAllSpecifiedReminders() {
        val routines = dataRepository.getAllRoutinesWithTasks().first()
        routines
            .map { it.routine }
            .filter { it.time != null }
            .forEach { routine ->
                val recurrences = dataRepository.getRecurrencesForRoutine(routine.id)
                recurrences.forEach { rec -> cancel(specifiedUuid(routine.id, rec.dayOfWeek)) }
            }
    }

    private fun parseHourMinute(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h to m
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleSpecifiedReminder() {
        val pref = uiState.value
        val offset =
            when (pref.specifiedSelected) {
                "5 min" -> 5.minutes
                "15 min" -> 15.minutes
                "30 min" -> 30.minutes
                "1 hour" -> 1.hours
                "4 hours" -> 4.hours
                else -> 15.minutes
            }

        viewModelScope.launch {
            val routines = dataRepository.getAllRoutinesWithTasks().first()

            val nowInstant = Clock.System.now()
            val tz = TimeZone.currentSystemDefault()

            routines
                .map { it.routine }
                .filter { it.time != null }
                .forEach { routine ->
                    val (hour, minute) = parseHourMinute(routine.time!!)
                    val recurrences = dataRepository.getRecurrencesForRoutine(routine.id)
                    if (recurrences.isEmpty()) return@forEach

                    recurrences.forEach { rec ->
                        val todayLdt = nowInstant.toLocalDateTime(tz)
                        val today = todayLdt.date
                        val todayIso = today.dayOfWeek.isoDayNumber

                        val deltaDays = (rec.dayOfWeek - todayIso + 7) % 7
                        val firstDate = today.plus(DatePeriod(days = deltaDays))

                        val baseFirst =
                            LocalDateTime(
                                year = firstDate.year,
                                month = firstDate.month,
                                dayOfMonth = firstDate.day,
                                hour = hour,
                                minute = minute,
                                second = 0,
                                nanosecond = 0,
                            )

                        var scheduledInstant = baseFirst.toInstant(tz) - offset

                        val periodDays = (rec.intervalWeeks.coerceAtLeast(1)) * 7
                        while (scheduledInstant <= nowInstant) {
                            scheduledInstant += periodDays.toLong().days
                        }

                        val scheduled = scheduledInstant.toLocalDateTime(tz)

                        alarmeeService.local.schedule(
                            alarmee =
                                Alarmee(
                                    uuid = specifiedUuid(routine.id, rec.dayOfWeek),
                                    notificationTitle = "Routine Reminder",
                                    notificationBody = "Soon: ${routine.name}",
                                    scheduledDateTime = scheduled,
                                    repeatInterval =
                                        RepeatInterval.Custom(duration = periodDays.toLong().days),
                                    androidNotificationConfiguration =
                                        AndroidNotificationConfiguration(
                                            priority = AndroidNotificationPriority.HIGH,
                                            channelId = "routineChannel",
                                        ),
                                    iosNotificationConfiguration = IosNotificationConfiguration(),
                                )
                        )
                    }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleDailyUnspecifiedReminder(hour: Int, minute: Int) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val targetToday =
            LocalDateTime(
                year = now.year,
                month = now.month,
                dayOfMonth = now.dayOfMonth,
                hour = hour,
                minute = minute,
                second = 0,
                nanosecond = 0,
            )
        val scheduled =
            if (targetToday > now) {
                targetToday
            } else {
                val nextDate = targetToday.date.plus(DatePeriod(days = 1))
                LocalDateTime(
                    year = nextDate.year,
                    month = nextDate.month,
                    dayOfMonth = nextDate.day,
                    hour = hour,
                    minute = minute,
                    second = 0,
                    nanosecond = 0,
                )
            }
        viewModelScope.launch {
            val count = dataRepository.countRoutinesWithoutTime()
            val body = "You have $count routines to start"

            alarmeeService.local.schedule(
                alarmee =
                    Alarmee(
                        uuid = UUID_UNSPECIFIED,
                        notificationTitle = "Routine Reminder",
                        notificationBody = body,
                        scheduledDateTime = scheduled,
                        repeatInterval = RepeatInterval.Daily,
                        androidNotificationConfiguration =
                            AndroidNotificationConfiguration(
                                priority = AndroidNotificationPriority.HIGH,
                                channelId = "routineChannel",
                            ),
                        iosNotificationConfiguration = IosNotificationConfiguration(),
                    )
            )
        }
    }

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRemindersEnabled(enabled)
            if (enabled) {
                scheduleSpecifiedReminder()
                val h = uiState.value.unspecifiedReminderHour
                val m = uiState.value.unspecifiedReminderMinute
                scheduleDailyUnspecifiedReminder(h, m)
            } else {
                cancelAllSpecifiedReminders()
                cancel(UUID_UNSPECIFIED)
            }
        }
    }

    fun setSpecified(option: String) {
        viewModelScope.launch {
            repository.setSpecifiedTimeOption(option)
            if (uiState.value.remindersEnabled) {
                cancelAllSpecifiedReminders()
                scheduleSpecifiedReminder()
            }
        }
    }

    fun setUnspecified(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setUnspecifiedReminderTime(hour, minute)
            if (uiState.value.remindersEnabled) {
                cancel(UUID_UNSPECIFIED)
                scheduleDailyUnspecifiedReminder(hour, minute)
            }
        }
    }

    companion object {
        const val UUID_SPECIFIED = "specified-reminder"
        const val UUID_UNSPECIFIED = "daily-unspecified-reminder"
        val USER_PREFERENCES_REPOSITORY_KEY =
            object : CreationExtras.Key<UserPreferencesRepository> {}
        val ALARMEE_SERVICE_KEY = object : CreationExtras.Key<AlarmeeService> {}
        val DATA_REPOSITORY_KEY = object : CreationExtras.Key<DataRepository> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val prefsRepository =
                    this[USER_PREFERENCES_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("UserPreferencesRepository not provided")

                val alarmee =
                    this[ALARMEE_SERVICE_KEY]
                        ?: throw IllegalArgumentException("AlarmeeService not provided")
                val dataRepository =
                    this[DATA_REPOSITORY_KEY]
                        ?: throw IllegalArgumentException("DataRepository not provided")
                SettingsViewModel(prefsRepository, dataRepository, alarmee)
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

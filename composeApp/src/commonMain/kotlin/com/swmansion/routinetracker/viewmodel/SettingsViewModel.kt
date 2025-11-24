package com.swmansion.routinetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.data.UserPreferencesRepository
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.RoutineWithTasks
import com.tweener.alarmee.AlarmeeService
import com.tweener.alarmee.model.Alarmee
import com.tweener.alarmee.model.AndroidNotificationConfiguration
import com.tweener.alarmee.model.AndroidNotificationPriority
import com.tweener.alarmee.model.IosNotificationConfiguration
import com.tweener.alarmee.model.RepeatInterval
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.count
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

private const val UUID_SPECIFIED = "specified-reminder"
private const val UUID_UNSPECIFIED = "daily-unspecified-reminder"
private val SPECIFIED_OPTIONS = listOf("5 min", "15 min", "30 min", "1 hour", "4 hours")

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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    private fun cancel(uuid: String) = alarmeeService.local.cancel(uuid)

    private fun specifiedUuid(routineId: Long, dayOfWeek: Int) =
        "$UUID_SPECIFIED-$routineId-$dayOfWeek"

    private suspend fun cancelAllSpecifiedReminders() {
        val routines = dataRepository.getAllRoutinesWithTasks().first()
        routines
            .map(RoutineWithTasks::routine)
            .filter { it.time != null }
            .forEach { routine ->
                dataRepository.getRecurrencesForRoutine(routine.id).forEach { rec ->
                    cancel(specifiedUuid(routine.id, rec.dayOfWeek))
                }
            }
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleSpecifiedReminderForRoutine(
        routine: Routine,
        recurrences: List<RoutineRecurrence>,
        hour: Int,
        minute: Int,
    ) {
        val pref = uiState.value
        val offset = getReminderOffset(pref.specifiedSelected)
        val nowInstant = getCurrentInstant()
        val tz = getCurrentTimeZone()

        recurrences.forEach { rec ->
            scheduleRoutineReminder(routine, rec, hour, minute, offset, nowInstant, tz)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleSpecifiedReminder() {
        val pref = uiState.value
        val offset = getReminderOffset(pref.specifiedSelected)

        viewModelScope.launch {
            val routines = dataRepository.getAllRoutinesWithTasks().first()
            val nowInstant = getCurrentInstant()
            val tz = getCurrentTimeZone()

            routines
                .map(RoutineWithTasks::routine)
                .filter { it.time != null }
                .forEach { routine ->
                    if (routine.time == null) return@forEach
                    val (hour, minute) = parseHourMinute(routine.time)
                    val recurrences = dataRepository.getRecurrencesForRoutine(routine.id)
                    recurrences.forEach { rec ->
                        scheduleRoutineReminder(routine, rec, hour, minute, offset, nowInstant, tz)
                    }
                }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun scheduleRoutineReminder(
        routine: Routine,
        rec: RoutineRecurrence,
        hour: Int,
        minute: Int,
        offset: Duration,
        nowInstant: Instant,
        tz: TimeZone,
    ) {
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
        while (scheduledInstant <= nowInstant) scheduledInstant += periodDays.toLong().days
        val scheduled = scheduledInstant.toLocalDateTime(tz)

        alarmeeService.local.schedule(
            Alarmee(
                uuid = specifiedUuid(routine.id, rec.dayOfWeek),
                notificationTitle = "Routine Reminder",
                notificationBody = "Soon: ${routine.name}",
                scheduledDateTime = scheduled,
                repeatInterval = RepeatInterval.Custom(duration = periodDays.toLong().days),
                androidNotificationConfiguration =
                    AndroidNotificationConfiguration(
                        priority = AndroidNotificationPriority.HIGH,
                        channelId = "routineChannel",
                    ),
                iosNotificationConfiguration = IosNotificationConfiguration(),
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    fun scheduleDailyUnspecifiedReminder(hour: Int, minute: Int) {
        val now = getCurrentInstant().toLocalDateTime(getCurrentTimeZone())
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
            if (targetToday > now) targetToday
            else {
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
            if (count > 0) {
                alarmeeService.local.schedule(
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
    }

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRemindersEnabled(enabled)
            if (enabled) {
                if (dataRepository.countRoutinesWithoutTime() > 0) {
                    scheduleDailyUnspecifiedReminder(
                        uiState.value.unspecifiedReminderHour,
                        uiState.value.unspecifiedReminderMinute,
                    )
                } else if (dataRepository.getAllRoutinesWithTasks().count() > 0) {
                    scheduleSpecifiedReminder()
                }
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

data class SettingsUiState(
    val remindersEnabled: Boolean = false,
    val specifiedOptions: List<String> = SPECIFIED_OPTIONS,
    val specifiedSelected: String = SPECIFIED_OPTIONS.first(),
    val unspecifiedReminderHour: Int = 9,
    val unspecifiedReminderMinute: Int = 0,
)

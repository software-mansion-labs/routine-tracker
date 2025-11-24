package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePickerState
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Task
import com.swmansion.routinetracker.navigation.CreateTask
import com.swmansion.routinetracker.viewmodel.CreateRoutineViewModel
import com.swmansion.routinetracker.viewmodel.SettingsViewModel
import com.swmansion.routinetracker.viewmodel.durationToString
import com.swmansion.routinetracker.viewmodel.parseHourMinute
import com.tweener.alarmee.AlarmeeService
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_add
import routinetracker.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    viewModel: CreateRoutineViewModel =
        viewModel(
            factory = CreateRoutineViewModel.Factory,
            extras =
                MutableCreationExtras().apply {
                    set(
                        CreateRoutineViewModel.DATA_REPOSITORY_KEY,
                        LocalAppContainer.current.repository,
                    )
                },
        ),
    alarmeeService: AlarmeeService,
    navController: NavController,
    settingsViewModel: SettingsViewModel =
        viewModel(
            factory = SettingsViewModel.Factory,
            extras =
                MutableCreationExtras().apply {
                    set(
                        SettingsViewModel.USER_PREFERENCES_REPOSITORY_KEY,
                        LocalAppContainer.current.userPreferencesRepository,
                    )
                    set(SettingsViewModel.ALARMEE_SERVICE_KEY, alarmeeService)
                    set(SettingsViewModel.DATA_REPOSITORY_KEY, LocalAppContainer.current.repository)
                },
        ),
) {
    val uiState by viewModel.uiState.collectAsState()
    val timePickerState = rememberAdaptiveTimePickerState()
    val remindersEnabled = settingsViewModel.uiState.collectAsState().value.remindersEnabled

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                ActionButtons(
                    isLoading = uiState.isLoading,
                    onCreate = {
                        viewModel.createRoutine { _, routine, recurrences ->
                            if (remindersEnabled) {
                                if (!routine.time.isNullOrEmpty() && recurrences.isNotEmpty()) {
                                    settingsViewModel.scheduleSpecifiedReminderForRoutine(
                                        routine,
                                        recurrences,
                                        parseHourMinute(routine.time).first,
                                        parseHourMinute(routine.time).second,
                                    )
                                } else {
                                    settingsViewModel.scheduleDailyUnspecifiedReminder(
                                        settingsViewModel.uiState.value.unspecifiedReminderHour,
                                        settingsViewModel.uiState.value.unspecifiedReminderMinute,
                                    )
                                }
                            }
                            navController.popBackStack()
                        }
                    },
                    onDiscard = navController::popBackStack,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RoutineNameField(
                routineName = uiState.routineName,
                onNameChange = viewModel::updateRoutineName,
                onErrorClear = viewModel::clearMessages,
                isError = uiState.errorMessage != null,
            )

            TimeSelectionButton(
                selectedTimeText = viewModel.getFormattedTime(),
                onTimeClick = { viewModel.updateVisibilityTimePicker(true) },
            )

            if (uiState.showTimePicker) {
                TimePickerDialog(
                    timePickerState = timePickerState,
                    onDone = { viewModel.setTime(timePickerState.hour, timePickerState.minute) },
                    onDismiss = { viewModel.updateVisibilityTimePicker(false) },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            RecurrenceSection(
                selectedDaysOfWeek = uiState.selectedDaysOfWeek,
                onDaysChange = viewModel::updateSelectedDaysOfWeek,
                intervalWeeks = uiState.intervalWeeks,
                onIntervalChange = viewModel::updateIntervalWeeks,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            TaskSection(uiState.tasks, navController)

            uiState.errorMessage?.let { ErrorMessageCard(message = it) }
            uiState.successMessage?.let { SuccessMessageCard(message = it) }
        }
    }
}

@Composable
private fun RoutineNameField(
    routineName: String,
    onNameChange: (String) -> Unit,
    onErrorClear: () -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = routineName,
        onValueChange = {
            onNameChange(it)
            onErrorClear()
        },
        label = { Text("Routine Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
    )
}

@Composable
private fun TimeSelectionButton(selectedTimeText: String?, onTimeClick: () -> Unit) {
    Button(onClick = onTimeClick, modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Text(
            text = selectedTimeText ?: "Select Time (optional)",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimePickerDialog(
    timePickerState: AdaptiveTimePickerState,
    onDone: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a Time") },
        text = { AdaptiveTimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = onDone) { Text("Done") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun RecurrenceSection(
    selectedDaysOfWeek: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit,
    intervalWeeks: Float,
    onIntervalChange: (Float) -> Unit,
) {
    Column {
        DaysOfWeekSelector(selectedDaysOfWeek = selectedDaysOfWeek, onDaysChange = onDaysChange)

        Spacer(modifier = Modifier.height(16.dp))

        IntervalWeeksSelector(intervalWeeks = intervalWeeks, onIntervalChange = onIntervalChange)
    }
}

@Composable
private fun DaysOfWeekSelector(
    selectedDaysOfWeek: Set<DayOfWeek>,
    onDaysChange: (Set<DayOfWeek>) -> Unit,
) {
    Column {
        Text(text = "Days of Week (optional)", style = MaterialTheme.typography.bodyMedium)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                FilterChip(
                    selected = selectedDaysOfWeek.contains(dayOfWeek),
                    onClick = {
                        onDaysChange(
                            if (selectedDaysOfWeek.contains(dayOfWeek)) {
                                selectedDaysOfWeek - dayOfWeek
                            } else {
                                selectedDaysOfWeek + dayOfWeek
                            }
                        )
                    },
                    label = { Text(dayOfWeek.displayName) },
                )
            }
        }
    }
}

@Composable
private fun IntervalWeeksSelector(intervalWeeks: Float, onIntervalChange: (Float) -> Unit) {
    Column {
        Text(
            text = "Repeat every ${intervalWeeks.toInt()} week(s) (optional)",
            style = MaterialTheme.typography.bodyMedium,
        )
        Slider(
            value = intervalWeeks,
            onValueChange = onIntervalChange,
            valueRange = 0f..4f,
            steps = 3,
        )
    }
}

@Composable
private fun TaskSection(tasks: List<Task>, navController: NavController) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(text = "Tasks: ${tasks.size}", modifier = Modifier.padding(16.dp))
            tasks.forEach { task ->
                Surface(
                    tonalElevation = 1.dp,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 12.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(task.name, style = MaterialTheme.typography.bodyLarge)
                        Text(task.duration?.let(::durationToString).orEmpty())
                    }
                }
            }
            Button(
                onClick = { navController.navigate(CreateTask) },
                modifier = Modifier.padding(16.dp).fillMaxWidth().height(48.dp),
            ) {
                Row {
                    Icon(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = "Add task",
                    )
                    Text(text = "Add task", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun ErrorMessageCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun SuccessMessageCard(message: String) {
    Card(
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
private fun ActionButtons(isLoading: Boolean, onCreate: () -> Unit, onDiscard: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onCreate,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp,
                )
            } else {
                Text("Create", style = MaterialTheme.typography.bodyLarge)
            }
        }

        TextButton(
            onClick = onDiscard,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading,
        ) {
            Text(text = "Discard", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

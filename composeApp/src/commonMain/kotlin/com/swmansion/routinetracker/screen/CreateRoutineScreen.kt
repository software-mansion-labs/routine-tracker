package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePickerState
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(onNavigateBack: () -> Unit) {
    val appContainer = LocalAppContainer.current
    val repository = appContainer.repository
    val scope = rememberCoroutineScope()

    var routineName by remember { mutableStateOf("") }
    val timePickerState = rememberAdaptiveTimePickerState()
    var isTimeSet by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDaysOfWeek by remember { mutableStateOf<Set<DayOfWeek>>(emptySet()) }
    var intervalWeeks by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Create Routine") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RoutineNameField(
                routineName = routineName,
                onNameChange = { routineName = it },
                onErrorClear = {
                    errorMessage = null
                    successMessage = null
                },
                isError = errorMessage != null,
            )

            TimeSelectionButton(
                selectedTimeText =
                    if (isTimeSet) formatTime(timePickerState.hour, timePickerState.minute)
                    else null,
                onTimeClick = { showTimePicker = true },
            )

            if (showTimePicker) {
                TimePickerDialog(
                    timePickerState = timePickerState,
                    onDone = {
                        isTimeSet = true
                        showTimePicker = false
                        errorMessage = null
                        successMessage = null
                    },
                    onDismiss = { showTimePicker = false },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            RecurrenceSection(
                selectedDaysOfWeek = selectedDaysOfWeek,
                onDaysChange = { selectedDaysOfWeek = it },
                intervalWeeks = intervalWeeks,
                onIntervalChange = { intervalWeeks = it },
            )

            errorMessage?.let { ErrorMessageCard(message = it) }
            successMessage?.let { SuccessMessageCard(message = it) }

            Spacer(modifier = Modifier.weight(1f))

            ActionButtons(
                isLoading = isLoading,
                onCreate = {
                    if (routineName.isBlank()) {
                        errorMessage = "Routine name is required"
                        return@ActionButtons
                    }

                    isLoading = true
                    errorMessage = null
                    successMessage = null

                    scope.launch {
                        try {
                            val timeString =
                                if (isTimeSet)
                                    formatTime(timePickerState.hour, timePickerState.minute)
                                else null

                            val routine = Routine(name = routineName.trim(), time = timeString)

                            val recurrences =
                                selectedDaysOfWeek.map { dayOfWeek ->
                                    RoutineRecurrence(
                                        routineId = 0,
                                        dayOfWeek = dayOfWeek.value,
                                        intervalWeeks = intervalWeeks.toInt(),
                                    )
                                }

                            val routineId =
                                repository.createRoutineWithRecurrence(routine, recurrences)
                            successMessage =
                                "Routine '${routine.name}' with ID: $routineId created successfully!"
                            routineName = ""
                            isTimeSet = false
                            selectedDaysOfWeek = emptySet()
                            intervalWeeks = 1f

                            onNavigateBack()
                        } catch (e: Exception) {
                            errorMessage = "Failed to create routine: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onDiscard = onNavigateBack,
            )
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
        Text(text = selectedTimeText ?: "Select Time", style = MaterialTheme.typography.bodyLarge)
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
        Text(text = "Days of Week", style = MaterialTheme.typography.bodyMedium)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DayOfWeek.allDays.forEach { dayOfWeek ->
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
            text = "Repeat every ${intervalWeeks.toInt()} week(s)",
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

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

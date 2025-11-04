package com.swmansion.routinetracker.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import kotlinx.coroutines.delay
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
    var selectedDaysOfWeek by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var intervalWeeks by remember { mutableStateOf(0f) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val daysOfWeek =
        listOf(
            "Mon" to 1,
            "Tue" to 2,
            "Wed" to 3,
            "Thu" to 4,
            "Fri" to 5,
            "Sat" to 6,
            "Sun" to 7,
        )

    val selectedTimeText =
        if (isTimeSet) {
            "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
        } else {
            null
        }

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
            OutlinedTextField(
                value = routineName,
                onValueChange = {
                    routineName = it
                    errorMessage = null
                    successMessage = null
                },
                label = { Text("Routine Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null,
            )

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    text = selectedTimeText ?: "Select Time",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            if (showTimePicker) {
                AlertDialog(
                    onDismissRequest = { showTimePicker = false },
                    title = { Text("Pick a Time") },
                    text = {
                        AdaptiveTimePicker(
                            state = timePickerState,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                isTimeSet = true
                                showTimePicker = false
                                errorMessage = null
                                successMessage = null
                            },
                        ) {
                            Text("Done")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showTimePicker = false },
                        ) {
                            Text("Cancel")
                        }
                    },
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
            )

            Column {
                Text(
                    text = "Days of Week",
                    style = MaterialTheme.typography.bodyMedium,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    daysOfWeek.forEach { (dayName, dayValue) ->
                        FilterChip(
                            selected = selectedDaysOfWeek.contains(dayValue),
                            onClick = {
                                selectedDaysOfWeek =
                                    if (selectedDaysOfWeek.contains(dayValue)) {
                                        selectedDaysOfWeek - dayValue
                                    } else {
                                        selectedDaysOfWeek + dayValue
                                    }
                            },
                            label = { Text(dayName) },
                        )
                    }
                }
            }

            Column {
                Text(
                    text = "Repeat every ${intervalWeeks.toInt()} week(s)",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Slider(
                    value = intervalWeeks,
                    onValueChange = { intervalWeeks = it },
                    valueRange = 0f..4f,
                    steps = 3,
                )
            }

            if (errorMessage != null) {
                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            if (successMessage != null) {
                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = successMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        if (routineName.isBlank()) {
                            errorMessage = "Routine name is required"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null
                        successMessage = null

                        scope.launch {
                            try {
                                val timeString =
                                    if (isTimeSet) {
                                        "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                                    } else {
                                        null
                                    }

                                val routine = Routine(name = routineName.trim(), time = timeString)
                                
                                val recurrences =
                                    selectedDaysOfWeek.map { dayOfWeek ->
                                        RoutineRecurrence(
                                            routineId = 0,
                                            dayOfWeek = dayOfWeek,
                                            intervalWeeks = intervalWeeks.toInt(),
                                        )
                                    }

                                val routineId = repository.createRoutineWithRecurrence(routine, recurrences)
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
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isLoading,
                ) {
                    Text(text = "Discard", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

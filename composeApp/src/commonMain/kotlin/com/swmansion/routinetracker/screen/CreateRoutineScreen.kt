package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.Routine
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
    val timePickerState =
        rememberTimePickerState(initialHour = 12, initialMinute = 0, is24Hour = true)
    var isTimeSet by remember { mutableStateOf(false) }
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
            OutlinedTextField(
                value = routineName,
                onValueChange = {
                    routineName = it
                    errorMessage = null
                    successMessage = null
                },
                label = { Text("Routine Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                TimeInput(
                    state = timePickerState,
                    modifier = Modifier,
                    colors = TimePickerDefaults.colors(),
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
                                val routineId = repository.createRoutine(routine)
                                successMessage =
                                    "Routine '${routine.name}' with ID: $routineId created successfully!"
                                routineName = ""
                                isTimeSet = false

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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
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

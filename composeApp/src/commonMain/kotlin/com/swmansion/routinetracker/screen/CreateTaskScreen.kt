package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
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
import com.swmansion.routinetracker.viewmodel.CreateTaskViewModel
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: CreateTaskViewModel =
        viewModel(
            factory = CreateTaskViewModel.Factory,
            extras =
                MutableCreationExtras().apply {
                    set(
                        CreateTaskViewModel.DATA_REPOSITORY_KEY,
                        LocalAppContainer.current.repository,
                    )
                },
        ),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val timePickerState = rememberAdaptiveTimePickerState(is24Hour = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
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
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RoutineNameField(
                routineName = uiState.taskName,
                onNameChange = viewModel::updateTaskName,
                onErrorClear = viewModel::clearMessages,
                isError = uiState.errorMessage != null,
            )

            DurationSelectionButton(
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

            uiState.errorMessage?.let { ErrorMessageCard(message = it) }
            uiState.successMessage?.let { SuccessMessageCard(message = it) }

            Spacer(modifier = Modifier.weight(1f))

            ActionButtons(
                isLoading = uiState.isLoading,
                onCreate = {
                    viewModel.createTask { name, duration ->
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("task_name", name)
                            set("task_duration", duration)
                        }
                        navController.popBackStack()
                    }
                },
                onDiscard = { navController.popBackStack() },
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
        label = { Text("Task Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
    )
}

@Composable
private fun DurationSelectionButton(selectedTimeText: String?, onTimeClick: () -> Unit) {
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
        title = { Text("Pick Duration") },
        text = { AdaptiveTimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = onDone) { Text("Done") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
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

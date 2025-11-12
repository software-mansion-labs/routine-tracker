package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePickerState
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var remindersEnabled by remember { mutableStateOf(false) }

    val specifiedOptions = listOf("5 min", "15 min", "30 min", "1 hour", "4 hours")
    var specifiedExpanded by remember { mutableStateOf(false) }
    var specifiedSelected by remember { mutableStateOf(specifiedOptions.first()) }

    var showUnspecifiedPicker by remember { mutableStateOf(false) }
    var unspecifiedHour by remember { mutableStateOf(9) }
    var unspecifiedMinute by remember { mutableStateOf(0) }
    val timePickerState =
        rememberAdaptiveTimePickerState(
            initialHour = unspecifiedHour,
            initialMinute = unspecifiedMinute,
        )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
        contentWindowInsets = WindowInsets(0.dp),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            RemindersToggleRow(
                checked = remindersEnabled,
                onCheckedChange = { remindersEnabled = it },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            if (remindersEnabled) {
                SpecifiedTimeRow(
                    specifiedSelected = specifiedSelected,
                    specifiedExpanded = specifiedExpanded,
                    specifiedOptions = specifiedOptions,
                    onExpandedChange = { specifiedExpanded = it },
                    onOptionSelected = {
                        specifiedSelected = it
                        specifiedExpanded = false
                    },
                )

                UnspecifiedTimeRow(
                    timeText = formatTime(unspecifiedHour, unspecifiedMinute),
                    onOpenPicker = { showUnspecifiedPicker = true },
                )
            }

            if (showUnspecifiedPicker) {
                TimePickerDialog(
                    timePickerState = timePickerState,
                    onDone = {
                        unspecifiedHour = timePickerState.hour
                        unspecifiedMinute = timePickerState.minute
                        showUnspecifiedPicker = false
                    },
                    onDismiss = { showUnspecifiedPicker = false },
                )
            }
        }
    }
}

@Composable
private fun RemindersToggleRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Reminders", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
            Text(
                text = if (checked) "On" else "Off",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpecifiedTimeRow(
    specifiedSelected: String,
    specifiedExpanded: Boolean,
    specifiedOptions: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Routine with specified time",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        ExposedDropdownMenuBox(
            expanded = specifiedExpanded,
            onExpandedChange = { onExpandedChange(!specifiedExpanded) },
            modifier = Modifier.widthIn(max = 140.dp),
        ) {
            TextField(
                readOnly = true,
                value = specifiedSelected,
                onValueChange = {},
                label = { Text("Reminder") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = specifiedExpanded)
                },
                modifier = Modifier.menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = specifiedExpanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                specifiedOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onOptionSelected(option) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnspecifiedTimeRow(timeText: String, onOpenPicker: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Routine with unspecified time",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )

        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { onOpenPicker() },
            modifier = Modifier.widthIn(max = 140.dp),
        ) {
            TextField(
                readOnly = true,
                value = timeText,
                onValueChange = {},
                label = { Text("Reminder") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier.menuAnchor(),
            )
        }
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
        title = { Text("Pick reminder time") },
        text = { AdaptiveTimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = onDone) { Text("Done") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun formatTime(hour: Int, minute: Int): String {
    val hh = hour.toString().padStart(2, '0')
    val mm = minute.toString().padStart(2, '0')
    return "$hh:$mm"
}

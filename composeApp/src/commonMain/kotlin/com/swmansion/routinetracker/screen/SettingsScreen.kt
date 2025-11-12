package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var remindersEnabled by remember { mutableStateOf(false) }

    val specifiedOptions =
        listOf("At time", "5 min before", "15 min before", "30 min before", "1 hour before")
    val unspecifiedOptions = listOf("Morning", "Afternoon", "Evening", "Any time")

    var specifiedExpanded by remember { mutableStateOf(false) }
    var specifiedSelected by remember { mutableStateOf(specifiedOptions.first()) }

    var unspecifiedExpanded by remember { mutableStateOf(false) }
    var unspecifiedSelected by remember { mutableStateOf(unspecifiedOptions.first()) }

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
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Reminders", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = if (remindersEnabled) "On" else "Off",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

            if (remindersEnabled) {
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
                        onExpandedChange = { specifiedExpanded = !specifiedExpanded },
                        modifier = Modifier.widthIn(max = 140.dp),
                    ) {
                        TextField(
                            readOnly = true,
                            value = specifiedSelected,
                            onValueChange = {},
                            label = { Text("Reminder") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = specifiedExpanded
                                )
                            },
                            modifier = Modifier.menuAnchor(),
                        )
                        ExposedDropdownMenu(
                            expanded = specifiedExpanded,
                            onDismissRequest = { specifiedExpanded = false },
                        ) {
                            specifiedOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        specifiedSelected = option
                                        specifiedExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

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
                        expanded = unspecifiedExpanded,
                        onExpandedChange = { unspecifiedExpanded = !unspecifiedExpanded },
                        modifier = Modifier.widthIn(max = 140.dp),
                    ) {
                        TextField(
                            readOnly = true,
                            value = unspecifiedSelected,
                            onValueChange = {},
                            label = { Text("Reminder") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = unspecifiedExpanded
                                )
                            },
                            modifier = Modifier.menuAnchor(),
                        )
                        ExposedDropdownMenu(
                            expanded = unspecifiedExpanded,
                            onDismissRequest = { unspecifiedExpanded = false },
                        ) {
                            unspecifiedOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        unspecifiedSelected = option
                                        unspecifiedExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

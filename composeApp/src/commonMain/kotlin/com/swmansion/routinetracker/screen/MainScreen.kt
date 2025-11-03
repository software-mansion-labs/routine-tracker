package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_add

sealed class Screen {
    data object CreateRoutine : Screen()
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.CreateRoutine) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add),
                            contentDescription = "Add",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text("Create") },
                    selected = selectedScreen is Screen.CreateRoutine,
                    onClick = { selectedScreen = Screen.CreateRoutine },
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (selectedScreen) {
                is Screen.CreateRoutine ->
                    CreateRoutineScreen(onNavigateBack = { selectedScreen = Screen.CreateRoutine })
            }
        }
    }
}

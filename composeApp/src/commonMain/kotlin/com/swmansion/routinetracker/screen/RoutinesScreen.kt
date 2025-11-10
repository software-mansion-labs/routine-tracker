package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.RoutineWithTasks
import com.swmansion.routinetracker.navigation.CreateRoutine
import com.swmansion.routinetracker.viewmodel.HomeViewModel
import com.swmansion.routinetracker.viewmodel.durationToString
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    navController: NavController,
    viewModel: HomeViewModel =
        viewModel(
            factory = HomeViewModel.Factory,
            extras =
                MutableCreationExtras().apply {
                    set(HomeViewModel.DATA_REPOSITORY_KEY, LocalAppContainer.current.repository)
                },
        ),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Routines") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(CreateRoutine) },
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = "Create Routine",
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        if (uiState.routinesWithTasks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No routines yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items = uiState.routinesWithTasks, key = { it.routine.id }) { routineWithTasks
                    ->
                    RoutineItem(routine = routineWithTasks)
                }
            }
        }
    }
}

@Composable
private fun RoutineItem(routine: RoutineWithTasks) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = routine.routine.name, style = MaterialTheme.typography.titleMedium)
                if (routine.routine.time != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.routine.time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                routine.tasks.forEach { task ->
                    Surface(
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(task.name, style = MaterialTheme.typography.bodyLarge)
                            Text(durationToString(task.duration) ?: "")
                        }
                    }
                }
            }
        }
    }
}

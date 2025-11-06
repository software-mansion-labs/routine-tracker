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
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.Routine
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_add
import routinetracker.composeapp.generated.resources.ic_home

enum class NavigationDestination {
    HOME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val appContainer = LocalAppContainer.current
    val repository = appContainer.repository
    val routinesFlow = repository.getAllRoutines()
    val routines by routinesFlow.collectAsState(initial = emptyList())

    var selectedDestination by remember { mutableStateOf(NavigationDestination.HOME) }

    val onCreateRoutineClick = CreateRoutineScreen()


    Scaffold(
        topBar = { TopAppBar(title = { Text("My Routines") }) },
        bottomBar = {
            NavigationBar(modifier = Modifier.height(84.dp), windowInsets = WindowInsets(0.dp)) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_home),
                            contentDescription = "Home",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text("Home") },
                    selected = selectedDestination == NavigationDestination.HOME,
                    onClick = { selectedDestination = NavigationDestination.HOME },
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateRoutineClick },
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = "Create Routine",
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        if (routines.isEmpty()) {
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
                items(routines) { routine -> RoutineItem(routine = routine) }
            }
        }
    }
}

@Composable
private fun RoutineItem(routine: Routine) {
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
                Text(text = routine.name, style = MaterialTheme.typography.titleMedium)
                if (routine.time != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

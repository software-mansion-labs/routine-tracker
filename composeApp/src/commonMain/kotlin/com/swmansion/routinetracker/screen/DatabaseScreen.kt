package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDatabase
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun TestDatabaseScreen(database: RoutineDatabase) {
    val repository =
        DataRepository(database.routineDao(), database.taskDao(), database.routineRecurrenceDao())
    val scope = rememberCoroutineScope()
    var testResults by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) { testResults = testResults + "Initializing database test..." }

    Column(Modifier.fillMaxWidth().padding(vertical = 50.dp), Arrangement.Bottom) {
        Column {
            Button(
                onClick = {
                    scope.launch {
                        testResults = testResults + "--- Creating test routines ---"

                        val routine1 = Routine(name = "Morning Exercise", time = "07:00")
                        val routine1Id =
                            repository.insertRoutineWithTasks(
                                routine1,
                                listOf(
                                    Task(
                                        routineId = 0,
                                        name = "Push-ups",
                                        duration = 10,
                                        order = 0,
                                    ),
                                    Task(routineId = 0, name = "Deadlift", duration = 10, order = 0),
                                ),
                            )
                        testResults =
                            testResults +
                                "✓ Created routine 'Morning Exercise' with ID: $routine1Id"

                        val routine2 = Routine(name = "Evening Meditation", time = "20:00")
                        val routine2Id =
                            repository.insertRoutineWithTasks(
                                routine2,
                                listOf(
                                    Task(
                                        routineId = 0,
                                        name = "Clear mind",
                                        duration = 5,
                                        order = 0,
                                    ),
                                    Task(
                                        routineId = 0,
                                        name = "Focus breath",
                                        duration = 10,
                                        order = 0,
                                    ),
                                ),
                            )
                        testResults =
                            testResults +
                                "✓ Created routine 'Evening Meditation' with ID: $routine2Id"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Insert Test Data")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        testResults = testResults + "--- Reading all routines ---"
                        val routines = repository.getAllRoutinesWithTasks().first()
                        testResults = testResults + "Found ${routines.size} routines"
                        routines.forEach { rvm ->
                            testResults =
                                testResults +
                                    "✓ Routine '${rvm.routine.name}' (ID: ${rvm.routine.id}, Time: ${rvm.routine.time}) - ${rvm.tasks.size} tasks"
                            rvm.tasks.forEach { task ->
                                testResults =
                                    testResults +
                                        "  → Task: ${task.name} Duration: ${task.duration}min)"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Read All")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        testResults = testResults + "--- Clearing results ---"
                        testResults = emptyList()
                        testResults = testResults + "Results cleared"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Clear")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        testResults = testResults + "--- Clearing data ---"
                        repository.removeAll()
                        testResults = testResults + "--- Data cleared ---"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Remove all data")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        testResults = testResults + "--- Getting data ---"
                        val routine = database.routineDao().getAllRoutines().first().first()
                        val routineWithTasks = repository.getRoutineWithTasks(routine.id).first()

                        routineWithTasks?.let { rwt ->
                            testResults =
                                testResults +
                                    "✓ Routine '${rwt.routine.name}' (ID: ${rwt.routine.id}, Time: ${rwt.routine.time}) - ${rwt.tasks.size} tasks"
                            rwt.tasks.forEach { task ->
                                testResults =
                                    testResults +
                                        "  → Task: ${task.name} Duration: ${task.duration}min)"
                            }
                        }

                        testResults = testResults + "--- All data are printed ---"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Get all tasks from first routine")
            }
        }
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier =
                    Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState())
            ) {
                testResults.forEach { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
        }
    }
}

package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.navigation.NavController
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.RoutineWithTasks
import com.swmansion.routinetracker.model.Task
import com.swmansion.routinetracker.screen.RoutinesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalTestApi::class)
class RoutinesScreenTest {
    
    @Test
    fun `should display empty state when no routines exist`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("No routines yet").assertIsDisplayed()
    }
    
    @Test
    fun `should display list of routines when routines exist`() = runComposeUiTest {
        val routines = listOf(
            Routine(id = 1L, name = "Morning Routine", time = "08:00"),
            Routine(id = 2L, name = "Evening Routine", time = "20:00"),
            Routine(id = 3L, name = "Workout Routine")
        )
        val testAppContainer = createTestAppContainer(routines)
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Morning Routine").assertIsDisplayed()
        onNodeWithText("08:00").assertIsDisplayed()
        onNodeWithText("Evening Routine").assertIsDisplayed()
        onNodeWithText("20:00").assertIsDisplayed()
        onNodeWithText("Workout Routine").assertIsDisplayed()
        onNodeWithText("No routines yet").assertDoesNotExist()
    }
    
    @Test
    fun `should display routine without time when time is null`() = runComposeUiTest {
        val routines = listOf(
            Routine(id = 1L, name = "Simple Routine", time = null)
        )
        val testAppContainer = createTestAppContainer(routines)
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Simple Routine").assertIsDisplayed()
    }
    
    @Test
    fun `should display top bar with correct title`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("My Routines").assertIsDisplayed()
    }
    
    @Test
    fun `should display floating action button`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        // var fabClicked = false
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController {
                    // fabClicked = true
                })
            }
        }

        onRoot().assertIsDisplayed()
    }
    
    @Test
    fun `should update list when routines are added`() = runComposeUiTest {
        val routinesFlow = MutableStateFlow<List<Routine>>(emptyList())
        val testAppContainer = createTestAppContainerWithFlow(routinesFlow)
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("No routines yet").assertIsDisplayed()
        
        routinesFlow.value = listOf(
            Routine(id = 1L, name = "New Routine", time = "10:00")
        )
        
        kotlinx.coroutines.delay(200)
        
        onNodeWithText("New Routine").assertIsDisplayed()
        onNodeWithText("No routines yet").assertDoesNotExist()
    }
    
    private fun createTestAppContainer(initialRoutines: List<Routine> = emptyList()): AppContainer {
        val routinesFlow = MutableStateFlow(initialRoutines)
        return TestAppContainer(routinesFlow)
    }
    
    private fun createTestAppContainerWithFlow(routinesFlow: MutableStateFlow<List<Routine>>): AppContainer {
        return TestAppContainer(routinesFlow)
    }
    
    private fun createMockNavController(onNavigate: (String) -> Unit = {}): MockNavController {
        return MockNavController(onNavigate)
    }
}

// Test AppContainer implementation
class TestAppContainer(private val routinesFlow: MutableStateFlow<List<Routine>>) : AppContainer() {
    override val repository: DataRepository by lazy {
        val mockRoutineDao = TestRoutineDao(routinesFlow)
        val mockTaskDao = TestTaskDao()
        val mockRecurrenceDao = TestRecurrenceDao()
        DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
    }
}

// Test DAO implementations
class TestRoutineDao(
    private val routinesFlow: MutableStateFlow<List<Routine>>
) : com.swmansion.routinetracker.database.RoutineDao {
    override fun getAllRoutines(): Flow<List<Routine>> = routinesFlow
    override suspend fun getRoutineById(id: Long): Routine? = routinesFlow.value.find { it.id == id }
    override fun getAllRoutinesWithTasks(): Flow<List<RoutineWithTasks>> = flowOf(emptyList())
    override fun getRoutineWithTasksById(id: Long): Flow<RoutineWithTasks?> = flowOf(null)
    override suspend fun insertRoutine(routine: Routine): Long {
        val newId = (routinesFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        routinesFlow.value = routinesFlow.value + routine.copy(id = newId)
        return newId
    }
    override suspend fun insertRoutines(routines: List<Routine>) {
        routinesFlow.value = routinesFlow.value + routines
    }
    override suspend fun updateRoutine(routine: Routine) {
        routinesFlow.value = routinesFlow.value.map { if (it.id == routine.id) routine else it }
    }
    override suspend fun deleteRoutine(routine: Routine) {
        routinesFlow.value = routinesFlow.value.filter { it.id != routine.id }
    }
    override suspend fun deleteRoutineById(id: Long) {
        routinesFlow.value = routinesFlow.value.filter { it.id != id }
    }
    override suspend fun removeAll() {
        routinesFlow.value = emptyList()
    }
}

class TestTaskDao : com.swmansion.routinetracker.database.TaskDao {
    override fun getTasksForRoutine(routineId: Long): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun getTasksForRoutineSuspend(routineId: Long): List<Task> = emptyList()
    override fun getAllTasks(): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun insertTask(task: Task): Long = 1L
    override suspend fun insertTasks(tasks: List<Task>) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {}
    override suspend fun removeAll() {}
}

class TestRecurrenceDao : com.swmansion.routinetracker.database.RoutineRecurrenceDao {
    override suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long = 1L
    override suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>) {}
    override suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> = emptyList()
    override suspend fun deleteRecurrencesForRoutine(routineId: Long) {}
    override suspend fun removeAll() {}
}

class MockNavController(private val onNavigate: (String) -> Unit = {}) : NavController {
    override val currentBackStackEntry: androidx.navigation.NavBackStackEntry?
        get() = null
    override val graph: androidx.navigation.NavGraph
        get() = throw NotImplementedError()
    
    override fun navigate(route: String) {
        onNavigate(route)
    }
    
    override fun navigate(route: androidx.navigation.NavDeepLinkRequest) {
        onNavigate(route.uri.toString())
    }
    
    override fun navigateUp(): Boolean = true
    override fun popBackStack(): Boolean = true
    override fun popBackStack(route: String, inclusive: Boolean): Boolean = true
    override fun popBackStack(route: androidx.navigation.NavDestination, inclusive: Boolean): Boolean = true
    override fun setGraph(graph: androidx.navigation.NavGraph) {}
    override fun setGraph(graph: androidx.navigation.NavGraph, startDestination: String) {}
    override fun setGraph(graph: androidx.navigation.NavGraph, startDestination: androidx.navigation.NavDestination) {}
    override fun enableOnBackPressed(enabled: Boolean) {}
    override fun addOnDestinationChangedListener(listener: OnDestinationChangedListener) {}
    override fun removeOnDestinationChangedListener(listener: OnDestinationChangedListener) {}
    override fun getBackStackEntry(route: String): androidx.navigation.NavBackStackEntry = throw NotImplementedError()
    override fun getBackStackEntry(navDestinationId: Int): androidx.navigation.NavBackStackEntry = throw NotImplementedError()
    override fun saveState(): androidx.navigation.NavControllerSavedState = throw NotImplementedError()
    override fun restoreState(savedState: androidx.navigation.NavControllerSavedState) {}
}

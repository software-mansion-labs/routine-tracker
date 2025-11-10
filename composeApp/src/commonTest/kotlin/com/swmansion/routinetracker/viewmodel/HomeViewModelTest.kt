package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.RoutineWithTasks
import com.swmansion.routinetracker.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow

class HomeViewModelTest {
    
    @Test
    fun `uiState should initially be empty`() = runTest {
        val repository = createMockRepository(flowOf(emptyList()))
        val viewModel = HomeViewModel(repository)
        
        assertTrue(viewModel.uiState.value.routines.isEmpty())
    }
    
    @Test
    fun `uiState should reflect routines from repository`() = runTest {
        val routines = listOf(
            Routine(id = 1L, name = "Morning Routine", time = "08:00"),
            Routine(id = 2L, name = "Evening Routine", time = "20:00"),
            Routine(id = 3L, name = "Workout Routine")
        )
        val repository = createMockRepository(flowOf(routines))
        val viewModel = HomeViewModel(repository)
        
        kotlinx.coroutines.delay(100)
        
        assertEquals(3, viewModel.uiState.value.routines.size)
        assertEquals("Morning Routine", viewModel.uiState.value.routines[0].name)
        assertEquals("Evening Routine", viewModel.uiState.value.routines[1].name)
        assertEquals("Workout Routine", viewModel.uiState.value.routines[2].name)
    }
    
    @Test
    fun `uiState should update when repository emits new routines`() = runTest {
        val stateFlow = MutableStateFlow<List<Routine>>(emptyList())
        val repository = createMockRepository(stateFlow)
        val viewModel = HomeViewModel(repository)
        
        assertTrue(viewModel.uiState.value.routines.isEmpty())
        
        val routines = listOf(
            Routine(id = 1L, name = "Routine 1"),
            Routine(id = 2L, name = "Routine 2")
        )
        stateFlow.value = routines
        
        kotlinx.coroutines.delay(100)
        
        assertEquals(2, viewModel.uiState.value.routines.size)
        assertEquals("Routine 1", viewModel.uiState.value.routines[0].name)
        assertEquals("Routine 2", viewModel.uiState.value.routines[1].name)
        
        val updatedRoutines = listOf(
            Routine(id = 1L, name = "Routine 1"),
            Routine(id = 2L, name = "Routine 2"),
            Routine(id = 3L, name = "Routine 3")
        )
        stateFlow.value = updatedRoutines
        
        kotlinx.coroutines.delay(100)
        
        assertEquals(3, viewModel.uiState.value.routines.size)
        assertEquals("Routine 3", viewModel.uiState.value.routines[2].name)
    }
    
    @Test
    fun `uiState should handle empty list from repository`() = runTest {
        val repository = createMockRepository(flowOf(emptyList()))
        val viewModel = HomeViewModel(repository)
        
        kotlinx.coroutines.delay(100)
        
        assertTrue(viewModel.uiState.value.routines.isEmpty())
    }
    
    @Test
    fun `uiState should maintain routine order from repository`() = runTest {
        val routines = listOf(
            Routine(id = 3L, name = "C Routine"),
            Routine(id = 1L, name = "A Routine"),
            Routine(id = 2L, name = "B Routine")
        )
        val repository = createMockRepository(flowOf(routines))
        val viewModel = HomeViewModel(repository)
        
        kotlinx.coroutines.delay(100)
        
        assertEquals(3, viewModel.uiState.value.routines.size)

        assertEquals("A Routine", viewModel.uiState.value.routines[0].name)
        assertEquals("B Routine", viewModel.uiState.value.routines[1].name)
        assertEquals("C Routine", viewModel.uiState.value.routines[2].name)
    }
    
    private fun createMockRepository(
        routinesFlow: Flow<List<Routine>>
    ): DataRepository {
        val mockRoutineDao = MockRoutineDao(1L, routinesFlow)
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        return DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
    }
}

// Reuse mock DAOs
class MockRoutineDao(
    private val insertResult: Long,
    private val routinesFlow: Flow<List<Routine>>
) : RoutineDao {
    override fun getAllRoutines(): Flow<List<Routine>> = routinesFlow
    override suspend fun getRoutineById(id: Long): Routine? = null
    override fun getAllRoutinesWithTasks(): Flow<List<RoutineWithTasks>> = flowOf(emptyList())
    override fun getRoutineWithTasksById(id: Long): Flow<RoutineWithTasks?> = flowOf(null)
    override suspend fun insertRoutine(routine: Routine): Long = insertResult
    override suspend fun insertRoutines(routines: List<Routine>) {}
    override suspend fun updateRoutine(routine: Routine) {}
    override suspend fun deleteRoutine(routine: Routine) {}
    override suspend fun deleteRoutineById(id: Long) {}
    override suspend fun removeAll() {}
}

class MockTaskDao : TaskDao {
    override fun getTasksForRoutine(routineId: Long): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun getTasksForRoutineSuspend(routineId: Long): List<Task> = emptyList()
    override fun getAllTasks(): Flow<List<Task>> = flowOf(emptyList())
    override suspend fun insertTask(task: Task): Long = 1L
    override suspend fun insertTasks(tasks: List<Task>) {}
    override suspend fun updateTask(task: Task) {}
    override suspend fun deleteTask(task: Task) {}
    override suspend fun removeAll() {}
}

class MockRecurrenceDao : com.swmansion.routinetracker.database.RoutineRecurrenceDao {
    override suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long = 1L
    override suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>) {}
    override suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> = emptyList()
    override suspend fun deleteRecurrencesForRoutine(routineId: Long) {}
    override suspend fun removeAll() {}
}


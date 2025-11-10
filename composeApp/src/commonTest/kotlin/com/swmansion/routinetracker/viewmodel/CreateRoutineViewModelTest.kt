package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.RoutineWithTasks
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow

class CreateRoutineViewModelTest {
    
    @Test
    fun `updateRoutineName should update routine name in state`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val newName = "Test Routine"
        
        viewModel.updateRoutineName(newName)
        
        assertEquals(newName, viewModel.uiState.value.routineName)
    }
    
    @Test
    fun `setTime should update time in state and hide time picker`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val hour = 10
        val minute = 30
        
        viewModel.setTime(hour, minute)
        
        assertEquals(hour, viewModel.uiState.value.selectedHour)
        assertEquals(minute, viewModel.uiState.value.selectedMinute)
        assertTrue(viewModel.uiState.value.isTimeSet)
        assertFalse(viewModel.uiState.value.showTimePicker)
    }
    
    @Test
    fun `getFormattedTime should return formatted time when time is set`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.setTime(9, 15)
        
        val formattedTime = viewModel.getFormattedTime()
        
        assertNotNull(formattedTime)
        assertEquals("09:15:00", formattedTime)
    }
    
    @Test
    fun `getFormattedTime should return null when time is not set`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        
        val formattedTime = viewModel.getFormattedTime()
        
        assertNull(formattedTime)
    }
    
    @Test
    fun `updateSelectedDaysOfWeek should update selected days in state`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        
        viewModel.updateSelectedDaysOfWeek(days)
        
        assertEquals(days, viewModel.uiState.value.selectedDaysOfWeek)
    }
    
    @Test
    fun `updateIntervalWeeks should update interval weeks in state`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val weeks = 2f
        
        viewModel.updateIntervalWeeks(weeks)
        
        assertEquals(weeks, viewModel.uiState.value.intervalWeeks)
    }
    
    @Test
    fun `createRoutine with empty name should show error message`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        var onSuccessCalled = false
        
        viewModel.createRoutine { onSuccessCalled = true }
        
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("required", ignoreCase = true))
        assertFalse(onSuccessCalled)
    }
    
    @Test
    fun `createRoutine with blank name should show error message`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("   ")
        var onSuccessCalled = false
        
        viewModel.createRoutine { onSuccessCalled = true }
        
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(onSuccessCalled)
    }
    
    @Test
    fun `createRoutine with valid name should create routine and reset form`() = runTest {
        val repository = createMockRepository(routineId = 1L)
        val viewModel = CreateRoutineViewModel(repository)
        val routineName = "Morning Routine"
        viewModel.updateRoutineName(routineName)
        viewModel.setTime(8, 0)
        viewModel.updateSelectedDaysOfWeek(setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        viewModel.updateIntervalWeeks(1f)
        var onSuccessCalled = false
        
        viewModel.createRoutine { onSuccessCalled = true }
        
        kotlinx.coroutines.delay(100)
        
        assertTrue(onSuccessCalled)
        assertNotNull(viewModel.uiState.value.successMessage)
        assertTrue(viewModel.uiState.value.successMessage!!.contains(routineName))
        assertEquals("", viewModel.uiState.value.routineName)

        assertFalse(viewModel.uiState.value.isTimeSet)
        assertTrue(viewModel.uiState.value.selectedDaysOfWeek.isEmpty())
        assertEquals(0f, viewModel.uiState.value.intervalWeeks)
    }
    
    @Test
    fun `createRoutine should create routine with recurrences`() = runTest {
        val repository = createMockRepository(routineId = 2L)
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("Evening Routine")
        viewModel.updateSelectedDaysOfWeek(setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY))
        viewModel.updateIntervalWeeks(2f)
        var onSuccessCalled = false
        
        viewModel.createRoutine { onSuccessCalled = true }
        
        kotlinx.coroutines.delay(100)
        
        assertTrue(onSuccessCalled)
        assertNotNull(viewModel.uiState.value.successMessage)
        ]
        val mockRepository = repository as MockDataRepository
        assertEquals(1, mockRepository.createRoutineWithRecurrenceCallCount)
        assertNotNull(mockRepository.lastRoutine)
        assertEquals("Evening Routine", mockRepository.lastRoutine!!.name)
        assertNotNull(mockRepository.lastRecurrences)
        assertEquals(2, mockRepository.lastRecurrences!!.size)
    }
    
    @Test
    fun `createRoutine should handle errors gracefully`() = runTest {
        val repository = createMockRepository(shouldThrowError = true)
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("Test Routine")
        var onSuccessCalled = false
        
        viewModel.createRoutine { onSuccessCalled = true }
        
        kotlinx.coroutines.delay(100)
        
        assertFalse(onSuccessCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Failed", ignoreCase = true))
        assertFalse(viewModel.uiState.value.isLoading)
    }
    
    @Test
    fun `clearMessages should clear error and success messages`() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateErrorMessage("Test error")
        viewModel.updateSuccessMessage("Test success")
        
        viewModel.clearMessages()
        
        assertNull(viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.successMessage)
    }
    
    private fun createMockRepository(
        routineId: Long = 1L,
        shouldThrowError: Boolean = false
    ): DataRepository {
        val mockRoutineDao = MockRoutineDao(routineId, flowOf(emptyList()))
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        return MockDataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao, shouldThrowError)
    }
}

class MockDataRepository(
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao,
    private val recurrenceDao: RoutineRecurrenceDao,
    private val shouldThrowError: Boolean = false
) : DataRepository(routineDao, taskDao, recurrenceDao) {
    var createRoutineWithRecurrenceCallCount = 0
    var lastRoutine: Routine? = null
    var lastRecurrences: List<RoutineRecurrence>? = null
    
    override suspend fun createRoutineWithRecurrence(
        routine: Routine,
        recurrences: List<RoutineRecurrence>
    ): Long {
        if (shouldThrowError) {
            throw Exception("Test error")
        }
        createRoutineWithRecurrenceCallCount++
        lastRoutine = routine
        lastRecurrences = recurrences
        return super.createRoutineWithRecurrence(routine, recurrences)
    }
}

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
    override fun getTasksForRoutine(routineId: Long): Flow<List<com.swmansion.routinetracker.model.Task>> = flowOf(emptyList())
    override suspend fun getTasksForRoutineSuspend(routineId: Long): List<com.swmansion.routinetracker.model.Task> = emptyList()
    override fun getAllTasks(): Flow<List<com.swmansion.routinetracker.model.Task>> = flowOf(emptyList())
    override suspend fun insertTask(task: com.swmansion.routinetracker.model.Task): Long = 1L
    override suspend fun insertTasks(tasks: List<com.swmansion.routinetracker.model.Task>) {}
    override suspend fun updateTask(task: com.swmansion.routinetracker.model.Task) {}
    override suspend fun deleteTask(task: com.swmansion.routinetracker.model.Task) {}
    override suspend fun removeAll() {}
}

class MockRecurrenceDao : RoutineRecurrenceDao {
    override suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long = 1L
    override suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>) {}
    override suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> = emptyList()
    override suspend fun deleteRecurrencesForRoutine(routineId: Long) {}
    override suspend fun removeAll() {}
}


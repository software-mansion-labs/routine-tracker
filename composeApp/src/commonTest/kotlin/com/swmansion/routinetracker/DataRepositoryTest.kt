package com.swmansion.routinetracker

import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.RoutineWithTasks
import com.swmansion.routinetracker.model.Task
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow

class DataRepositoryTest {
    
    @Test
    fun `createRoutine should insert routine and return id`() = runTest {
        val routine = Routine(name = "Morning Routine", time = "08:00")
        val routineId = 1L
        val mockRoutineDao = createMockRoutineDao(insertResult = routineId)
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        val result = repository.createRoutine(routine)
        
        assertEquals(routineId, result)
        assertEquals(1, mockRoutineDao.insertCallCount)
        assertEquals(routine, mockRoutineDao.lastInsertedRoutine)
    }
    
    @Test
    fun `createRoutineWithRecurrence should insert routine and recurrences`() = runTest {
        val routine = Routine(name = "Evening Routine", time = "20:00")
        val routineId = 2L
        val recurrences = listOf(
            RoutineRecurrence(routineId = 0, dayOfWeek = DayOfWeek.MONDAY.value, intervalWeeks = 1),
            RoutineRecurrence(routineId = 0, dayOfWeek = DayOfWeek.WEDNESDAY.value, intervalWeeks = 1)
        )
        val mockRoutineDao = createMockRoutineDao(insertResult = routineId)
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        val result = repository.createRoutineWithRecurrence(routine, recurrences)
        
        assertEquals(routineId, result)
        assertEquals(1, mockRoutineDao.insertCallCount)
        assertEquals(1, mockRecurrenceDao.insertCallCount)
        assertTrue(mockRecurrenceDao.lastInsertedRecurrences.isNotEmpty())
        assertEquals(2, mockRecurrenceDao.lastInsertedRecurrences.size)

        mockRecurrenceDao.lastInsertedRecurrences.forEach {
            assertEquals(routineId, it.routineId)
        }
    }
    
    @Test
    fun `createRoutineWithRecurrence with empty recurrences should not insert recurrences`() = runTest {
        val routine = Routine(name = "Simple Routine")
        val routineId = 3L
        val mockRoutineDao = createMockRoutineDao(insertResult = routineId)
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        val result = repository.createRoutineWithRecurrence(routine, emptyList())
        
        assertEquals(routineId, result)
        assertEquals(1, mockRoutineDao.insertCallCount)
        assertEquals(0, mockRecurrenceDao.insertCallCount)
    }
    
    @Test
    fun `getAllRoutines should return all routines from dao`() = runTest {
        val routines = listOf(
            Routine(id = 1L, name = "Routine 1", time = "08:00"),
            Routine(id = 2L, name = "Routine 2", time = "12:00"),
            Routine(id = 3L, name = "Routine 3")
        )
        val mockRoutineDao = createMockRoutineDao(routinesFlow = flowOf(routines))
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        val result = repository.getAllRoutines()
        
        var collectedRoutines: List<Routine>? = null
        result.collect { collectedRoutines = it }
        assertNotNull(collectedRoutines)
        assertEquals(3, collectedRoutines.size)
        assertEquals("Routine 1", collectedRoutines[0].name)
        assertEquals("Routine 2", collectedRoutines[1].name)
        assertEquals("Routine 3", collectedRoutines[2].name)
    }
    
    @Test
    fun `getAllRoutines should return empty list when no routines exist`() = runTest {
        // Given
        val mockRoutineDao = createMockRoutineDao(routinesFlow = flowOf(emptyList()))
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        // When
        val result = repository.getAllRoutines()
        
        // Then
        var collectedRoutines: List<Routine>? = null
        result.collect { collectedRoutines = it }
        assertNotNull(collectedRoutines)
        assertTrue(collectedRoutines.isEmpty())
    }
    
    // Helper functions to create mock DAOs
    private fun createMockRoutineDao(
        insertResult: Long = 1L,
        routinesFlow: Flow<List<Routine>> = flowOf(emptyList())
    ): MockRoutineDao {
        return MockRoutineDao(insertResult, routinesFlow)
    }
    
    private fun createMockTaskDao(): MockTaskDao {
        return MockTaskDao()
    }
    
    private fun createMockRecurrenceDao(): MockRecurrenceDao {
        return MockRecurrenceDao()
    }
}

class MockRoutineDao(
    private val insertResult: Long,
    private val routinesFlow: Flow<List<Routine>>
) : RoutineDao {
    var insertCallCount = 0
    var lastInsertedRoutine: Routine? = null
    
    override fun getAllRoutines(): Flow<List<Routine>> = routinesFlow
    
    override suspend fun getRoutineById(id: Long): Routine? = null
    
    override fun getAllRoutinesWithTasks(): Flow<List<RoutineWithTasks>> = flowOf(emptyList())

    override fun getRoutineWithTasksById(id: Long): Flow<RoutineWithTasks?> = flowOf(null)
    
    override suspend fun insertRoutine(routine: Routine): Long {
        insertCallCount++
        lastInsertedRoutine = routine
        return insertResult
    }
    
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

class MockRecurrenceDao : RoutineRecurrenceDao {
    var insertCallCount = 0
    var lastInsertedRecurrences: List<RoutineRecurrence> = emptyList()
    
    override suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long {
        insertCallCount++
        return 1L
    }
    
    override suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>) {
        insertCallCount++
        lastInsertedRecurrences = recurrences
    }
    
    override suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> = emptyList()
    
    override suspend fun deleteRecurrencesForRoutine(routineId: Long) {}
    
    override suspend fun removeAll() {}
}


package com.swmansion.routinetracker

import com.swmansion.routinetracker.mock.MockRecurrenceDao
import com.swmansion.routinetracker.mock.MockRoutineDao
import com.swmansion.routinetracker.mock.MockTaskDao
import com.swmansion.routinetracker.model.DayOfWeek
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow

class DataRepositoryTest {
    
    @Test
    fun createRoutineShouldInsertRoutineAndReturnId() = runTest {
        val routineId = 1L
        val routine = Routine(id = routineId ,name = "Morning Routine", time = "08:00")
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
    fun createRoutineWithRecurrenceShouldInsertRoutineAndRecurrences() = runTest {
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
    fun createRoutineWithRecurrenceWithEmptyRecurrencesShouldNotInsertRecurrences() = runTest {
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
    fun getAllRoutinesShouldReturnAllRoutinesFromDao() = runTest {
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
    fun getAllRoutinesShouldReturnEmptyListWhenNoRoutinesExist() = runTest {
        val mockRoutineDao = createMockRoutineDao(routinesFlow = flowOf(emptyList()))
        val mockTaskDao = createMockTaskDao()
        val mockRecurrenceDao = createMockRecurrenceDao()
        val repository = DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
        
        val result = repository.getAllRoutines()
        
        var collectedRoutines: List<Routine>? = null
        result.collect { collectedRoutines = it }
        assertNotNull(collectedRoutines)
        assertTrue(collectedRoutines.isEmpty())
    }
    
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


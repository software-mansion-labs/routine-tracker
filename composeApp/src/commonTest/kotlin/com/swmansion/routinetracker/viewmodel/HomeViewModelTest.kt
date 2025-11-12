package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.mock.MockRecurrenceDao
import com.swmansion.routinetracker.mock.MockRoutineDao
import com.swmansion.routinetracker.mock.MockTaskDao
import com.swmansion.routinetracker.model.Routine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class HomeViewModelTest {

    @Test
    fun uiStateShouldInitiallyBeEmpty() = runTest {
        val repository = createMockRepository(flowOf(emptyList()))
        val viewModel = HomeViewModel(repository)

        assertTrue(viewModel.uiState.value.routines.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateShouldReflectRoutinesFromRepository() = runTest {
        val routines =
            listOf(
                Routine(id = 1L, name = "Morning Routine", time = "08:00"),
                Routine(id = 2L, name = "Evening Routine", time = "20:00"),
                Routine(id = 3L, name = "Workout Routine"),
            )
        val repository = createMockRepository(flowOf(routines))
        val viewModel = HomeViewModel(repository)

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.routines.size)
        assertEquals("Morning Routine", viewModel.uiState.value.routines[0].name)
        assertEquals("Evening Routine", viewModel.uiState.value.routines[1].name)
        assertEquals("Workout Routine", viewModel.uiState.value.routines[2].name)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateShouldUpdateWhenRepositoryEmitsNewRoutines() = runTest {
        val stateFlow = MutableStateFlow<List<Routine>>(emptyList())
        val repository = createMockRepository(stateFlow)
        val viewModel = HomeViewModel(repository)

        assertTrue(viewModel.uiState.value.routines.isEmpty())

        val routines =
            listOf(Routine(id = 1L, name = "Routine 1"), Routine(id = 2L, name = "Routine 2"))
        stateFlow.value = routines

        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.routines.size)
        assertEquals("Routine 1", viewModel.uiState.value.routines[0].name)
        assertEquals("Routine 2", viewModel.uiState.value.routines[1].name)

        val updatedRoutines =
            listOf(
                Routine(id = 1L, name = "Routine 1"),
                Routine(id = 2L, name = "Routine 2"),
                Routine(id = 3L, name = "Routine 3"),
            )
        stateFlow.value = updatedRoutines

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.routines.size)
        assertEquals("Routine 3", viewModel.uiState.value.routines[2].name)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateShouldHandleEmptyListFromRepository() = runTest {
        val repository = createMockRepository(flowOf(emptyList()))
        val viewModel = HomeViewModel(repository)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.routines.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun uiStateShouldMaintainRoutineOrderFromRepository() = runTest {
        val routines =
            listOf(
                Routine(id = 1L, name = "A Routine"),
                Routine(id = 2L, name = "B Routine"),
                Routine(id = 3L, name = "C Routine"),
            )
        val repository = createMockRepository(flowOf(routines))
        val viewModel = HomeViewModel(repository)

        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.routines.size)

        assertEquals("A Routine", viewModel.uiState.value.routines[0].name)
        assertEquals("B Routine", viewModel.uiState.value.routines[1].name)
        assertEquals("C Routine", viewModel.uiState.value.routines[2].name)
    }

    private fun createMockRepository(routinesFlow: Flow<List<Routine>>): DataRepository {
        val mockRoutineDao = MockRoutineDao(1L, routinesFlow)
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        return DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
    }
}

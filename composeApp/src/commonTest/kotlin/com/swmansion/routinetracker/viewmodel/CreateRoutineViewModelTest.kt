package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.mock.MockDataRepository
import com.swmansion.routinetracker.mock.MockRecurrenceDao
import com.swmansion.routinetracker.mock.MockRoutineDao
import com.swmansion.routinetracker.mock.MockTaskDao
import com.swmansion.routinetracker.model.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class CreateRoutineViewModelTest {

    @Test
    fun updateRoutineNameShouldUpdateRoutineNameInState() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val newName = "Test Routine"

        viewModel.updateRoutineName(newName)

        assertEquals(newName, viewModel.uiState.value.routineName)
    }

    @Test
    fun setTimeShouldUpdateTimeInStateAndHideTimePicker() = runTest {
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
    fun getFormattedTimeShouldReturnFormattedTimeWhenTimeIsSet() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.setTime(9, 15)

        val formattedTime = viewModel.getFormattedTime()

        assertNotNull(formattedTime)
        assertEquals("09:15", formattedTime)
    }

    @Test
    fun getFormattedTimeShouldReturnNullWhenTimeIsNotSet() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)

        val formattedTime = viewModel.getFormattedTime()

        assertNull(formattedTime)
    }

    @Test
    fun updateSelectedDaysOfWeekShouldUpdateSelectedDaysInState() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)

        viewModel.updateSelectedDaysOfWeek(days)

        assertEquals(days, viewModel.uiState.value.selectedDaysOfWeek)
    }

    @Test
    fun updateIntervalWeeksShouldUpdateIntervalWeeksInState() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        val weeks = 2f

        viewModel.updateIntervalWeeks(weeks)

        assertEquals(weeks, viewModel.uiState.value.intervalWeeks)
    }

    @Test
    fun createRoutineWithEmptyNameShouldShowErrorMessage() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        var onSuccessCalled = false

        viewModel.createRoutine { onSuccessCalled = true }

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("required", ignoreCase = true))
        assertFalse(onSuccessCalled)
    }

    @Test
    fun createRoutineWithBlankNameShouldShowErrorMessage() = runTest {
        val repository = createMockRepository()
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("   ")
        var onSuccessCalled = false

        viewModel.createRoutine { onSuccessCalled = true }

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(onSuccessCalled)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createRoutineShouldCreateRoutineWithRecurrences() = runTest {
        val repository = createMockRepository(routineId = 2L)
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("Evening Routine")
        viewModel.updateSelectedDaysOfWeek(setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY))
        viewModel.updateIntervalWeeks(2f)
        var onSuccessCalled = false

        viewModel.createRoutine { onSuccessCalled = true }

        advanceUntilIdle()

        assertTrue(onSuccessCalled)
        assertNotNull(viewModel.uiState.value.successMessage)

        val mockRepository = repository as MockDataRepository
        assertEquals(1, mockRepository.createRoutineWithRecurrenceCallCount)
        assertNotNull(mockRepository.lastRoutine)
        assertEquals("Evening Routine", mockRepository.lastRoutine!!.name)
        assertNotNull(mockRepository.lastRecurrences)
        assertEquals(2, mockRepository.lastRecurrences!!.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createRoutineShouldHandleErrorsGracefully() = runTest {
        val repository = createMockRepository(shouldThrowError = true)
        val viewModel = CreateRoutineViewModel(repository)
        viewModel.updateRoutineName("Test Routine")
        var onSuccessCalled = false

        viewModel.createRoutine { onSuccessCalled = true }

        advanceUntilIdle()

        assertFalse(onSuccessCalled)
        assertNotNull(viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.errorMessage!!.contains("Failed", ignoreCase = true))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun clearMessagesShouldClearErrorAndSuccessMessages() = runTest {
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
        shouldThrowError: Boolean = false,
    ): DataRepository {
        val mockRoutineDao = MockRoutineDao(routineId, flowOf(emptyList()))
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        return MockDataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao, shouldThrowError)
    }
}

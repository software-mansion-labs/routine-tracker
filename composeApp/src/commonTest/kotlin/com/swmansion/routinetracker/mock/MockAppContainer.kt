package com.swmansion.routinetracker.mock

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.model.Routine
import kotlinx.coroutines.flow.MutableStateFlow

class MockAppContainer(private val routinesFlow: MutableStateFlow<List<Routine>>) : AppContainer() {
    override val repository: DataRepository by lazy {
        val mockRoutineDao = MockRoutineDao(1L, routinesFlow)
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        DataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
    }
}

package com.swmansion.routinetracker.mock.di

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.DefaultDataRepository
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.mock.database.MockRecurrenceDao
import com.swmansion.routinetracker.mock.database.MockRoutineDao
import com.swmansion.routinetracker.mock.database.MockTaskDao
import com.swmansion.routinetracker.model.Routine
import kotlinx.coroutines.flow.MutableStateFlow

class MockAppContainer(private val routinesFlow: MutableStateFlow<List<Routine>>) : AppContainer {
    override val repository: DataRepository by lazy {
        val mockRoutineDao = MockRoutineDao(1L, routinesFlow)
        val mockTaskDao = MockTaskDao()
        val mockRecurrenceDao = MockRecurrenceDao()
        DefaultDataRepository(mockRoutineDao, mockTaskDao, mockRecurrenceDao)
    }
}

package com.swmansion.routinetracker.mock.database

import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineWithTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockRoutineDao(
    private val insertResult: Long,
    private val routinesFlow: Flow<List<Routine>>,
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

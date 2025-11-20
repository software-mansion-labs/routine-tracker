package com.swmansion.routinetracker.mock.database

import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineWithTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MockRoutineDao(
    private val insertResult: Long,
    private val routinesFlow: Flow<List<Routine>>,
) : RoutineDao {
    var insertCallCount = 0
    var lastInsertedRoutine: Routine? = null

    override fun getAllRoutines(): Flow<List<Routine>> = routinesFlow

    override suspend fun getRoutineById(id: Long): Routine? = null

    override fun getAllRoutinesWithTasks(): Flow<List<RoutineWithTasks>> {
        return routinesFlow.map { routines ->
            routines.map { routine -> RoutineWithTasks(routine = routine, tasks = emptyList()) }
        }
    }

    override fun getRoutineWithTasksById(id: Long): Flow<RoutineWithTasks?> {
        return routinesFlow.map { routines ->
            routines
                .firstOrNull { it.id == id }
                ?.let { routine -> RoutineWithTasks(routine = routine, tasks = emptyList()) }
        }
    }

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

    override suspend fun countRoutinesWithoutTime(): Int =
        routinesFlow.first().count { it.time == null }
}

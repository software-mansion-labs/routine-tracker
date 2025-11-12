package com.swmansion.routinetracker.mock

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence

class MockDataRepository(
    routineDao: RoutineDao,
    taskDao: TaskDao,
    recurrenceDao: RoutineRecurrenceDao,
) : DataRepository(routineDao, taskDao, recurrenceDao) {
    var createRoutineWithRecurrenceCallCount = 0
    var lastRoutine: Routine? = null
    var lastRecurrences: List<RoutineRecurrence>? = null

    override suspend fun createRoutineWithRecurrence(
        routine: Routine,
        recurrences: List<RoutineRecurrence>,
    ): Long {
        createRoutineWithRecurrenceCallCount++
        lastRoutine = routine
        lastRecurrences = recurrences
        return super.createRoutineWithRecurrence(routine, recurrences)
    }
}

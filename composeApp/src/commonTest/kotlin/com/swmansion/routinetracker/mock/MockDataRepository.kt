package com.swmansion.routinetracker.mock

import com.swmansion.routinetracker.DataRepository
import com.swmansion.routinetracker.IDataRepository
import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence

class MockDataRepository(
    routineDao: RoutineDao,
    taskDao: TaskDao,
    recurrenceDao: RoutineRecurrenceDao,
) : IDataRepository {
    private val delegate: IDataRepository = DataRepository(routineDao, taskDao, recurrenceDao)
    
    var createRoutineWithRecurrenceCallCount = 0
    var lastRoutine: Routine? = null
    var lastRecurrences: List<RoutineRecurrence>? = null

    override fun getAllRoutinesWithTasks() = delegate.getAllRoutinesWithTasks()
    
    override suspend fun createRoutineWithRecurrence(
        routine: Routine,
        recurrences: List<RoutineRecurrence>,
    ): Long {
        createRoutineWithRecurrenceCallCount++
        lastRoutine = routine
        lastRecurrences = recurrences
        
        return delegate.createRoutineWithRecurrence(routine, recurrences)
    }
    
    override suspend fun addTaskToRoutine(routineId: Long, task: com.swmansion.routinetracker.model.Task) =
        delegate.addTaskToRoutine(routineId, task)
}

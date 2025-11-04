package com.swmansion.routinetracker

import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineRecurrence
import com.swmansion.routinetracker.model.Task

class DataRepository(
    private val routineDao: RoutineDao,
    private val taskDao: TaskDao,
    private val routineRecurrenceDao: RoutineRecurrenceDao,
) {
    fun getAllRoutinesWithTasks() = routineDao.getAllRoutinesWithTasks()

    fun getRoutineWithTasks(id: Long) = routineDao.getRoutineWithTasksById(id)

    suspend fun createRoutine(routine: Routine) = routineDao.insertRoutine(routine)

    suspend fun createRoutineWithRecurrence(
        routine: Routine,
        recurrences: List<RoutineRecurrence>,
    ): Long {
        val routineId = routineDao.insertRoutine(routine)
        if (recurrences.isNotEmpty()) {
            routineRecurrenceDao.insertRecurrences(
                recurrences.map { it.copy(routineId = routineId) },
            )
        }
        return routineId
    }

    suspend fun insertRoutineWithTasks(routine: Routine, tasks: List<Task>): Long {
        val routineId = routineDao.insertRoutine(routine)
        taskDao.insertTasks(tasks.mapIndexed { i, t -> t.copy(routineId = routineId) })
        return routineId
    }

    suspend fun updateRoutine(routine: Routine) = routineDao.updateRoutine(routine)

    suspend fun deleteRoutine(routine: Routine) = routineDao.deleteRoutine(routine)

    suspend fun addTaskToRoutine(routineId: Long, task: Task) =
        taskDao.insertTask(task.copy(routineId = routineId))

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun removeAll() {
        routineDao.removeAll()
        taskDao.removeAll()
        routineRecurrenceDao.removeAll()
    }
}

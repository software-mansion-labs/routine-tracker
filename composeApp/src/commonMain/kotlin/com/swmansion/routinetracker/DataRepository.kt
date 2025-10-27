package com.swmansion.routinetracker

import com.swmansion.routinetracker.database.RoutineDao
import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.Task
import com.swmansion.routinetracker.viewmodel.RoutineViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class DataRepository(private val routineDao: RoutineDao, private val taskDao: TaskDao) {

    fun getAllRoutinesWithTasks(): Flow<List<RoutineViewModel>> =
        routineDao.getAllRoutines().combine(taskDao.getAllTasks()) { routines, tasks ->
            routines.map { RoutineViewModel(it, tasks.filter { t -> t.routineId == it.id }) }
        }

    fun getRoutineWithTasks(id: Long): Flow<RoutineViewModel?> =
        routineDao.getAllRoutines().combine(taskDao.getAllTasks()) { routines, tasks ->
            routines.find { it.id == id }?.let { RoutineViewModel(it, tasks.filter { t -> t.routineId == it.id }) }
        }

    suspend fun insertRoutineWithTasks(routine: Routine, tasks: List<Task>) {
        val routineId = routineDao.insertRoutine(routine)
        taskDao.insertTasks(tasks.mapIndexed { i, t -> t.copy(routineId = routineId, order = i + 1) })
    }

    suspend fun updateRoutine(routine: Routine) = routineDao.updateRoutine(routine)
    suspend fun deleteRoutine(routine: Routine) = routineDao.deleteRoutine(routine)

    suspend fun addTaskToRoutine(routineId: Long, task: Task): Long {
        val existingTasks = taskDao.getTasksForRoutineSuspend(routineId)
        return taskDao.insertTask(task.copy(routineId = routineId, order = existingTasks.size + 1))
    }

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun reorderTasks(routineId: Long, taskIds: List<Long>) {
        taskIds.forEachIndexed { i, id -> taskDao.updateTaskOrder(id, i + 1) }
    }
}
package com.swmansion.routinetracker.mock

import com.swmansion.routinetracker.database.TaskDao
import com.swmansion.routinetracker.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockTaskDao : TaskDao {
    override fun getTasksForRoutine(routineId: Long): Flow<List<Task>> = flowOf(emptyList())

    override suspend fun getTasksForRoutineSuspend(routineId: Long): List<Task> = emptyList()

    override fun getAllTasks(): Flow<List<Task>> = flowOf(emptyList())

    override suspend fun insertTask(task: Task): Long = 1L

    override suspend fun insertTasks(tasks: List<Task>) {}

    override suspend fun updateTask(task: Task) {}

    override suspend fun deleteTask(task: Task) {}

    override suspend fun removeAll() {}
}

package com.swmansion.routinetracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.swmansion.routinetracker.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY `order`")
    fun getTasksForRoutine(routineId: Long): kotlinx.coroutines.flow.Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE routineId = :routineId ORDER BY `order`")
    suspend fun getTasksForRoutineSuspend(routineId: Long): List<Task>

    @Query("SELECT * FROM tasks ORDER BY routineId, `order`")
    fun getAllTasks(): kotlinx.coroutines.flow.Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET `order` = :newOrder WHERE id = :taskId")
    suspend fun updateTaskOrder(taskId: Long, newOrder: Int)
}
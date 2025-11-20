package com.swmansion.routinetracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.RoutineWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines ORDER BY name") fun getAllRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id") suspend fun getRoutineById(id: Long): Routine?

    @Transaction
    @Query("SELECT * FROM routines ORDER BY name")
    fun getAllRoutinesWithTasks(): Flow<List<RoutineWithTasks>>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineWithTasksById(id: Long): Flow<RoutineWithTasks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutines(routines: List<Routine>)

    @Update suspend fun updateRoutine(routine: Routine)

    @Delete suspend fun deleteRoutine(routine: Routine)

    @Query("DELETE FROM routines WHERE id = :id") suspend fun deleteRoutineById(id: Long)

    @Query("DELETE FROM routines") suspend fun removeAll()

    @Query("SELECT COUNT(*) FROM routines WHERE time IS NULL OR time = ''")
    suspend fun countRoutinesWithoutTime(): Int
}

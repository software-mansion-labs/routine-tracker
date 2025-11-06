package com.swmansion.routinetracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.swmansion.routinetracker.model.RoutineRecurrence

@Dao
interface RoutineRecurrenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>)

    @Query("SELECT * FROM routine_recurrence WHERE routineId = :routineId")
    suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence>

    @Query("DELETE FROM routine_recurrence WHERE routineId = :routineId")
    suspend fun deleteRecurrencesForRoutine(routineId: Long)

    @Query("DELETE FROM routine_recurrence") suspend fun removeAll()
}

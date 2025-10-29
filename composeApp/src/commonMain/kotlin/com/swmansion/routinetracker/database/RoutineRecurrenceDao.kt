package com.swmansion.routinetracker.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RoutineRecurrenceDao {
    @Query("DELETE FROM routine_recurrence") suspend fun removeAll()
}

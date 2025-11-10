package com.swmansion.routinetracker.mock

import com.swmansion.routinetracker.database.RoutineRecurrenceDao
import com.swmansion.routinetracker.model.RoutineRecurrence

class MockRecurrenceDao : RoutineRecurrenceDao {
    var insertCallCount = 0
    var lastInsertedRecurrences: List<RoutineRecurrence> = emptyList()

    override suspend fun insertRecurrence(recurrence: RoutineRecurrence): Long {
        insertCallCount++
        return 1L
    }

    override suspend fun insertRecurrences(recurrences: List<RoutineRecurrence>) {
        insertCallCount++
        lastInsertedRecurrences = recurrences
    }

    override suspend fun getRecurrencesForRoutine(routineId: Long): List<RoutineRecurrence> = emptyList()

    override suspend fun deleteRecurrencesForRoutine(routineId: Long) {}

    override suspend fun removeAll() {}
}

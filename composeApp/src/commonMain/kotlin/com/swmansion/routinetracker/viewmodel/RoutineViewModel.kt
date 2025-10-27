package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.Task

data class RoutineViewModel(val routine: Routine, val tasks: List<Task>) {
    fun getTotalDuration(): Int? = tasks.mapNotNull { it.duration }.takeIf { it.isNotEmpty() }?.sum()
    fun getTaskCount(): Int = tasks.size
    fun getTimeDisplay(): String = routine.time ?: "No specific time"
}

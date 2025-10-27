package com.swmansion.routinetracker.viewmodel

import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.model.Task

data class RoutineViewModel(val routine: Routine, val tasks: List<Task>)

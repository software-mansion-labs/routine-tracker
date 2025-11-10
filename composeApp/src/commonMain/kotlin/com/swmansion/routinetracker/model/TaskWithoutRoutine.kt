package com.swmansion.routinetracker.model

data class TaskWithoutRoutine(
    val id: Long = 0,
    val name: String,
    val duration: Int? = null,
    val order: Int,
)

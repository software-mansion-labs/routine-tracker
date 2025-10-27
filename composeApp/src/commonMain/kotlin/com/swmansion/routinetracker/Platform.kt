package com.swmansion.routinetracker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
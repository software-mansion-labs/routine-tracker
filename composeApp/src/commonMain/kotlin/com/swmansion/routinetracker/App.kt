package com.swmansion.routinetracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.data.createAlarmeePlatformConfiguration
import com.swmansion.routinetracker.navigation.CreateRoutine
import com.swmansion.routinetracker.navigation.CreateTask
import com.swmansion.routinetracker.navigation.Home
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import com.swmansion.routinetracker.screen.CreateTaskScreen
import com.swmansion.routinetracker.screen.HomeScreen
import com.tweener.alarmee.createAlarmeeService

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val alarmeeService = createAlarmeeService()
        alarmeeService.initialize(platformConfiguration = createAlarmeePlatformConfiguration())

        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<Home> {
                HomeScreen(navController = navController, alarmeeService = alarmeeService)
            }
            composable<CreateRoutine> {
                CreateRoutineScreen(navController = navController, alarmeeService = alarmeeService)
            }
            composable<CreateTask> { CreateTaskScreen(navController = navController) }
        }
    }
}

package com.swmansion.routinetracker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.navigation.Routes
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import com.swmansion.routinetracker.screen.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(route = Routes.HOME) { HomeScreen(navController = navController) }
            composable(route = Routes.CREATE_ROUTINE) {
                CreateRoutineScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

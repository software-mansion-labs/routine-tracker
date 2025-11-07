package com.swmansion.routinetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.navigation.CreateRoutine
import com.swmansion.routinetracker.navigation.Home
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import com.swmansion.routinetracker.screen.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Home,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable<Home> { HomeScreen(navController = navController) }
                composable<CreateRoutine> { CreateRoutineScreen(navController = navController) }
            }
        }
    }
}

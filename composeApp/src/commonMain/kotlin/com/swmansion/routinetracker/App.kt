package com.swmansion.routinetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.navigation.BottomNavigationBar
import com.swmansion.routinetracker.navigation.Routes
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import com.swmansion.routinetracker.screen.HomeScreen

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar = remember(currentRoute) { currentRoute == Routes.HOME }

        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(route = Routes.HOME) { HomeScreen(navController = navController) }
                composable(route = Routes.CREATE_ROUTINE) {
                    CreateRoutineScreen(onNavigateBack = { navController.popBackStack() })
                }
            }

            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

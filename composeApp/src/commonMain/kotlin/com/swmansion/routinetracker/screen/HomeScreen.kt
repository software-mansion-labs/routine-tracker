package com.swmansion.routinetracker.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.navigation.Routines
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_routine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val homeNavController = rememberNavController()
    val currentBackStackEntry by homeNavController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.height(84.dp), windowInsets = WindowInsets(0.dp)) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_routine),
                            contentDescription = "Routines",
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text("Routines") },
                    selected =
                        currentBackStackEntry?.destination?.hierarchy?.any {
                            it.hasRoute(Routines::class)
                        } == true,
                    onClick = { homeNavController.navigate(Routines) },
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        NavHost(
            navController = homeNavController,
            startDestination = Routines,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
        ) {
            composable<Routines> { RoutinesScreen(navController = navController) }
        }
    }
}

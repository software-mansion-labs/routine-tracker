package com.swmansion.routinetracker.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import routinetracker.composeapp.generated.resources.Res
import routinetracker.composeapp.generated.resources.ic_home

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier.height(84.dp), windowInsets = WindowInsets(0.dp)) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                )
            },
            label = { Text("Home") },
            selected = currentRoute == Routes.HOME,
            onClick = {
                if (currentRoute != Routes.HOME) {
                    navController.navigate(Routes.HOME)
                }
            },
        )
    }
}

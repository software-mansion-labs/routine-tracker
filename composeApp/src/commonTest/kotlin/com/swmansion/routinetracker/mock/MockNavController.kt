package com.swmansion.routinetracker.mock

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun createMockNavController(): NavController {
    return rememberNavController()
}

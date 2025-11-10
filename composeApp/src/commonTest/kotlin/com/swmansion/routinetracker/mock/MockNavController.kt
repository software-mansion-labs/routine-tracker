package com.swmansion.routinetracker.mock

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph

class MockNavController(private val onNavigate: (String) -> Unit = {}) : NavController() {
    override val currentBackStackEntry: NavBackStackEntry?
        get() = null
    override var graph: NavGraph = TODO()
        get() = throw NotImplementedError()

    override fun navigate(request: NavDeepLinkRequest) {
        onNavigate(request.uri.toString())
    }

    override fun navigateUp(): Boolean = true
    override fun popBackStack(): Boolean = true
    override fun addOnDestinationChangedListener(listener: OnDestinationChangedListener) {}
    override fun removeOnDestinationChangedListener(listener: OnDestinationChangedListener) {}
}

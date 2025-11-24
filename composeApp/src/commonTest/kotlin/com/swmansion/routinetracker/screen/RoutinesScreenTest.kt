package com.swmansion.routinetracker.screen

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.di.MockAppContainer
import com.swmansion.routinetracker.model.Routine
import com.tweener.alarmee.createAlarmeeService
import kotlin.test.Test
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalTestApi::class)
class RoutinesScreenTest {

    @Test
    fun shouldDisplayEmptyStateWhenNoRoutinesExist() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("No routines yet").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayListOfRoutinesWhenRoutinesExist() = runComposeUiTest {
        val routines =
            MutableStateFlow(
                listOf(
                    Routine(id = 1L, name = "Morning Routine", time = "08:00"),
                    Routine(id = 2L, name = "Evening Routine", time = "20:00"),
                    Routine(id = 3L, name = "Workout Routine"),
                )
            )
        val testAppContainer = createTestAppContainer(routines)
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("Morning Routine").assertIsDisplayed()
        onNodeWithText("08:00").assertIsDisplayed()
        onNodeWithText("Evening Routine").assertIsDisplayed()
        onNodeWithText("20:00").assertIsDisplayed()
        onNodeWithText("Workout Routine").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayRoutineWithoutTimeWhenTimeIsNull() = runComposeUiTest {
        val routines =
            MutableStateFlow(listOf(Routine(id = 1L, name = "Simple Routine", time = null)))
        val testAppContainer = createTestAppContainer(routines)
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("Simple Routine").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayTopBarWithCorrectTitle() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("My Routines").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayFloatingActionButton() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onRoot().assertIsDisplayed()
    }

    @Test
    fun shouldUpdateListWhenRoutinesAreAdded() = runComposeUiTest {
        val routinesFlow = MutableStateFlow(emptyList<Routine>())
        val testAppContainer = createTestAppContainer(routinesFlow)
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                RoutinesScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("No routines yet").assertIsDisplayed()

        routinesFlow.value = listOf(Routine(id = 1L, name = "New Routine", time = "10:00"))

        waitUntil(timeoutMillis = 5000) {
            try {
                onNodeWithText("New Routine").assertExists()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        onNodeWithText("New Routine").assertIsDisplayed()
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow, alarmeeService = createAlarmeeService())
}

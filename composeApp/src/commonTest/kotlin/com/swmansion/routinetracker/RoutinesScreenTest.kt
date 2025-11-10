package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.swmansion.routinetracker.di.AppContainer
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.createMockNavController
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.screen.RoutinesScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class RoutinesScreenTest {
    
    @Test
    fun `should display empty state when no routines exist`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("No routines yet").assertIsDisplayed()
    }
    
    @Test
    fun `should display list of routines when routines exist`() = runComposeUiTest {
        val routines = listOf(
            Routine(id = 1L, name = "Morning Routine", time = "08:00"),
            Routine(id = 2L, name = "Evening Routine", time = "20:00"),
            Routine(id = 3L, name = "Workout Routine")
        )
        val testAppContainer = createTestAppContainer(routines)
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Morning Routine").assertIsDisplayed()
        onNodeWithText("08:00").assertIsDisplayed()
        onNodeWithText("Evening Routine").assertIsDisplayed()
        onNodeWithText("20:00").assertIsDisplayed()
        onNodeWithText("Workout Routine").assertIsDisplayed()
        onNodeWithText("No routines yet").assertDoesNotExist()
    }
    
    @Test
    fun `should display routine without time when time is null`() = runComposeUiTest {
        val routines = listOf(
            Routine(id = 1L, name = "Simple Routine", time = null)
        )
        val testAppContainer = createTestAppContainer(routines)
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Simple Routine").assertIsDisplayed()
    }
    
    @Test
    fun `should display top bar with correct title`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("My Routines").assertIsDisplayed()
    }
    
    @Test
    fun `should display floating action button`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(emptyList())
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }

        onRoot().assertIsDisplayed()
    }
    
    @Test
    fun `should update list when routines are added`() = runComposeUiTest {
        val routinesFlow = MutableStateFlow<List<Routine>>(emptyList())
        val testAppContainer = createTestAppContainerWithFlow(routinesFlow)
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                RoutinesScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("No routines yet").assertIsDisplayed()
        
        routinesFlow.value = listOf(
            Routine(id = 1L, name = "New Routine", time = "10:00")
        )
        
        kotlinx.coroutines.delay(200)
        
        onNodeWithText("New Routine").assertIsDisplayed()
        onNodeWithText("No routines yet").assertDoesNotExist()
    }
    
    private fun createTestAppContainer(initialRoutines: List<Routine> = emptyList()): AppContainer {
        val routinesFlow = MutableStateFlow(initialRoutines)
        return MockAppContainer(routinesFlow)
    }
    
    private fun createTestAppContainerWithFlow(routinesFlow: MutableStateFlow<List<Routine>>): AppContainer {
        return MockAppContainer(routinesFlow)
    }
    

}

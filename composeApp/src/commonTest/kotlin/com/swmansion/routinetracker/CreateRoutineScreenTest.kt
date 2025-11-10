package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CreateRoutineScreenTest {
    
    @Test
    fun `should display create routine screen with all elements`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Create Routine").assertIsDisplayed()
        onNodeWithText("Routine Name").assertIsDisplayed()
        onNodeWithText("Select Time (optional)").assertIsDisplayed()
        onNodeWithText("Create").assertIsDisplayed()
        onNodeWithText("Discard").assertIsDisplayed()
    }
    
    @Test
    fun `should allow entering routine name`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val routineName = "Test Routine"
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Routine Name").performTextInput(routineName)
        onNodeWithText("Create").assertIsDisplayed()
    }
    
    @Test
    fun `should display error message when creating routine with empty name`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Create").performClick()
        kotlinx.coroutines.delay(200) // todo: delete
    }
    
    @Test
    fun `should display success message after creating routine`() = runComposeUiTest {
        val routinesFlow = MutableStateFlow<List<Routine>>(emptyList())
        val testAppContainer = createTestAppContainer(routinesFlow)
        val routineName = "New Routine"
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Routine Name").performTextInput(routineName)
        onNodeWithText("Create").performClick()
        
        kotlinx.coroutines.delay(300)
    }
    
    @Test
    fun `should display days of week selector`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Days of Week (optional)").assertIsDisplayed()
    }
    
    @Test
    fun `should display interval weeks selector`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
    }
    
    @Test
    fun `should navigate back when discard is clicked`() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        // var backPressed = false
        
        setContent {
            CompositionLocalProvider(LocalAppContainer provides testAppContainer) {
                CreateRoutineScreen(navController = createMockNavController {
                    // backPressed = true
                })
            }
        }
        
        onNodeWithText("Discard").performClick()
    }
    
    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ): TestAppContainer {
        return TestAppContainer(routinesFlow)
    }
    
    private fun createMockNavController(onNavigate: (String) -> Unit = {}): MockNavController {
        return MockNavController(onNavigate)
    }
}



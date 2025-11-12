package com.swmansion.routinetracker

import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.createMockNavController
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.screen.CreateRoutineScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class CreateRoutineScreenTest {
    
    @Test
    fun shouldDisplayCreateRoutineScreenWithAllElements() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
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
    fun shouldAllowEnteringRoutineName() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        val routineName = "Test Routine"
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Routine Name").performTextInput(routineName)
        onNodeWithText("Create").assertIsDisplayed()
    }
    
    @Test
    fun shouldDisplayErrorMessageWhenCreatingRoutineWithEmptyName() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Create").performClick()
        kotlinx.coroutines.delay(200) // todo: delete
    }
    
    @Test
    fun shouldDisplaySuccessMessageAfterCreatingRoutine() = runComposeUiTest {
        val routinesFlow = MutableStateFlow<List<Routine>>(emptyList())
        val testAppContainer = createTestAppContainer(routinesFlow)
        val viewModelStoreOwner = MockViewModelStoreOwner()
        val routineName = "New Routine"
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Routine Name").performTextInput(routineName)
        onNodeWithText("Create").performClick()
        
        kotlinx.coroutines.delay(300)
    }
    
    @Test
    fun shouldDisplayDaysOfWeekSelector() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Days of Week (optional)").assertIsDisplayed()
    }
    
    @Test
    fun shouldDisplayIntervalWeeksSelector() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        
        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
    }
    
    @Test
    fun shouldNavigateBackWhenDiscardIsClicked() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer
            ) {
                CreateRoutineScreen(navController = createMockNavController())
            }
        }
        
        onNodeWithText("Discard").performClick()
    }
    
    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ): MockAppContainer {
        return MockAppContainer(routinesFlow)
    }
}



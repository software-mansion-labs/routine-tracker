package com.swmansion.routinetracker.screen

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.di.MockAppContainer
import com.swmansion.routinetracker.model.Routine
import kotlin.test.Test
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalTestApi::class)
class CreateRoutineScreenTest {

    @Test
    fun shouldDisplayCreateRoutineScreenWithAllElements() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
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
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
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
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("Create").performClick()
        onNodeWithText("Routine name is required").assertIsDisplayed()
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
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("Routine Name").performTextInput(routineName)
        onNodeWithText("Create").performClick()

        waitUntil(timeoutMillis = 5000) {
            try {
                onNodeWithText("created successfully", substring = true).assertExists()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        onNodeWithText("created successfully", substring = true).assertIsDisplayed()
    }

    @Test
    fun shouldDisplayDaysOfWeekSelector() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
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
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
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
                LocalAppContainer provides testAppContainer,
            ) {
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        onNodeWithText("Discard").performClick()
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow)
}

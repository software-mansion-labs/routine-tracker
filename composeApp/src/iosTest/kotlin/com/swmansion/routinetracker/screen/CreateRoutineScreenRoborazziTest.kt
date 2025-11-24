package com.swmansion.routinetracker.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.di.MockAppContainer
import com.swmansion.routinetracker.model.Routine
import com.tweener.alarmee.createAlarmeeService
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class CreateRoutineScreenRoborazziTest {

    @Test
    fun shouldCaptureCreateRoutineScreenInitialState() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()

        setupCreateRoutineScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "create_routine_initial_state.png")
        onNodeWithText("Create Routine")
            .captureRoboImage(this, filePath = "create_routine_title.png")
    }

    @Test
    fun shouldCaptureCreateRoutineScreenWithNameEntered() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()
        val routineName = "Test Routine"

        setupCreateRoutineScreenContent(testAppContainer)

        onNodeWithText("Routine Name").performTextInput(routineName)
        onRoot().captureRoboImage(this, filePath = "create_routine_with_name.png")
    }

    @Test
    fun shouldCaptureCreateRoutineScreenWithAllSections() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()

        setupCreateRoutineScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "create_routine_all_sections.png")
        onNodeWithText("Days of Week (optional)")
            .captureRoboImage(this, filePath = "create_routine_days_section.png")
    }

    @Test
    fun shouldCaptureCreateRoutineScreenActionButtons() = runComposeUiTest {
        val testAppContainer = createTestAppContainer()

        setupCreateRoutineScreenContent(testAppContainer)

        onNodeWithText("Create")
            .captureRoboImage(this, filePath = "create_routine_create_button.png")
        onNodeWithText("Discard")
            .captureRoboImage(this, filePath = "create_routine_discard_button.png")
    }

    private fun ComposeUiTest.setupCreateRoutineScreenContent(testAppContainer: MockAppContainer) {
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalViewModelStoreOwner provides viewModelStoreOwner,
                    LocalAppContainer provides testAppContainer,
                ) {
                    CreateRoutineScreen(navController = rememberNavController())
                }
            }
        }
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow, alarmeeService = createAlarmeeService())
}

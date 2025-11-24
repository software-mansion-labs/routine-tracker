package com.swmansion.routinetracker.viewmodel

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.di.MockAppContainer
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.screen.RoutinesScreen
import com.tweener.alarmee.createAlarmeeService
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class HomeViewModelRoborazziTest {

    @Test
    fun shouldCaptureRoutinesScreenWithMultipleRoutines() = runComposeUiTest {
        val routines =
            listOf(
                Routine(id = 1L, name = "Morning Routine", time = "08:00"),
                Routine(id = 2L, name = "Evening Routine", time = "20:00"),
                Routine(id = 3L, name = "Workout Routine", time = "18:00"),
                Routine(id = 4L, name = "Reading Routine"),
            )
        val routinesFlow = MutableStateFlow(routines)
        val testAppContainer = createTestAppContainer(routinesFlow)

        setupRoutinesScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "routines_multiple_routines.png")
    }

    @Test
    fun shouldCaptureRoutinesScreenWithRoutinesWithoutTime() = runComposeUiTest {
        val routines =
            listOf(
                Routine(id = 1L, name = "Simple Routine 1"),
                Routine(id = 2L, name = "Simple Routine 2"),
            )
        val routinesFlow = MutableStateFlow(routines)
        val testAppContainer = createTestAppContainer(routinesFlow)

        setupRoutinesScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "routines_without_time.png")
        onNodeWithText("Simple Routine 1")
            .captureRoboImage(this, filePath = "routines_simple_routine.png")
    }

    private fun ComposeUiTest.setupRoutinesScreenContent(testAppContainer: MockAppContainer) {
        val viewModelStoreOwner = MockViewModelStoreOwner()

        setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalViewModelStoreOwner provides viewModelStoreOwner,
                    LocalAppContainer provides testAppContainer,
                ) {
                    RoutinesScreen(navController = rememberNavController())
                }
            }
        }
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow, alarmeeService = createAlarmeeService())
}

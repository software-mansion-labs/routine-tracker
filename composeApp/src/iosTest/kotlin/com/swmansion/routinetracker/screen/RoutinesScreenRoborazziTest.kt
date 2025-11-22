package com.swmansion.routinetracker.screen

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
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class RoutinesScreenRoborazziTest {

    @Test
    fun shouldCaptureEmptyRoutinesScreen() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))

        setupRoutinesScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "routines_empty_screen.png")
        onNodeWithText("No routines yet")
            .captureRoboImage(this, filePath = "routines_empty_state.png")
    }

    @Test
    fun shouldCaptureRoutinesScreenWithRoutines() = runComposeUiTest {
        val routines =
            MutableStateFlow(
                listOf(
                    Routine(id = 1L, name = "Morning Routine", time = "08:00"),
                    Routine(id = 2L, name = "Evening Routine", time = "20:00"),
                    Routine(id = 3L, name = "Workout Routine"),
                )
            )
        val testAppContainer = createTestAppContainer(routines)

        setupRoutinesScreenContent(testAppContainer)

        onRoot().captureRoboImage(this, filePath = "routines_with_routines.png")
        onNodeWithText("Morning Routine")
            .captureRoboImage(this, filePath = "routines_morning_routine.png")
    }

    @Test
    fun shouldCaptureRoutinesScreenTopBar() = runComposeUiTest {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))

        setupRoutinesScreenContent(testAppContainer)

        onNodeWithText("My Routines").captureRoboImage(this, filePath = "routines_top_bar.png")
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
    ) = MockAppContainer(routinesFlow)
}

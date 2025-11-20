package com.swmansion.routinetracker.viewmodel

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import com.swmansion.routinetracker.di.LocalAppContainer
import com.swmansion.routinetracker.mock.MockViewModelStoreOwner
import com.swmansion.routinetracker.mock.di.MockAppContainer
import com.swmansion.routinetracker.model.Routine
import com.swmansion.routinetracker.screen.RoutinesScreen
import com.swmansion.routinetracker.utils.ScreenshotTests
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [28])
@OptIn(ExperimentalTestApi::class)
@Category(ScreenshotTests::class)
class HomeViewModelRoborazziTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule =
        RoborazziRule(composeRule = composeTestRule, captureRoot = composeTestRule.onRoot())

    @Test
    fun shouldCaptureRoutinesScreenWithMultipleRoutines() {
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

        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun shouldCaptureRoutinesScreenWithRoutinesWithoutTime() {
        val routines =
            listOf(
                Routine(id = 1L, name = "Simple Routine 1"),
                Routine(id = 2L, name = "Simple Routine 2"),
            )
        val routinesFlow = MutableStateFlow(routines)
        val testAppContainer = createTestAppContainer(routinesFlow)

        setupRoutinesScreenContent(testAppContainer)

        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("Simple Routine 1").captureRoboImage()
    }

    private fun setupRoutinesScreenContent(testAppContainer: MockAppContainer) {
        val viewModelStoreOwner = MockViewModelStoreOwner()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                PreviewContextConfigurationEffect()
                RoutinesScreen(navController = rememberNavController())
            }
        }
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow)
}

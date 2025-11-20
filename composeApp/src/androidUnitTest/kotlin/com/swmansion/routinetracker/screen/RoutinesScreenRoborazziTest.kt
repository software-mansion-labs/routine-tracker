package com.swmansion.routinetracker.screen

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
class RoutinesScreenRoborazziTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule =
        RoborazziRule(composeRule = composeTestRule, captureRoot = composeTestRule.onRoot())

    @Test
    fun shouldCaptureEmptyRoutinesScreen() {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))

        setupRoutinesScreenContent(testAppContainer)

        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("No routines yet").captureRoboImage()
    }

    @Test
    fun shouldCaptureRoutinesScreenWithRoutines() {
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

        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("Morning Routine").captureRoboImage()
    }

    @Test
    fun shouldCaptureRoutinesScreenTopBar() {
        val testAppContainer = createTestAppContainer(MutableStateFlow(emptyList()))

        setupRoutinesScreenContent(testAppContainer)

        composeTestRule.onNodeWithText("My Routines").captureRoboImage()
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

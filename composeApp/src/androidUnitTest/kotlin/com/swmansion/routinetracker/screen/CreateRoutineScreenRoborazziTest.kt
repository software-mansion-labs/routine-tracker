package com.swmansion.routinetracker.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
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
class CreateRoutineScreenRoborazziTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule =
        RoborazziRule(
            composeRule = composeTestRule,
            captureRoot = composeTestRule.onRoot(),
        )

    @Test
    fun shouldCaptureCreateRoutineScreenInitialState() {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                PreviewContextConfigurationEffect()
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("Create Routine").captureRoboImage()
    }

    @Test
    fun shouldCaptureCreateRoutineScreenWithNameEntered() {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()
        val routineName = "Test Routine"

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                PreviewContextConfigurationEffect()
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Routine Name").performTextInput(routineName)
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun shouldCaptureCreateRoutineScreenWithAllSections() {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                PreviewContextConfigurationEffect()
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("Days of Week (optional)").captureRoboImage()
    }

    @Test
    fun shouldCaptureCreateRoutineScreenActionButtons() {
        val testAppContainer = createTestAppContainer()
        val viewModelStoreOwner = MockViewModelStoreOwner()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalAppContainer provides testAppContainer,
            ) {
                PreviewContextConfigurationEffect()
                CreateRoutineScreen(navController = rememberNavController())
            }
        }

        composeTestRule.onNodeWithText("Create").captureRoboImage()
        composeTestRule.onNodeWithText("Discard").captureRoboImage()
    }

    private fun createTestAppContainer(
        routinesFlow: MutableStateFlow<List<Routine>> = MutableStateFlow(emptyList())
    ) = MockAppContainer(routinesFlow)
}

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziRule
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [28], manifest = Config.NONE)
@OptIn(ExperimentalTestApi::class)
class ExampleAndroidTest {
    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val roborazziRule =
        RoborazziRule(
            composeRule = composeTestRule,
            captureRoot = composeTestRule.onRoot(),
            options = RoborazziRule.Options(outputDirectoryPath = "src/androidUnitTest/resources"),
        )

    @Test
    fun test() {
        composeTestRule.setContent {
            MaterialTheme {
                Column { Button(modifier = Modifier, onClick = {}) { Text("Hello World") } }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
        composeTestRule.onNodeWithText("Hello World").captureRoboImage()
    }
}

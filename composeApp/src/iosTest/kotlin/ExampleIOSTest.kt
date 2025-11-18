import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runComposeUiTest
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class, ExperimentalRoborazziApi::class)
class ExampleIOSTest {

    @Test
    fun test() {
        runComposeUiTest {
            setContent {
                MaterialTheme {
                    Column { Button(modifier = Modifier, onClick = {}) { Text("Hello World") } }
                }
            }
            onRoot().captureRoboImage(this, filePath = "test_1")
            onNodeWithText("Hello World").captureRoboImage(this, filePath = "test_2")
        }
    }
}

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 512.dp, height = 320.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        val density = LocalDensity.current

        MaterialTheme {
            Canvas(modifier = Modifier.fillMaxSize()) {
                with(density) {
                    repeat(512) {
                        drawRect(
                            Color.Blue,
                            topLeft = Offset(it.dp.toPx(), 10.dp.toPx()),
                            size = Size(1.dp.toPx(), 1.dp.toPx())
                        )
                    }

                }
            }
        }
    }
}
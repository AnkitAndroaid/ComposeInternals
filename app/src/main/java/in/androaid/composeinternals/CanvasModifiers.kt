import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun CanvasModifiersDemo(modifier: Modifier = Modifier) {

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(color = Color.Red )
    }

}

@Preview(showBackground = true)
@Composable
private fun CanvasModifiersDemoPreview() {
    CanvasModifiersDemo()
}
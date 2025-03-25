import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun CanvasModifiersDemo(modifier: Modifier = Modifier) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Green)
        .drawWithContent {
            drawCircle(color = Color.Red)
            drawContent()
        }
       ,
        contentAlignment = Alignment.Center
    ){
        Text("Hello world")
    }

}

@Preview(showBackground = true)
@Composable
private fun CanvasModifiersDemoPreview() {
    CanvasModifiersDemo()
}
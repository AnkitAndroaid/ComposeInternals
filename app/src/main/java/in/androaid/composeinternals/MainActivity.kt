package `in`.androaid.composeinternals

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.androaid.composeinternals.ui.theme.ComposeInternalsTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeInternalsTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->

                    Box(modifier = Modifier.padding(innerPadding)){
                        InsuranceRateChart(innerPadding)
                    }

                }
            }
        }
    }
}

@Composable
fun InsuranceRateChart(paddingValues: PaddingValues) {
    val density = LocalDensity.current
    val paddingPx = with(density) { 50.dp.toPx() }
    val tooltipWidth = with(density) { 140.dp.toPx() }
    val tooltipHeight = with(density) { 70.dp.toPx() }
    val pointRadiusPx = with(density) { 4.dp.toPx() }

    val months = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb")
    val yourRate = List(6) { 37f }
    val averageRate = listOf(110f, 115f, 140f, 160f, 220f, 292f)

    val maxRate = (averageRate + yourRate).maxOrNull() ?: 0f
    val minRate = 0f

    val padding = 50.dp
    val pointRadius = 4.dp


    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val width = size.width
                    val xStep = (size.width - paddingPx) / (months.size - 1)
                    val tappedX = offset.x
                    val index =
                        ((offset.x - paddingPx) / xStep)
                            .roundToInt()
                            .coerceIn(0, months.lastIndex)
                            .coerceIn(0, months.lastIndex)
                    selectedIndex.value = index
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            val chartHeight = size.height - paddingPx
            val chartWidth = size.width - paddingPx
            val xStep = chartWidth / (months.size - 1)
            val yStep = chartHeight / (maxRate - minRate)

            // Draw Y-axis lines and labels
            val yValues = listOf(110f, 158f, 205f, 253f, 300f)
            yValues.forEach { yValue ->
                val y = chartHeight - ((yValue - minRate) * yStep)
                drawLine(
                    color = Color.LightGray,
                    start = Offset(paddingPx, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "$${yValue.toInt()}",
                    0f,
                    y,
                    Paint().apply {
                        textSize = 30f
                        color = android.graphics.Color.DKGRAY
                    }
                )
            }

            fun getPoints(values: List<Float>): List<Offset> {
                return values.mapIndexed { index, value ->
                    val x = paddingPx + xStep * index
                    val y = chartHeight - ((value - minRate) * yStep)
                    Offset(x, y)
                }
            }

            val avgPoints = getPoints(averageRate)
            val yourPoints = getPoints(yourRate)

            // Draw dashed average rate line
            for (i in 0 until avgPoints.lastIndex) {
                drawLine(
                    color = Color.Gray,
                    start = avgPoints[i],
                    end = avgPoints[i + 1],
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f)) // Dashed line
                )
            }

            // Draw solid your rate line
            for (i in 0 until yourPoints.lastIndex) {
                drawLine(
                    color = Color(0xFF00C853), // Green line
                    start = yourPoints[i],
                    end = yourPoints[i + 1],
                    strokeWidth = 4f
                )
            }

            // Draw X-axis labels
            months.forEachIndexed { index, label ->
                val x = paddingPx + xStep * index
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x - 20f,
                    size.height,
                    Paint().apply {
                        textSize = 30f
                        color = android.graphics.Color.DKGRAY
                    }
                )
            }

//            // Draw dots on both lines
//            (avgPoints + yourPoints).forEach {
//                drawCircle(
//                    color = Color.Black,
//                    center = it,
//                    radius = pointRadiusPx
//                )
//            }

            // Tooltip on tap (shown above average line)
            selectedIndex.value?.let { index ->
                val avgPt = avgPoints[index]
                val yourPt = yourPoints[index]

                val tooltipLines = listOf(
                    "You: $${yourRate[index].toInt()}",
                    "Avg: $${averageRate[index].toInt()}"
                )

                val paint = Paint().apply {
                    textSize = 28f
                    color = android.graphics.Color.WHITE
                    isAntiAlias = true
                }

                val maxLineWidth = tooltipLines.maxOf { paint.measureText(it) }
                val lineHeight = 34f
                val tooltipPadding = 16f
                val tooltipWidth = maxLineWidth + tooltipPadding * 2
                val tooltipHeight = lineHeight * tooltipLines.size + tooltipPadding

                val cornerRadius = 20f
                val triangleHeight = 16f
                val triangleWidth = 24f
                val verticalSpacing = 20f

                val canvasWidth = size.width
                val tooltipXCentered = avgPt.x - tooltipWidth / 2

                // Clamp tooltip so it stays within screen horizontally
                val tooltipLeft = tooltipXCentered.coerceIn(0f, canvasWidth - tooltipWidth)
                val tooltipRight = tooltipLeft + tooltipWidth

                // ➕ Check available space on top
                val spaceAbove = avgPt.y
                val spaceBelow = size.height - avgPt.y

                val showAbove = spaceAbove > tooltipHeight + triangleHeight + verticalSpacing

                val tooltipTop = if (showAbove) {
                    // Tooltip above the data point
                    avgPt.y - tooltipHeight - triangleHeight - verticalSpacing
                } else {
                    // Tooltip below the data point
                    avgPt.y + triangleHeight + verticalSpacing
                }

                // Constrain triangle within tooltip width
                val triangleCenterX = avgPt.x.coerceIn(
                    tooltipLeft + triangleWidth / 2,
                    tooltipRight - triangleWidth / 2
                )

                // 🧱 Tooltip box
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(tooltipLeft, tooltipTop),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Fill
                )

                // 🔻 Triangle (below box if tooltip is above, else above box)
                val trianglePath = androidx.compose.ui.graphics.Path().apply {
                    if (showAbove) {
                        // Triangle points down (below tooltip)
                        moveTo(triangleCenterX - triangleWidth / 2, tooltipTop + tooltipHeight)
                        lineTo(triangleCenterX + triangleWidth / 2, tooltipTop + tooltipHeight)
                        lineTo(triangleCenterX, tooltipTop + tooltipHeight + triangleHeight)
                    } else {
                        // Triangle points up (above tooltip)
                        moveTo(triangleCenterX - triangleWidth / 2, tooltipTop)
                        lineTo(triangleCenterX + triangleWidth / 2, tooltipTop)
                        lineTo(triangleCenterX, tooltipTop - triangleHeight)
                    }
                    close()
                }
                drawPath(trianglePath, Color.Black)

                // 📝 Text inside tooltip
                tooltipLines.forEachIndexed { i, line ->
                    drawContext.canvas.nativeCanvas.drawText(
                        line,
                        tooltipLeft + tooltipPadding,
                        tooltipTop + tooltipPadding + (i + 1) * lineHeight - 8f,
                        paint
                    )
                }

                // 🔴 Highlight selected points
                drawCircle(Color.Red, 8f, avgPt)
                drawCircle(Color.Red, 8f, yourPt)
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInsuranceChart() {
    InsuranceRateChart(PaddingValues())
}


@Composable
fun ImageExample() {
    Image(
        painterResource(R.drawable.smart_ac_controller),
        contentDescription = "Smart AC Controller",
        modifier = Modifier
            .fillMaxSize()
            .size(100.dp)
    )
}


@Composable
fun ImageExample2() {
    Image(
        painterResource(R.drawable.smart_ac_controller),
        contentDescription = "Smart AC Controller",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(unbounded = false)
            .size(100.dp)
    )
}


//@Preview
//@Composable
//fun ImageExamplePreview(){
//    ImageExample()
//}
//
//@Preview
//@Composable
//fun ImageExample2Preview(){
//    ImageExample2()
//}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ComposeInternalsTheme {
//        Greeting("Android")
//    }
//}
package `in`.androaid.composeinternals

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
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
                        InsuranceRateChart()
                    }

                }
            }
        }
    }
}

@Composable
fun InsuranceRateChart() {
    val months = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb")
    val yourRate = List(months.size) { 37f }
    val averageRate = listOf(110f, 115f, 140f, 160f, 220f, 292f)

    val selectedIndex = remember { mutableStateOf<Int?>(null) }

    val density = LocalDensity.current
    val chartPadding = 16.dp
    val chartInnerPadding = 8.dp
    val pointRadius = 4.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val chartPaddingPx = with(density) { chartPadding.toPx() }
                    val chartWidth = size.width - 2 * chartPaddingPx
                    val xStep = chartWidth / (months.size - 1)
                    val index = ((offset.x - chartPaddingPx) / xStep)
                        .roundToInt()
                        .coerceIn(0, months.lastIndex)
                    selectedIndex.value = index
                }
            }
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = chartPadding)
        ) {
            val chartPaddingPx = with(density) { chartPadding.toPx() }
            val innerPadPx = with(density) { chartInnerPadding.toPx() }

            val drawableLeft = chartPaddingPx + innerPadPx
            val drawableRight = size.width - chartPaddingPx - innerPadPx
            val chartWidth = drawableRight - drawableLeft
            val chartHeight = size.height - chartPaddingPx

            val maxRate = (yourRate + averageRate).maxOrNull() ?: 0f
            val yStep = chartHeight / maxRate
            val xStep = chartWidth / (months.size - 1)

            val avgPoints = averageRate.mapIndexed { i, value ->
                Offset(x = drawableLeft + i * xStep, y = chartHeight - value * yStep)
            }
            val yourPoints = yourRate.mapIndexed { i, value ->
                Offset(x = drawableLeft + i * xStep, y = chartHeight - value * yStep)
            }

            // Y-axis lines
            val yValues = listOf(110f, 158f, 205f, 253f, 300f)
            yValues.forEach { y ->
                val yOffset = chartHeight - y * yStep
                drawLine(
                    color = Color.LightGray,
                    start = Offset(drawableLeft, yOffset),
                    end = Offset(drawableRight, yOffset),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "$${y.toInt()}",
                    0f,
                    yOffset,
                    Paint().apply {
                        textSize = 30f
                        color = android.graphics.Color.DKGRAY
                    }
                )
            }

            // X-axis labels
            months.forEachIndexed { i, month ->
                val x = drawableLeft + i * xStep
                drawContext.canvas.nativeCanvas.drawText(
                    month,
                    x - 20f,
                    size.height,
                    Paint().apply {
                        textSize = 30f
                        color = android.graphics.Color.DKGRAY
                    }
                )
            }

            // Draw lines
            for (i in 0 until avgPoints.lastIndex) {
                drawLine(
                    color = Color.Gray,
                    start = avgPoints[i],
                    end = avgPoints[i + 1],
                    strokeWidth = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                )
            }

            for (i in 0 until yourPoints.lastIndex) {
                drawLine(
                    color = Color(0xFF00C853),
                    start = yourPoints[i],
                    end = yourPoints[i + 1],
                    strokeWidth = 4f
                )
            }

            // Tooltip logic
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
                val verticalSpacing = 12f

                val spaceAbove = avgPt.y
                val showAbove = spaceAbove > tooltipHeight + triangleHeight + verticalSpacing

                val tooltipTop = if (showAbove) {
                    avgPt.y - tooltipHeight - triangleHeight - verticalSpacing
                } else {
                    avgPt.y + triangleHeight + verticalSpacing
                }

                val tooltipXCentered = avgPt.x - tooltipWidth / 2
                val tooltipLeft = tooltipXCentered.coerceIn(
                    drawableLeft,
                    drawableRight - tooltipWidth
                )
                val tooltipRight = tooltipLeft + tooltipWidth

                // Decide triangle placement logic
                val triangleSide = when {
                    avgPt.x < tooltipLeft + cornerRadius -> "LEFT"
                    avgPt.x > tooltipRight - cornerRadius -> "RIGHT"
                    else -> "CENTER"
                }

                val triangleCenterX = when (triangleSide) {
                    "LEFT" -> tooltipLeft + cornerRadius
                    "RIGHT" -> tooltipRight - cornerRadius
                    else -> avgPt.x
                }

                // Draw tooltip box
                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(tooltipLeft, tooltipTop),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Fill
                )

                // Angled triangle
                val trianglePath = androidx.compose.ui.graphics.Path().apply {
                    if (showAbove) {
                        when (triangleSide) {
                            "LEFT" -> {
                                moveTo(triangleCenterX, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX + triangleWidth, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX + triangleWidth / 2, tooltipTop + tooltipHeight + triangleHeight)
                            }
                            "RIGHT" -> {
                                moveTo(triangleCenterX, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX - triangleWidth, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX - triangleWidth / 2, tooltipTop + tooltipHeight + triangleHeight)
                            }
                            else -> {
                                moveTo(triangleCenterX - triangleWidth / 2, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX + triangleWidth / 2, tooltipTop + tooltipHeight)
                                lineTo(triangleCenterX, tooltipTop + tooltipHeight + triangleHeight)
                            }
                        }
                    } else {
                        when (triangleSide) {
                            "LEFT" -> {
                                moveTo(triangleCenterX, tooltipTop)
                                lineTo(triangleCenterX + triangleWidth, tooltipTop)
                                lineTo(triangleCenterX + triangleWidth / 2, tooltipTop - triangleHeight)
                            }
                            "RIGHT" -> {
                                moveTo(triangleCenterX, tooltipTop)
                                lineTo(triangleCenterX - triangleWidth, tooltipTop)
                                lineTo(triangleCenterX - triangleWidth / 2, tooltipTop - triangleHeight)
                            }
                            else -> {
                                moveTo(triangleCenterX - triangleWidth / 2, tooltipTop)
                                lineTo(triangleCenterX + triangleWidth / 2, tooltipTop)
                                lineTo(triangleCenterX, tooltipTop - triangleHeight)
                            }
                        }
                    }
                    close()
                }
                drawPath(trianglePath, Color.Black)

                // Tooltip text
                tooltipLines.forEachIndexed { i, line ->
                    drawContext.canvas.nativeCanvas.drawText(
                        line,
                        tooltipLeft + tooltipPadding,
                        tooltipTop + tooltipPadding + (i + 1) * lineHeight - 8f,
                        paint
                    )
                }

                // Highlight points
                drawCircle(Color.Red, with(density) { pointRadius.toPx() }, avgPt)
                drawCircle(Color.Red, with(density) { pointRadius.toPx() }, yourPt)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInsuranceRateChart() {
    InsuranceRateChart()
}

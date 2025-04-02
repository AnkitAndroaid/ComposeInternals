import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

// Configuration data classes and enums remain unchanged...
// Assume they are defined here as in the previous version

@Composable
fun InsuranceRateChart(
    data: RateChartData,
    config: ChartConfiguration = ChartConfiguration(),
    style: ChartStyle = ChartStyle(
        yourLineColor = Color(0xFF00C853),
        avgLineColor = Color.Gray
    ),
    dimensions: ChartDimensions = ChartDimensions(),
    tooltipConfig: TooltipConfig = TooltipConfig { index, your, avg ->
        listOf("You: $${your.toInt()}", "Avg: $${avg.toInt()}")
    }
) {
    val selectedIndex = remember { mutableStateOf<Int?>(null) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.height)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val chartPaddingPx = with(density) { dimensions.outerPadding.toPx() }
                    val chartWidth = size.width - 2 * chartPaddingPx
                    val xStep = chartWidth / (data.labels.size - 1)
                    val index = ((offset.x - chartPaddingPx) / xStep)
                        .roundToInt()
                        .coerceIn(0, data.labels.lastIndex)
                    selectedIndex.value = index
                }
            }
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensions.outerPadding)
        ) {
            val chartPaddingPx = with(density) { dimensions.outerPadding.toPx() }
            val innerPadPx = with(density) { dimensions.innerPadding.toPx() }

            val drawableLeft = chartPaddingPx + innerPadPx
            val drawableRight = size.width - chartPaddingPx - innerPadPx
            val chartWidth = drawableRight - drawableLeft
            val chartHeight = size.height - chartPaddingPx

            val maxRate = (data.yourRates + data.averageRates).maxOrNull() ?: 0f
            val yStep = chartHeight / maxRate
            val xStep = chartWidth / (data.labels.size - 1)

            val avgPoints = data.averageRates.mapIndexed { i, value ->
                Offset(drawableLeft + i * xStep, chartHeight - value * yStep)
            }
            val yourPoints = data.yourRates.mapIndexed { i, value ->
                Offset(drawableLeft + i * xStep, chartHeight - value * yStep)
            }

            drawYAxisLabels(config, yStep, chartHeight, drawableLeft, drawableRight)
            drawXAxisLabels(data.labels, drawableLeft, xStep, size.height)
            drawLineGraph(avgPoints, style.avgLineColor, dashed = style.showAvgDashedLine)
            drawLineGraph(yourPoints, style.yourLineColor, dashed = false)

            if (tooltipConfig.showTooltip) {
                selectedIndex.value?.let { index ->
                    drawTooltip(
                        index = index,
                        avgPt = avgPoints[index],
                        yourPt = yourPoints[index],
                        yourRate = data.yourRates[index],
                        avgRate = data.averageRates[index],
                        chartWidth = chartWidth,
                        drawableLeft = drawableLeft,
                        drawableRight = drawableRight,
                        sizeHeight = size.height,
                        style = style,
                        dimensions = dimensions,
                        density = density,
                        tooltipConfig = tooltipConfig
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawYAxisLabels(
    config: ChartConfiguration,
    yStep: Float,
    chartHeight: Float,
    drawableLeft: Float,
    drawableRight: Float
) {
    config.yAxisGridValues.forEach { y ->
        val yOffset = chartHeight - y * yStep
        drawLine(
            color = Color.LightGray,
            start = Offset(drawableLeft, yOffset),
            end = Offset(drawableRight, yOffset),
            strokeWidth = 1f,
            pathEffect = if (config.showDashedLines) PathEffect.dashPathEffect(floatArrayOf(10f, 10f)) else null
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
}

private fun DrawScope.drawXAxisLabels(
    labels: List<String>,
    drawableLeft: Float,
    xStep: Float,
    chartBottom: Float
) {
    labels.forEachIndexed { i, label ->
        val x = drawableLeft + i * xStep
        drawContext.canvas.nativeCanvas.drawText(
            label,
            x - 20f,
            chartBottom,
            Paint().apply {
                textSize = 30f
                color = android.graphics.Color.DKGRAY
            }
        )
    }
}

private fun DrawScope.drawLineGraph(
    points: List<Offset>,
    color: Color,
    dashed: Boolean = false
) {
    for (i in 0 until points.lastIndex) {
        drawLine(
            color = color,
            start = points[i],
            end = points[i + 1],
            strokeWidth = 3f,
            pathEffect = if (dashed) PathEffect.dashPathEffect(floatArrayOf(12f, 12f)) else null
        )
    }
}

private fun DrawScope.drawTooltip(
    index: Int,
    avgPt: Offset,
    yourPt: Offset,
    yourRate: Float,
    avgRate: Float,
    chartWidth: Float,
    drawableLeft: Float,
    drawableRight: Float,
    sizeHeight: Float,
    style: ChartStyle,
    dimensions: ChartDimensions,
    density: androidx.compose.ui.unit.Density,
    tooltipConfig: TooltipConfig
) {
    val tooltipLines = tooltipConfig.tooltipFormatter(index, yourRate, avgRate)

    val paint = Paint().apply {
        textSize = 28f
        color = style.tooltipTextColor.toArgb()
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
    val spaceBelow = sizeHeight - avgPt.y
    val showAbove = spaceAbove > tooltipHeight + triangleHeight + verticalSpacing

    val tooltipTop = if (showAbove) {
        avgPt.y - tooltipHeight - triangleHeight - verticalSpacing
    } else {
        avgPt.y + triangleHeight + verticalSpacing
    }

    val tooltipXCentered = avgPt.x - tooltipWidth / 2
    val tooltipLeft = tooltipXCentered.coerceIn(drawableLeft, drawableRight - tooltipWidth)
    val tooltipRight = tooltipLeft + tooltipWidth

    val triangleCenterX = avgPt.x.coerceIn(
        tooltipLeft + triangleWidth / 2,
        tooltipRight - triangleWidth / 2
    )

    drawRoundRect(
        color = style.tooltipBackground,
        topLeft = Offset(tooltipLeft, tooltipTop),
        size = Size(tooltipWidth, tooltipHeight),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius),
        style = Fill
    )

    val trianglePath = androidx.compose.ui.graphics.Path().apply {
        if (showAbove) {
            moveTo(triangleCenterX - triangleWidth / 2, tooltipTop + tooltipHeight)
            lineTo(triangleCenterX + triangleWidth / 2, tooltipTop + tooltipHeight)
            lineTo(triangleCenterX, tooltipTop + tooltipHeight + triangleHeight)
        } else {
            moveTo(triangleCenterX - triangleWidth / 2, tooltipTop)
            lineTo(triangleCenterX + triangleWidth / 2, tooltipTop)
            lineTo(triangleCenterX, tooltipTop - triangleHeight)
        }
        close()
    }
    drawPath(trianglePath, style.tooltipBackground)

    tooltipLines.forEachIndexed { i, line ->
        drawContext.canvas.nativeCanvas.drawText(
            line,
            tooltipLeft + tooltipPadding,
            tooltipTop + tooltipPadding + (i + 1) * lineHeight - 8f,
            paint
        )
    }

    drawCircle(style.pointHighlightColor, with(density) { dimensions.pointRadius.toPx() }, avgPt)
    drawCircle(style.pointHighlightColor, with(density) { dimensions.pointRadius.toPx() }, yourPt)
}

@Preview(showBackground = true)
@Composable
fun PreviewInsuranceRateChart() {
    InsuranceRateChart(
        data = RateChartData(
            labels = listOf("Sep", "Oct", "Nov", "Dec", "Jan", "Feb"),
            yourRates = List(6) { 37f },
            averageRates = listOf(110f, 115f, 140f, 160f, 220f, 292f)
        ),
        config = ChartConfiguration(
            yAxisGridValues = listOf(110f, 158f, 205f, 253f, 300f)
        )
    )
}
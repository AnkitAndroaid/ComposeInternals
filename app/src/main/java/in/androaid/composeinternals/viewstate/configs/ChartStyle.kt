import androidx.compose.ui.graphics.Color

data class ChartStyle(
    val yourLineColor: Color,
    val avgLineColor: Color,
    val avgLineType: LineType = LineType.Dashed,
    val showAvgDashedLine: Boolean = true,
    val tooltipBackground: Color = Color.Black,
    val tooltipTextColor: Color = Color.White,
    val pointHighlightColor: Color = Color.Red
)

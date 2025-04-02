data class TooltipConfig(
    val showTooltip: Boolean = true,
    val tooltipFormatter: (index: Int, yourValue: Float, avgValue: Float) -> List<String>
)
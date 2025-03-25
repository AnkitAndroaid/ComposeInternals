package `in`.androaid.composeinternals

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ComposeCompilerDemo(text: String, modifier: Modifier) {
    Text(text = text,
        modifier = modifier)

}
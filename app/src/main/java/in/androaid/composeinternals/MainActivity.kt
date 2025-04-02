package `in`.androaid.composeinternals

import ChartConfiguration
import InsuranceRateChart1
import RateChartData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import `in`.androaid.composeinternals.ui.theme.ComposeInternalsTheme

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
                        InsuranceRateChart1(
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

                }
            }
        }
    }
}


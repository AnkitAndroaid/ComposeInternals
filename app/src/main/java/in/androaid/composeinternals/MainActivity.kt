package `in`.androaid.composeinternals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.androaid.composeinternals.ui.theme.ComposeInternalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeInternalsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
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
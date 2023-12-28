package to.increment.llama

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import to.increment.llama.ui.theme.LlamaTheme
import java.io.File

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stateFlow = MutableStateFlow("")

        lifecycleScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val dir = File(this@MainActivity.filesDir, "model")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, "alpaca7b.bin")

            Log.w(
                "TAG",
                "path: ${file.absolutePath} isDir=${file.isDirectory} exists=${file.exists()} readable=${file.canRead()}"
            )

            val result = NativeLib().stringFromJNI(
                file.absolutePath,
                "tell me about alpacas",
                object : CallbackHandler {
                    override fun callbackMethod(message: String?) {
                        Log.w("TAG", "callback from native: $message")
                        stateFlow.value += message
                    }
                }
            )
        }

        setContent {
            LlamaTheme {
                val state = stateFlow.collectAsState()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "Alpaca demo",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            modifier = Modifier.alpha(if (state.value.isBlank()) 0.56f else 1f),
                            text = state.value.takeUnless { it.isBlank() } ?: "Computing...",
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LlamaTheme {
        Text(
            text = "Android"
        )
    }
}
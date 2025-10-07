package io.gnocchi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.gnocchi.ui.theme.GnocchiTheme
import io.gnocchi.viewmodel_example.ExampleAction
import io.gnocchi.viewmodel_example.ExampleState
import io.gnocchi.viewmodel_example.ExampleViewModel
import io.gnocchi_compose.rememberGnocchiState

class MainActivity : ComponentActivity() {

    private val viewModel = ExampleViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            GnocchiTheme {
                // A surface container using the 'background' color from the theme


                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RenderState()
                }
            }
        }
    }

    @Composable
    private fun RenderState() {
        val state = rememberGnocchiState(viewModel.state)

        Log.d("State", state.value.toString())

        when (val state = state.value) {
            ExampleState.Empty -> viewModel.action(ExampleAction.Load)
            is ExampleState.Loading -> RenderLoad()
            is ExampleState.DataLoaded -> RenderData(state.data)
        }
    }
}

@Composable
private fun RenderData(data: List<String>) {

    data.forEach { item ->
        Text(text = item)
    }
}

@Composable
private fun RenderLoad() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GnocchiTheme {
        Greeting("Android")
    }
}
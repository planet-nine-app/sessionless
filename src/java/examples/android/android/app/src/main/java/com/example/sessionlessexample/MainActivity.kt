package com.example.sessionlessexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sessionlessexample.ui.theme.SessionlessExampleTheme
import com.planetnine.sessionless.Sessionless
import com.planetnine.sessionless.SessionlessImpl
import com.planetnine.sessionless.Keys

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SessionlessExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

fun setKeys(input: Keys) {
    println("another message: $input")
}

fun getKeys(input: Keys) {
    println("another message: $input")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SessionlessExampleTheme {
        Greeting("Android")
        val sessionless = SessionlessImpl()
        sessionless.generateKeys(::setKeys, ::getKeys)
    }
}
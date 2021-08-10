package com.example.composevlmtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.composevlmtest.ui.theme.ComposeVlmTestTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeVlmTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    StackView(listOf(R.drawable.img_2,R.drawable.img_3,R.drawable.img_4), this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Helloasdfasdfasdf $name!"
    ,
    modifier = Modifier
        .clickable {

        }
        .padding(horizontal = 10.dp)
    )
}

@Composable
fun testDialog(
){
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Greeting(name = "안녕")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeVlmTestTheme {
        Column(
            modifier =  Modifier.fillMaxWidth()
        ){
            Greeting("Android"
            )

           // Divider(color = Black, modifier = Modifier.width(20.dp).height(10.dp))

            Greeting("Android"
            )
        }
    }
}


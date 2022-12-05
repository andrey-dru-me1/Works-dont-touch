package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card

class AddCardActivity : ComponentActivity() {

    var cardName: String = ""
    var barCode: String = ""
    var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        CardNameEdit()
                        BarCodeEdit()
                        PickImageButton()
                        SaveButton()
                    }
                }
            }
        }
    }

    @Composable
    fun CardNameEdit() {
        val focusRequester = remember { FocusRequester() }
        var text by rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            modifier = Modifier.focusRequester(focusRequester),
            placeholder = { Text("Shop name") },
            onValueChange = {
                text = it
                this.cardName = it
            }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    @Composable
    fun BarCodeEdit() {
        var text by rememberSaveable { mutableStateOf("") }
        TextField(
            value = text,
            placeholder = { Text("Barcode") },
            onValueChange = {
                text = it
                this.barCode = it
            }
        )
    }

    @Composable
    fun PickImageButton() {
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                image = it
            }
        Button(onClick = {launcher.launch("image/*") }) {
            Text("Pick an image")
        }
    }

    @Composable
    fun SaveButton() {
        Button(onClick = {
            DataController.getInstance().putCard(Card(DataController.getInstance().nextId(), cardName, barCode, image))
            finish()
        }) {
            Text("OK")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    KotlinClientTheme {
        Text("Android")
    }
}
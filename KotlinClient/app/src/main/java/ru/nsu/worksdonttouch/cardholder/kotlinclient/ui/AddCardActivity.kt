package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction.ImageSaver
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class AddCardActivity : ComponentActivity() {

    var cardName: String = ""
    var barCode: String = ""
    var image: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
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
            label = { Text("Shop name") },
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
            label = { Text("Barcode") },
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun SaveButton() {
        Button(onClick = {
            val file = File("/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/images/${cardName}")
            try {
                Files.createDirectory(Paths.get("/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/images/"))
            }
            catch (_: Throwable) { }
            file.createNewFile()

            if(image != null) ImageSaver.saveToFile(this, image!!, file)

            DataController.getInstance().putCard(Card(cardName, barCode, Uri.fromFile(file)))
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
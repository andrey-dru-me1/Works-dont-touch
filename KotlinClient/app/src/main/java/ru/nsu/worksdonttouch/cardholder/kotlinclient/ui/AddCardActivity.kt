package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths


class AddCardActivity : ComponentActivity() {

    var cardName: String = ""
    var barCode: String = ""

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
                        val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
                        val launcher = rememberLauncherForActivityResult(
                            ActivityResultContracts.TakePicturePreview()
                        )
                        {
                            bitmap.value = it
                        }

                        CardNameEdit()
                        BarCodeEdit()
                        Button(
                            onClick = { launcher.launch(null) },
                            content = { Text("Pick an image") }
                        )
                        SaveButton(bitmap.value)
                        bitmap.value?.asImageBitmap()?.let {
                            Image(
                                bitmap = it,
                                contentDescription = "Picked image",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .aspectRatio((86.0/54).toFloat())
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun SaveButton(bitmap: Bitmap?) {
        Button(onClick = {
            val path: String = "/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/images/${cardName}"
            val file = File(path)
            try {
                Files.createDirectory(Paths.get("/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/images/"))
            }
            catch (_: Throwable) { }
            file.createNewFile()

            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            val uri: Uri = Uri.fromFile(file)

            DataController.getInstance().putCard(Card(cardName, barCode, uri))
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
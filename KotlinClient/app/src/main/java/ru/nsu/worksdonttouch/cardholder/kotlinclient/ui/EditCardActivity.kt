package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.unit.dp
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.nio.file.Files
import java.nio.file.Paths


class EditCardActivity : ComponentActivity() {

    var cardName: String? = ""
    var barcode: String? = ""

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

                    val card: Card? =  intent.getParcelableExtra<Card>("card")
                    cardName = card?.name
                    barcode = card?.barcode

                    try {
                        Files.delete(Paths.get(card?.imagePath))
                    } catch(e: InvocationTargetException) {
                        Log.d("INFO", "$e")
                    }


                    Column {
                        val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(card?.image) }
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
                            content = { Text("Take a photo") }
                        )
                        SaveButton(bitmap.value, card)
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
        var text by rememberSaveable { mutableStateOf(cardName) }
        TextField(
            value = text ?: "",
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
        var text by rememberSaveable { mutableStateOf(barcode) }
        TextField(
            value = text ?: "",
            label = { Text("Barcode") },
            onValueChange = {
                text = it
                this.barcode = it
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun SaveButton(bitmap: Bitmap?, card: Card?) {
        Button(onClick = {
            val path = "/data/data/ru.nsu.worksdonttouch.cardholder.kotlinclient/files/images/${cardName}"
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

            DataController.getInstance().editCard(card, cardName, this.barcode, bitmap, path)

            finish()
        }) {
            Text("OK")
        }
    }

}
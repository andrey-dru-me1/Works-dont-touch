package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.graphics.Bitmap
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
import androidx.compose.ui.unit.dp
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.data.card.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.ByteArrayOutputStream


class EditCardActivity : ComponentActivity() {

    private var cardName: String? = ""
    private var barcode: String? = ""

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

                    //TODO: delete file

                    Column {
                        val bitmap: MutableState<Bitmap?> = remember { mutableStateOf() }   //get current image
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

            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG,0,stream)
            //TODO: rewrite image

            //TODO: edit card globally
//            DataController.getInstance().editCard(card, cardName, this.barcode, bitmap, path)

            finish()
        }) {
            Text("OK")
        }
    }

}
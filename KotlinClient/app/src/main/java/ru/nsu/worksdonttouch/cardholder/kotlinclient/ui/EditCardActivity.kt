package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme


class EditCardActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val card: Card? = intent.getSerializableExtra("card") as Card?

                    //TODO: delete file

                    Column {
                        val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }   //get current image
                        DataController.getInstance().getImage(card, card?.images?.get(0)
                            ?: 0) { _, data -> runOnUiThread { bitmap.value = BitmapFactory.decodeFile(data.absolutePath) } }

                        val launcher = rememberLauncherForActivityResult(
                            ActivityResultContracts.TakePicturePreview()
                        ) { bitmap.value = it }

                        CardNameEdit(card)
                        BarCodeEdit(card)
                        Button(
                            onClick = { launcher.launch(null) },
                            content = { Text("Take a photo") }
                        )
                        SaveButton(card)
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
    fun CardNameEdit(card: Card?) {
        val focusRequester = remember { FocusRequester() }
        var text by rememberSaveable { mutableStateOf(card?.name) }
        TextField(
            value = text ?: "",
            modifier = Modifier.focusRequester(focusRequester),
            label = { Text("Shop name") },
            onValueChange = {
                text = it
                card?.name = it
            }
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    @Composable
    fun BarCodeEdit(card: Card?) {
        var text by rememberSaveable { mutableStateOf(card?.barcode) }
        TextField(
            value = text ?: "",
            label = { Text("Barcode") },
            onValueChange = {
                text = it
                card?.barcode = it
            }
        )
    }

    @Composable
    fun SaveButton(card: Card?) {
        Button(onClick = {

            DataController.getInstance().editCard(card) {_, _ -> }

            finish()
        }) {
            Text("OK")
        }
    }

}
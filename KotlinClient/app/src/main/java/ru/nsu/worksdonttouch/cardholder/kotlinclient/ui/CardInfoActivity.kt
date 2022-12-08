package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.rememberAsyncImagePainter
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import ru.nsu.worksdonttouch.cardholder.kotlinclient.R
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.card.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Location
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.bitmatrix.converter.BitMatrixConverter
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.File


class CardInfoActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val card: Card = intent.getParcelableExtra("card")!!

                    val openDialog = rememberSaveable { mutableStateOf(false) }
                    var selectedLocation: Location? = null

                    Column {

                        //Main card image (face side)
                        Image(
                            painter = rememberAsyncImagePainter(model = card.images[0]),
                            contentDescription = "Card preview",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .aspectRatio((86.0 / 54).toFloat())
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Text(fontSize = 50.sp, text = card.name)

                        val barcodeString: String = card.barcode ?: ""

                        val writer = Code128Writer()
                        val matrix: BitMatrix = writer.encode(barcodeString, BarcodeFormat.CODE_128, 500, 150)

                        val bitmap = BitMatrixConverter.bitMatrixToBitmap(matrix)

                        Image (
                            bitmap = bitmap.asImageBitmap(),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth(),
                            contentDescription = "Barcode"
                        )

                        Text(text = barcodeString, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                        //Other images of the card
                        Column {
                            card.images.map {
                                var image: File? = null
                                DataController.getInstance().getImage(card, it) { _, file -> image = file}
                                Image(painter = rememberAsyncImagePainter(model = image), contentDescription = card.name)
                            }
                        }

                        Text("Locations:")
                        Column {
                            //TODO: remove sample location
                            ClickableText(
                                text = AnnotatedString("Sample location"),
                                style = TextStyle(color = Color.Blue),
                                onClick = {
                                    selectedLocation = null
                                    openDialog.value = true
                                }
                            )
                            card.locations.map {
                                ClickableText(
                                    text = AnnotatedString(it.name),
                                    style = TextStyle(color = Color.Blue),
                                    onClick = { _ ->
                                        selectedLocation = it
                                        openDialog.value = true
                                    }
                                )
                            }
                        }

                    }
                    Box( contentAlignment = Alignment.BottomEnd ) {
                        Button(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(15.dp)
                                .size(60.dp),
                            onClick = {
                                val intent = Intent(this@CardInfoActivity, EditCardActivity::class.java)
                                intent.putExtra("card", card)
                                startActivity(intent)
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pen),
                                contentDescription = "Edit card"
                            )
                        }
                    }

                    if(openDialog.value && selectedLocation != null) {
                        EditCoordinatesFragment(location = selectedLocation!!) {
                            openDialog.value = false
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EditCoordinatesFragment(location: Location, close: () -> Unit) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.6f)
        ) {
            Box(
                modifier = Modifier
                    .padding(35.dp, 80.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .fillMaxSize()
                    .background(Color.White)
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(50.dp)
                    )
            ) {
                Popup(
                    alignment = Alignment.Center,
                    properties = PopupProperties(),
                    onDismissRequest = { close.apply {  } },
                ) {
                    Text(text = location.name, fontSize = 50.sp)

                    Row {
                        Text(text = "Latitude")
                        Text(text = "Longitude")
                    }

                    location.coordinates.map {
                        Row {
                            Text(text = it.latitude.toString())
                            Text(text = it.longitude.toString())
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    KotlinClientTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)) {
                val expandState = remember {
                    mutableStateOf(true)
                }
                Popup(
                    alignment = Alignment.Center,
                    properties = PopupProperties(),
                    onDismissRequest = { expandState.value = false }
                ) {
                    Button(modifier = Modifier
                        .fillMaxSize()
                        .padding(50.dp), onClick = { }) {
                        Text("Lol")
                    }
                }
            }
        }
    }
}
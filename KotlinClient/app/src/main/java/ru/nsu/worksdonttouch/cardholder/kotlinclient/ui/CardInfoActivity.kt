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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Coordinate
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.location.Location
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.bitmatrix.converter.BitMatrixConverter
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import java.io.File
import java.util.*


class CardInfoActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val cardHelper: Card? = intent.getSerializableExtra("card") as Card?
                    if(cardHelper == null) finish()
                    val card: Card = cardHelper!!

                    val openDialog = rememberSaveable { mutableStateOf(false) }
                    var selectedLocation: Location? = null

                    Column {

                        //Main card image (face side)
                        Image(
                            painter = rememberAsyncImagePainter(model = if (card.images.size  > 0) card.images[0] else null),
                            contentDescription = "Card preview",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .aspectRatio((86.0 / 54).toFloat())
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Text(fontSize = 50.sp, text = card.name)

                        val barcodeString: String = card.barcode ?: ""

                        val writer = Code128Writer()
                        val matrix: BitMatrix =
                            writer.encode(barcodeString, BarcodeFormat.CODE_128, 500, 150)

                        val bitmap = BitMatrixConverter.bitMatrixToBitmap(matrix)

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth(),
                            contentDescription = "Barcode"
                        )

                        Text(
                            text = barcodeString,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        //Other images of the card
                        Column {
                            card.images.map {
                                var image: File? = null
                                DataController.getInstance()
                                    .getImage(card, it) { _, file -> image = file }
                                Image(
                                    painter = rememberAsyncImagePainter(model = image),
                                    contentDescription = card.name
                                )
                            }
                        }

                        Text("Locations:")
                        Column {
                            card.locations.map {
                                ClickableText(text = AnnotatedString(it.name),
                                    style = TextStyle(color = Color.Blue),
                                    onClick = { _ ->
                                        selectedLocation = it
                                        openDialog.value = true
                                    })
                            }
                        }

                    }
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Button(shape = CircleShape,
                            modifier = Modifier
                                .padding(15.dp)
                                .size(60.dp),
                            onClick = {
                                val intent =
                                    Intent(this@CardInfoActivity, EditCardActivity::class.java)
                                intent.putExtra("card", card)
                                startActivity(intent)
                            }) {
                            Image(
                                painter = painterResource(id = R.drawable.pen),
                                contentDescription = "Edit card"
                            )
                        }
                    }

                    if (openDialog.value && selectedLocation != null) {
                        EditCoordinatesFragment(location = selectedLocation!!) {
                            openDialog.value = false
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EditCoordinatesFragment(location: Location, close: () -> Unit) {
    val localLocation = location.clone()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(35.dp, 80.dp)
    ) {
        Popup(properties = PopupProperties(focusable = true),
            alignment = Alignment.Center,
            onDismissRequest = { close() }) {
            Box(
                modifier = Modifier
                    .size(maxWidth, maxHeight)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White)
            ) {

                val latitude = remember { mutableStateOf("") }
                val longitude = remember { mutableStateOf("") }

                val coors: SnapshotStateList<Coordinate?> = remember {
                    val res = mutableStateListOf<Coordinate?>()
                    res.addAll(localLocation.coordinates)
                    res
                }

                Column {

                    Text(text = localLocation.name, fontSize = 50.sp)

                    Row {
                        Column(Modifier.wrapContentSize()) {
                            Text(text = "Latitude")
                            coors.map {
                                Text(text = it?.latitude.toString())
                            }
                            CoordinateField(latitude)
                        }

                        Spacer(modifier = Modifier.padding(6.dp))

                        Column(Modifier.wrapContentSize()) {
                            Text(text = "Longitude")
                            coors.map {
                                Text(text = it?.longitude.toString())
                            }
                            CoordinateField(longitude)
                        }
                    }
                }

                Button(
                    onClick = {
                        coors.add(
                            Coordinate(
                                latitude.value.toDouble(), longitude.value.toDouble()
                            )
                        )
                        longitude.value = ""
                        latitude.value = ""
                        localLocation.coordinates = coors
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(30.dp, 20.dp)
                        .clip(CircleShape)
                ) {
                    Text("Add")
                }

                Button(
                    onClick = {
                        location.coordinates = localLocation.coordinates
                        close()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(30.dp, 20.dp)
                        .clip(CircleShape)
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
fun CoordinateField(text: MutableState<String>) {
    BasicTextField(
        value = text.value,
        onValueChange = { text.value = it },
        singleLine = true,
        modifier = Modifier.border(
                2.dp, Color.LightGray, RoundedCornerShape(5.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    KotlinClientTheme {
        val location = remember {
            mutableStateOf(
                Location(
                    "Быстроном", true, listOf(Coordinate(15.0, 20.0))
                )
            )
        }
        val flag = remember { mutableStateOf(true) }
        if (flag.value) {
            EditCoordinatesFragment(location = location.value, close = { flag.value = false })
        }
        Button(onClick = { flag.value = true }, Modifier.wrapContentSize()) {

        }
    }
}
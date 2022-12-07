package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.oned.Code128Writer
import ru.nsu.worksdonttouch.cardholder.kotlinclient.R
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.interaction.BitMatrixConverter
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme


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
                    Column {
                        Image(
                            bitmap = card.image.asImageBitmap(),
                            contentDescription = "Card preview",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .aspectRatio((86.0 / 54).toFloat())
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Text(fontSize = 50.sp, text = card.name)
                        Text(text = card.barcode)

                        val text: String = card.barcode

                        val writer = Code128Writer()
                        val matrix: BitMatrix = writer.encode(text, BarcodeFormat.CODE_128, 500, 200)

                        val bitmap = BitMatrixConverter.bitMatrixToBitmap(matrix)

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth(),
                            contentDescription = "Barcode"
                        )
                    }
                    Box( contentAlignment = Alignment.BottomEnd ) {
                        Button(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(15.dp)
                                .size(60.dp),
                            onClick = { /*TODO*/ }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pen),
                                contentDescription = "Edit card"
                            )
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
        Text("Android")
    }
}
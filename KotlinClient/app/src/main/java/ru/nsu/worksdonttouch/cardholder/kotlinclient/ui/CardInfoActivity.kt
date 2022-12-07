package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    val card: Card = intent.getParcelableExtra<Card>("card")!!
                    Column {
                        Text(fontSize = 50.sp, text = card.name)
                        Text(text = card.barcode)
                        Image(
                            bitmap = card.image.asImageBitmap(),
                            contentDescription = "Card preview",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .aspectRatio((86.0/54).toFloat())
                                .clip(RoundedCornerShape(10.dp))
                        )
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
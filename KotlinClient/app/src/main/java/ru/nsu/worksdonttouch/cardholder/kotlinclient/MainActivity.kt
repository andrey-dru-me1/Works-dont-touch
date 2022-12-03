package ru.nsu.worksdonttouch.cardholder.kotlinclient

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val pair: List<Card> = listOf(
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        )
                    )
                    val list: MutableList<Card> = ArrayList()
                    for (i in 1..10)
                        list.addAll(pair)
                    CardGrid(list)

                    AddCardButton()
                }
            }
        }
    }
}

data class Card(val name: String, val image: Painter)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardGrid(cards: List<Card>) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
    ) {
        cards.map { item { CardView(it) } }
    }
}

@Composable
fun CardView(card: Card) {

    val mContext = LocalContext.current

    IconButton(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 5.dp),
        onClick = {
            mContext.startActivity(Intent(mContext, CardInfoActivity::class.java))
        },
    )
    {
        Image(
            painter = card.image,
            contentDescription = card.name,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AddCardButton() {

    val mContext = LocalContext.current

    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Button(
            shape = CircleShape,
            modifier = Modifier
                .padding(15.dp)
                .size(60.dp),
            onClick = {
                mContext.startActivity(Intent(mContext, AddCardActivity::class.java))
            }
        )
        {
            Text(
                text = "+",
                fontSize = 32.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinClientTheme {
        Text("lol")
    }
}
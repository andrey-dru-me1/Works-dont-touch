package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.UpdateListener
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.R

class MainActivity : ComponentActivity(), UpdateListener {

    private val cards = mutableStateListOf<Card>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val list = remember { cards }
                    CardsGrid(list)

                    AddCardButton()
                }
            }
        }
        DataController.getInstance().addListener(this)
    }

    override fun update() {
        cards.clear()
        cards.addAll(DataController.getInstance().cards)
    }

    @Composable
    fun CardsGrid(cards: Collection<Card>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
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
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
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

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinClientTheme {
        Text("lol")
    }
}
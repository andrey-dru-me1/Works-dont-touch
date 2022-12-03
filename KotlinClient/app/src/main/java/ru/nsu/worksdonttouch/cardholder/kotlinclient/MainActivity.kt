package ru.nsu.worksdonttouch.cardholder.kotlinclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    CardViewer(listOf(
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        ),
                        Card(
                            "Android",
                            painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        Card(
                            "Something",
                            painterResource(id = R.drawable.ic_launcher_background)
                        )
                    ))
                }
            }
        }
    }
}

data class Card(val name: String, val image: Painter)

@Composable
fun CardViewer(cards: List<Card>) {
    LazyColumn(modifier =  Modifier.fillMaxSize()) {
        cards.map { item { Card(it) } }
    }
}

@Composable
fun Card(card: Card) {
    Image(
        painter = card.image,
        contentDescription = card.name,
        Modifier.fillMaxSize().padding(15.dp),
        contentScale = ContentScale.FillWidth
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinClientTheme {
        Text("lol")
    }
}
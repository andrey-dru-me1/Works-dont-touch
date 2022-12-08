package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.UpdateListener
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.Update
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.UpdateType

class MainActivity : ComponentActivity(), UpdateListener {

    private val cards: SnapshotStateList<Card> = mutableStateListOf()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataController.getInstance().putUserFromFile()
        if(DataController.getInstance().user == null) {
            val intent = Intent(this, AuthorisationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        DataController.getInstance().putCardsFromFile()
        cards.addAll(DataController.getInstance().cards)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ){}
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.hsl(245F, 0.3F, 0.2F)
                ) {
                    val list = remember { cards }
                    CardsGrid(list)

                    AddCardButton()
                }
            }
        }
        DataController.getInstance().addListener(this)
    }

    override fun update(update: Update) {
        if (update.type == UpdateType.ADD_CARD || update.type == UpdateType.REPLACE_CARD) {
            cards.clear()
            cards.addAll(DataController.getInstance().cards)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CardsGrid(cards: SnapshotStateList<Card>) {
        val refreshScope = rememberCoroutineScope()
        var refreshing by remember { mutableStateOf(false) }

        fun refresh() = refreshScope.launch {
            refreshing = true
            //TODO: Try connecting to a server and synchronize all the data
            delay(1500)
            refreshing = false
        }

        val state = rememberPullRefreshState(refreshing, ::refresh)
        val rotation = animateFloatAsState(state.progress * 120)

        Box(
            Modifier
                .fillMaxSize()
                .pullRefresh(state) ) {
            LazyVerticalGrid (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp),
                columns = GridCells.Fixed(2),
            ) {
                cards.map {  item { CardView(it) } }
            }

            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopCenter)
                    .pullRefreshIndicatorTransform(state)
                    .rotate(rotation.value),
                shape = RoundedCornerShape(10.dp),
                color = Color.DarkGray,
                elevation = if (state.progress > 0 || refreshing) 20.dp else 0.dp,
            ) {
                Box {
                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(25.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun CardView(card: Card) {

        val mContext = LocalContext.current

        IconButton(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.5.dp),
            onClick = {
                val intent = Intent(mContext, CardInfoActivity::class.java)
                intent.putExtra("card", card)
                mContext.startActivity(intent)
            },
        )
        {
            Box {
                Image(
                    bitmap = card.image.asImageBitmap(),
                    contentDescription = card.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio((86.0 / 54).toFloat())
                        .clip(RoundedCornerShape(10.dp))
                )
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .align(Alignment.BottomStart)
                ) {
                    Text(
                        text = card.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(6.dp, 0.dp)
                    )
                }
            }
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
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Gray) ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
            ) {
                item {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color = Color.Blue))
                }
            }
        }
    }
}
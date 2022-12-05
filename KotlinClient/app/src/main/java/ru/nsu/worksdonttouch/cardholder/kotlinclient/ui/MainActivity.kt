package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.UpdateListener
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.Card
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.Update
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.update.UpdateType
import ru.nsu.worksdonttouch.cardholder.kotlinclient.R
import java.io.File

class MainActivity : ComponentActivity(), UpdateListener {

    private val cards: SnapshotStateList<Card> = mutableStateListOf()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val list = remember { cards }
//                    val img: MutableState<Uri?> = remember { image }
//                    Image(painter = rememberImagePainter(data = img.value), contentDescription = "sth")
                    CardsGrid(list)

                    AddCardButton()
                }
            }
        }
        DataController.getInstance().addListener(this)
    }

    override fun update(update: Update) {
        if(update.type == UpdateType.ADD_CARD || update.type == UpdateType.REPLACE_CARD) {
            cards.clear()
            cards.addAll(DataController.getInstance().cards)
        }
    }

    @Composable
    fun CardsGrid(cards: SnapshotStateList<Card>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
        ) {
            cards.map { item { CardView(it) } }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun CardView(card: Card) {

        val mContext = LocalContext.current

        IconButton(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp, 5.dp),
            onClick = {
                mContext.startActivity(Intent(mContext, CardInfoActivity::class.java))
            },
        )
        {
            Column {
                Image(
                    painter = rememberImagePainter(data = card.image),
                    contentDescription = card.name,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                )
                Text(text = "Shop " + card.name)
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
        Text("lol")
    }
}
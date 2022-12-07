package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.User
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.ui.theme.KotlinClientTheme
import kotlin.random.Random

class AuthorisationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        var login by rememberSaveable { mutableStateOf("") }
                        var password by rememberSaveable { mutableStateOf("") }
                        TextField(
                            value = login,
                            label = { Text( "Login" ) },
                            onValueChange = { login = it },
                            singleLine = true
                        )
                        TextField(
                            value = password,
                            label = { Text( "Password" ) },
                            onValueChange = { password = it },
                            singleLine = true
                        )
                        Button(
                            onClick = { onSaveButtonClick(login, password) }
                        ) {
                            Text("Log in")
                        }
                    }
                }
            }
        }
    }

    private fun onSaveButtonClick(login: String, password: String) {
        DataController.getInstance().user = User(
            login = login,
            password = password,
            token = Random(System.currentTimeMillis()).toString()
        )
        finish()
    }

}
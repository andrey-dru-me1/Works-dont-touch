package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.User
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.ui.theme.KotlinClientTheme
import kotlin.random.Random

class RegistrationActivity : ComponentActivity() {
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
                            Text("Register")
                        }
                        ClickableText(
                            text = AnnotatedString("Sign in"),
                            style = TextStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
                            onClick = {
                                val intent = Intent(this@RegistrationActivity, AuthorisationActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        )
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
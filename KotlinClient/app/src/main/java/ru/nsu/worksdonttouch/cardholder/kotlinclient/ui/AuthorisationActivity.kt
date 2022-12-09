package ru.nsu.worksdonttouch.cardholder.kotlinclient.ui

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.DataController
import ru.nsu.worksdonttouch.cardholder.kotlinclient.data.objects.UserData
import ru.nsu.worksdonttouch.cardholder.kotlinclient.ui.theme.KotlinClientTheme

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
                        ClickableText(
                            text = AnnotatedString("Sign up"),
                            style = TextStyle(color = Color.Blue, textDecoration = TextDecoration.Underline),
                            onClick = {
                                val intent = Intent(this@AuthorisationActivity, RegistrationActivity::class.java)
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
        Thread {
            try {
                DataController.getInstance().loginUser(UserData(login, password))
            } catch (e: Throwable) {
                Log.d("EXCEPTION", e.message.toString())
            }

            if (!DataController.getInstance().isOffline) {
                runOnUiThread{
                    startActivity(Intent(this@AuthorisationActivity, MainActivity::class.java))
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Wrong login or password", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()

    }

}
package com.example.timeisapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.backend.client1
import com.example.timeisapp.backend.registerMe
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)

        val sendButton = findViewById<Button>(R.id.register_send_button)
        val cancelButton = findViewById<Button>(R.id.register_cancel_button)
        val usernameField = findViewById<EditText>(R.id.username_register)
        val passwordField = findViewById<EditText>(R.id.password_register)
        val emailField = findViewById<EditText>(R.id.email_register)
        val emailRegex = Regex("\\W{2,}|\\s")
        val emailRegex2 = Regex("[!\"£\$%&/()=?^§°ç:;,#~{}|\\[\\]<>]")
        val emailRegex3 = Regex("\\.{2,}|-{2,}|_{2,}|\\|/")
        val atRegex = Regex("@")
        var registered = false


        sendButton.setOnClickListener {
            if (atRegex.findAll(emailField.text).count() != 1 || emailRegex.containsMatchIn(
                    emailField.text
                ) || emailRegex2.containsMatchIn(emailField.text) || emailRegex3.containsMatchIn(
                    emailField.text
                ) || atRegex.findAll(emailField.text)
                    .count() > 1 || atRegex.findAll(emailField.text).count() < 1
            ) {
                Snackbar.make(it, "Invalid email", Snackbar.LENGTH_SHORT).show()
                emailField.text.clear()
            } else if (usernameField.text.length < 3) {
                Snackbar.make(it, "Username too short", Snackbar.LENGTH_SHORT).show()
                usernameField.text.clear()
            } else if (passwordField.text.length < 8) {
                Snackbar.make(it, "Password too short", Snackbar.LENGTH_SHORT).show()
                passwordField.text.clear()
            } else {

                runBlocking {
                    launch {
                        val (responseCode, responseData) = registerMe(
                            username = usernameField.text.toString(),
                            password = passwordField.text.toString(),
                            email = emailField.text.toString(),
                            client = client1
                        )
                        when (responseCode) {
                            200 -> setResult(RESULT_OK, Intent().putExtra("userData", responseData))
                            400, 401, 500 -> Snackbar.make(
                                it,
                                "There was a problem try again later",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        if (responseCode == 200) {
                            registered = true
                        }
                    }.join()
                }
                if (registered) {
                    finish()
                }
            }
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }


    }
}
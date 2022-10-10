package com.example.timeisapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.database.logMeIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)


        findViewById<Button>(R.id.register_cancel_button).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        findViewById<Button>(R.id.register_send_button).setOnClickListener {
            runBlocking {
                launch {
                    val (responseCode,responseData) = logMeIn(
                        findViewById<EditText>(R.id.username_field).text.toString(),
                        findViewById<EditText>(R.id.password_field).text.toString()
                    )
                    when (responseCode) {
                        200 -> setResult(RESULT_OK, Intent().putExtra("userData", responseData))
                        204 -> setResult(RESULT_OK, Intent().putExtra("alreadyLogged", responseCode))
                        400 -> setResult(RESULT_CANCELED, Intent().putExtra("error", responseCode))
                        401 -> setResult(RESULT_CANCELED, Intent().putExtra("error", responseCode))
                    }
                }.join()
            }
            finish()
        }

    }
}

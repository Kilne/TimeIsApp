package com.example.timeisapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.backend.client1
import com.example.timeisapp.backend.logMeIn
import com.google.android.material.snackbar.Snackbar
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
            var isResponseOk = false
            runBlocking {
                launch {
                    val (responseCode,responseData) = logMeIn(
                        findViewById<EditText>(R.id.username_field).text.toString(),
                        findViewById<EditText>(R.id.password_field).text.toString(),
                        client = client1
                    )
                    when (responseCode) {
                        200 -> setResult(RESULT_OK, Intent().putExtra("userData", responseData))
                        400,401,500 -> Snackbar.make(it, "There was a problem try again later", Snackbar.LENGTH_SHORT).show()
                    }
                    if (responseCode == 200 || responseCode == 204) {
                        isResponseOk = true
                    }
                }.join()
            }
            if (isResponseOk) {
                finish()
            }
        }

    }
}

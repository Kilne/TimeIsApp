package com.example.timeisapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.database.logMeIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class LogInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)


        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        findViewById<Button>(R.id.send_button).setOnClickListener {
            runBlocking {
                launch {
                    val responseData = logMeIn(
                        findViewById<EditText>(R.id.username_field).text.toString(),
                        findViewById<EditText>(R.id.password_field).text.toString()
                    )
                    if (responseData !is Int) {
                        val toBeSerialized = (responseData as JsonObject)
                        val dataSerializable: HashMap<String, Any> = HashMap()
                        toBeSerialized.forEach { K, V ->
                            dataSerializable[K] = V
                        }
                        setResult(RESULT_OK, Intent().putExtra("userData", dataSerializable))
                    } else {
                        setResult(RESULT_CANCELED, Intent().putExtra("error", responseData))
                    }
                }.join()
            }
            finish()
        }

    }
}

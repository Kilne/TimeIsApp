package com.example.timeisapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.timeisapp.database.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.Login_button)
        val registerButton = findViewById<Button>(R.id.Register_button)

        loginButton.setOnClickListener {
            Toast.makeText(this, "Before suspend", Toast.LENGTH_SHORT).show()
            runBlocking {
                launch {
                    Toast.makeText(this@MainActivity, "After suspend", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@MainActivity,
                        logMeIn("Luc1","password").toString() , Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            // @TODO: Listener
        }
    }

}
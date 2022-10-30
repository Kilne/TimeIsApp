package com.example.timeisapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.backend.MyClient
import com.example.timeisapp.backend.client1
import com.example.timeisapp.database.Database
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    override fun onDestroy() {
        super.onDestroy()
        client1?.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Buttons activation if not
        manipulateButtons(true)

        // Set the client
        client1 = MyClient().getClient()


        // User details
        var userDetails: Database?

        // Buttons
        val loginButton = findViewById<Button>(R.id.Login_button)
        val registerButton = findViewById<Button>(R.id.Register_button)

        // Contracts
        val homeContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_CANCELED) {
                    File(this.filesDir, "userDetails.txt").delete()
                    userDetails = null
                    manipulateButtons(true)
                    Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show()
                }

            }
        val logInContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                val data: Intent? = result.data
                if (result.resultCode == RESULT_OK) {

                    manipulateButtons(false)

                    if (data?.hasExtra("userData") == true) {

                        userDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                            Json.decodeFromString(
                                data.getSerializableExtra(
                                    "userData", Serializable::class.java
                                ) as String
                            ) as Database
                        } else {
                            Json.decodeFromString(data.getSerializableExtra("userData")
                                    as String) as Database
                        }

                        Toast.makeText(
                            this@MainActivity,
                            "Welcome ${userDetails!!.username}",
                            Toast.LENGTH_SHORT
                        ).show()

                        homeContract.launch(Intent(this@MainActivity, HomeActivity::class.java).putExtra("userData", userDetails))

                    }
                } else {
                    Toast.makeText(
                        this@MainActivity, "Login failed", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        val registerContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                val data: Intent? = result.data

                if (result.resultCode == RESULT_OK) {

                    manipulateButtons(false)

                    if (data?.hasExtra("userData") == true) {
                        userDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            data.getSerializableExtra(
                                "userData", Serializable::class.java
                            ) as Database
                        } else {
                            data.getSerializableExtra("userData") as Database
                        }

                        Toast.makeText(
                            this@MainActivity,
                            "Welcome ${userDetails!!.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                        homeContract.launch(Intent(this@MainActivity, HomeActivity::class.java).putExtra("userData", userDetails))
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity, "Registration aborted", Toast.LENGTH_SHORT
                    ).show()
                }

            }

        //Listeners
        loginButton.setOnClickListener {
            logInContract.launch(Intent(this@MainActivity, LogInActivity::class.java))
        }

        registerButton.setOnClickListener {
            registerContract.launch(Intent(this@MainActivity, RegisterActivity::class.java))
        }


    }

    private fun manipulateButtons(status: Boolean) {

        val loginButton = findViewById<Button>(R.id.Login_button)
        val registerButton = findViewById<Button>(R.id.Register_button)

        if (!status) {
            loginButton.isClickable = false
            registerButton.isClickable = false
            loginButton.alpha = 0.5f
            registerButton.alpha = 0.5f
        } else {
            loginButton.isClickable = true
            registerButton.isClickable = true
            loginButton.alpha = 1f
            registerButton.alpha = 1f
        }
    }

}
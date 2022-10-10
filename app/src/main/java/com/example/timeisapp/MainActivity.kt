package com.example.timeisapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.database.Database
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var userDetails: Database?
        //TMP
        for (file in filesDir.listFiles()!!) {
            Log.d("Files", "FileName:" + file.name)
            file.delete()
        }
       /* userDetails = if (this.fileList().isNotEmpty()){
             Json.decodeFromString<Database>(this.fileList()[0])
            // TODO c'è qualche altro file da bruciare
        } else {
            null
        }*/
        val loginButton = findViewById<Button>(R.id.Login_button)
        val registerButton = findViewById<Button>(R.id.Register_button)
        val logInContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                val data: Intent? = result.data
                if (result.resultCode == RESULT_OK) {

                    loginButton.isEnabled = false
                    registerButton.isEnabled = false

                    if (data?.hasExtra("userData") == true){

                        Toast.makeText(this@MainActivity,
                            "Login successful", Toast.LENGTH_SHORT).show()
                        userDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            data.getSerializableExtra("userData",
                                Serializable::class.java) as Database
                        } else {
                            data.getSerializableExtra("userData") as Database
                        }

                        File(this.filesDir, "userDetails.txt").writeText(
                            Json.encodeToString(userDetails))

                        Toast.makeText(this@MainActivity,
                            "Welcome ${userDetails!!.username}", Toast.LENGTH_SHORT).show()
                        // TODO Lanciare la nuova activity

                    }
                } else {
                    Toast.makeText(this@MainActivity, "Login Failed with code" +
                            " ${data?.getIntExtra("error", 0)}",
                        Toast.LENGTH_SHORT).show()
                }
            }
        val registerContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->

                val data: Intent? = result.data

                if (result.resultCode == RESULT_OK) {
                    if (data?.hasExtra("userData") == true){
                        userDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            data.getSerializableExtra("userData",
                                Serializable::class.java) as Database
                        } else {
                            data.getSerializableExtra("userData") as Database
                        }
                        File(this.filesDir, "userDetails.txt").writeText(
                            Json.encodeToString(userDetails))
                        Toast.makeText(this@MainActivity,
                            "Welcome ${userDetails!!.username}", Toast.LENGTH_SHORT).show()
                        // TODO Lanciare la nuova activity
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Registration Failed with code" +
                            " ${data?.getIntExtra("error", 0)}",
                        Toast.LENGTH_SHORT).show()
                }

            }


        loginButton.setOnClickListener {
            logInContract.launch(Intent(this@MainActivity, LogInActivity::class.java))
        }

        registerButton.setOnClickListener {
            registerContract.launch(Intent(this@MainActivity, RegisterActivity::class.java))
        }
    }

}
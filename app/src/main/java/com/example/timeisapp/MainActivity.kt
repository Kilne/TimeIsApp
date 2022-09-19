package com.example.timeisapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.timeisapp.database.DatabaseORM
import com.example.timeisapp.database.ProjectORM

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userData: DatabaseORM
        val userProject: ProjectORM
        val dataMap: MutableMap<String, Any> = mutableMapOf()
        val loginButton = findViewById<Button>(R.id.Login_button)
        val registerButton = findViewById<Button>(R.id.Register_button)
        val logInContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                Log.d("MainActivity", "onCreate: ${result.resultCode}")
                val data: Intent? = result.data
                if (result.resultCode == RESULT_OK) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data?.getSerializableExtra("userData", HashMap::class.java)!!.forEach {
                            dataMap[it.key as String] = it.value
                        }
                    } else {
                        (data?.getSerializableExtra("userData") as HashMap<*, *>).forEach {
                            dataMap[it.key as String] = it.value
                        }
                    }

                } else {
                    Toast.makeText(this@MainActivity, "Login Failed with code" +
                            " ${data?.getIntExtra("error", 0)}",
                        Toast.LENGTH_SHORT).show()
                }
            }


        loginButton.setOnClickListener {
            logInContract.launch(Intent(this@MainActivity, LogInActivity::class.java))
            Log.d("MainActivity", "hashmap: $dataMap")
        }

        registerButton.setOnClickListener {
            // @TODO: Listener
        }
    }

}
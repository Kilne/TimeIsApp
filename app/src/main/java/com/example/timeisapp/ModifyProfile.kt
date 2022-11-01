package com.example.timeisapp

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.example.timeisapp.backend.changeDetailsUser
import com.example.timeisapp.backend.client1
import com.example.timeisapp.database.Database
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ModifyProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_profile)

        // Store data from main activity
        val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("userData", Database::class.java) as Database
        } else {
            intent.getSerializableExtra("userData") as Database
        }

        // Change container
        val values = mutableMapOf<String, String>()
        values["name"] = ""
        values["email"] = ""
        values["password"] = ""
        val container = mutableMapOf<String, Boolean>()
        container["name"] = false
        container["email"] = false
        container["password"] = false
        // Buttons
        val changePass = findViewById<Button>(R.id.change_password_button)
        val changeEmail = findViewById<Button>(R.id.change_email_button)
        val changeName = findViewById<Button>(R.id.change_user_button)
        val applyButton = findViewById<Button>(R.id.apply_changes_button)
        val cancelButton = findViewById<Button>(R.id.discard_changes_button)
        val backHome = findViewById<Button>(R.id.back_to_home)

        // Container layout
        val applyCancelLayout = findViewById<ConstraintLayout>(R.id.apply_changes_layout)

        // text fields
        val name = findViewById<EditText>(R.id.edit_text_username)
        val email = findViewById<EditText>(R.id.edit_text_email)
        val password = findViewById<EditText>(R.id.edit_text_password)

        // user profile title
        val senseTheOrientation = this.resources.configuration.orientation
        if (senseTheOrientation == Configuration.ORIENTATION_PORTRAIT) {
            for (child in findViewById<ConstraintLayout>(R.id.profile_background_por).children.toSet()) {
                if (child is TextView) {
                    val string = "Welcome " + userData.username
                    child.text = string
                }
            }
        }else{
            for (child in findViewById<ConstraintLayout>(R.id.profile_background_land).children.toSet()) {
                if (child is TextView) {
                    val string = "Welcome " + userData.username
                    child.text = string
                }
            }
        }


        // Listeners
        changeName.setOnClickListener {
            if (name.text.toString().isNotEmpty()) {
                if (!textValidator(name.text.toString())) {
                    values["name"] = name.text.toString()
                    name.text.clear()
                    applyCancelLayout.visibility = ConstraintLayout.VISIBLE
                    applyButton.visibility = Button.VISIBLE
                    cancelButton.visibility = Button.VISIBLE
                }
            } else {
                Snackbar.make(it, "Please enter a new name", Snackbar.LENGTH_SHORT).show()
            }
        }
        changeEmail.setOnClickListener {
            if (email.text.toString().isNotEmpty()) {
                if (!emailValidator(email.text.toString())) {
                    values["email"] = email.text.toString()
                    email.text.clear()
                    applyCancelLayout.visibility = ConstraintLayout.VISIBLE
                    applyButton.visibility = Button.VISIBLE
                    cancelButton.visibility = Button.VISIBLE
                }
            } else {
                Snackbar.make(it, "Please enter a new email", Snackbar.LENGTH_SHORT).show()
            }
        }
        changePass.setOnClickListener {
            if (password.text.toString().isNotEmpty()) {

                values["password"] = password.text.toString()
                password.text.clear()
                applyCancelLayout.visibility = ConstraintLayout.VISIBLE
                applyButton.visibility = Button.VISIBLE
                cancelButton.visibility = Button.VISIBLE

            } else {
                Snackbar.make(it, "Please enter a new password", Snackbar.LENGTH_SHORT).show()
            }
        }
        backHome.setOnClickListener {
            setResult(RESULT_OK, intent.putExtra("userData", userData))
            finish()
        }
        cancelButton.setOnClickListener {
            applyCancelLayout.visibility = ConstraintLayout.GONE
            applyButton.visibility = Button.GONE
            cancelButton.visibility = Button.GONE
            values["name"] = ""
            values["email"] = ""
            values["password"] = ""
        }
        applyButton.setOnClickListener {
            sendModifyRequest(values, container, userData.session_id)
            var countedFailures = 0
            var valuesSent = 0
            for ((_, v) in values) {
                if (v.isNotEmpty()) {
                    valuesSent++
                }
            }
            for ((_, v) in container) {
                if (!v) {
                    countedFailures++
                }
            }

            if (countedFailures == 0) {
                Snackbar.make(it, "All changes applied", Snackbar.LENGTH_SHORT).show()
                if (values["name"]!!.isNotEmpty()) {
                    userData.username = values["name"]!!
                }
                applyCancelLayout.visibility = ConstraintLayout.GONE
                applyButton.visibility = Button.GONE
                cancelButton.visibility = Button.GONE
                values["name"] = ""
                values["email"] = ""
                values["password"] = ""
            }
            if (countedFailures == valuesSent) {
                Snackbar.make(it, "No changes applied", Snackbar.LENGTH_SHORT).show()
                applyCancelLayout.visibility = ConstraintLayout.GONE
                applyButton.visibility = Button.GONE
                cancelButton.visibility = Button.GONE
                values["name"] = ""
                values["email"] = ""
                values["password"] = ""
            }
            if (countedFailures != 0 && countedFailures != valuesSent) {
                Snackbar.make(it, "Some changes applied", Snackbar.LENGTH_SHORT).show()
                for ((k, v) in container) {
                    if (v) {
                        Snackbar.make(it, "Change $k applied", Snackbar.LENGTH_SHORT).show()
                    }
                }
                if (values["name"]!!.isNotEmpty()) {
                    userData.username = values["name"]!!
                }
                applyCancelLayout.visibility = ConstraintLayout.GONE
                applyButton.visibility = Button.GONE
                cancelButton.visibility = Button.GONE
                values["name"] = ""
                values["email"] = ""
                values["password"] = ""
            }
        }

    }

    private fun textValidator(str: String): Boolean {
        val regex = Regex("[^\\sa-zA-Z0-9_]*")
        return regex.matches(str)

    }

    private fun emailValidator(str: String): Boolean {
        val emailRegex = Regex("\\W{2,}|\\s")
        val emailRegex2 = Regex("[!\"£\$%&/()=?^§°ç:;,#~{}|\\[\\]<>]")
        val emailRegex3 = Regex("\\.{2,}|-{2,}|_{2,}|\\|/")
        val atRegex = Regex("@")

        if (emailRegex.matches(str) || emailRegex2.matches(str) || emailRegex3.matches(str) || !atRegex.containsMatchIn(
                str
            )
        ) {
            return false
        }

        if (atRegex.findAll(str).count() > 1) {
            return false
        }

        return true
    }

    private fun sendModifyRequest(
        values: MutableMap<String, String>,
        container: MutableMap<String, Boolean>,
        session_id: String
    ) {
        var answer = Pair(false, mapOf<String, Boolean>())
        runBlocking {
            launch {
                answer = changeDetailsUser(
                    session_id, values["name"]!!, values["email"]!!, values["password"]!!,
                    client1
                )
            }.join()
        }
        if (answer.first) {
            container["name"] = answer.second["username"]!!
            container["email"] = answer.second["email"]!!
            container["password"] = answer.second["password"]!!
        } else {
            container["name"] = false
            container["email"] = false
            container["password"] = false
        }
    }
}

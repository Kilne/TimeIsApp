package com.example.timeisapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
import androidx.core.view.children
import com.example.timeisapp.backend.*
import com.example.timeisapp.database.Database
import com.example.timeisapp.database.Project
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        // Get the buttons
        val addButton = findViewById<Button>(R.id.add_project_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val confirmButton = findViewById<Button>(R.id.confirm_delete)
        val cancelButton = findViewById<Button>(R.id.cancel_button)
        val logOutButton = findViewById<Button>(R.id.logout_button)
        val profileButton = findViewById<Button>(R.id.profile_button)

        // Get the values from intent
        val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("userData", Database::class.java)
        } else {
            intent.getSerializableExtra("userData")
        } as Database

        // Project creation/modify contract
        val modifyContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val data: Project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.data?.getSerializableExtra("projectData", Project::class.java) as Project
                    } else {
                        it.data?.getSerializableExtra("projectData") as Project
                    }
                    for (project in userData.user_projects) {
                        if (project.p_id == data.p_id) {
                            project.p_name = data.p_name
                            project.description = data.description
                            project.due_date = data.due_date
                            project.goal = data.goal
                            project.single_step_value = data.single_step_value
                            project.perc_compl = data.perc_compl
                            project.num_steps_completed = data.num_steps_completed
                            project.obj = data.obj
                            project.time_left = data.time_left
                            project.num_steps_total = data.num_steps_total
                        }
                    }
                    for (child in findViewById<LinearLayout>(R.id.CardZone).children.toSet()) {
                        if (child.findViewById<CheckBox>(R.id.checkBox).tag == data.p_id) {
                            child.findViewById<ProgressBar>(R.id.PbarHorizontal).progress =
                                data.perc_compl.toInt()
                            child.findViewById<TextView>(R.id.CardText).text = data.p_name
                        }
                    }
                    Toast.makeText(this, "Project modified", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Project not modified", Toast.LENGTH_SHORT).show()
                }
            }
        val createContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

                if (result.resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        userData.user_projects.add(
                            result.data?.getSerializableExtra(
                                "newProject",
                                Project::class.java
                            ) as Project
                        )
                    } else {
                        userData.user_projects.add(result.data?.getSerializableExtra("newProject") as Project)
                    }

                    if (!addNewProject(userData.user_projects.last(), userData.session_id)) {
                        userData.user_projects.removeLast()
                    }
                    addCards(userData.user_projects.last(), modifyContract, userData.session_id)

                } else {
                    Toast.makeText(this, "Project creation cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        val profileContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.data?.getSerializableExtra("userData", Database::class.java) as Database
                    } else {
                        it.data?.getSerializableExtra("userData") as Database
                    }

                    if (data.username != userData.username) {
                        userData.username = data.username
                    }
                }
            }


        // Check if projects need to be placed
        if (userData.user_projects.isEmpty()) {
            addEmptyCard()
        } else {
            for (projects in userData.user_projects) {
                addCards(projects, modifyContract, userData.session_id)
            }
        }


        // button listeners
        logOutButton.setOnClickListener {
            var hasLoggedOut = false
            runBlocking {
                launch {
                    if (logOut(client1)) {
                        setResult(RESULT_CANCELED)
                        hasLoggedOut = true
                    }
                }.join()
            }
            if (hasLoggedOut) {
                finish()
            } else {
                Snackbar.make(it, "There was a problem try again later", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        deleteButton.setOnClickListener {
            it.isClickable = false
            it.alpha = 0.5f
            addButton.isClickable = false
            addButton.alpha = 0.5f
            buttonManipulation(true)
            showChecks(true)
        }
        cancelButton.setOnClickListener {
            buttonManipulation(false)
            addButton.isClickable = true
            addButton.alpha = 1f
            deleteButton.isClickable = true
            deleteButton.alpha = 1f
            showChecks(false)
        }
        confirmButton.setOnClickListener {
            if (removeCards(userData, modifyContract)) {
                showChecks(false)
                buttonManipulation(false)
                addButton.isClickable = true
                addButton.alpha = 1f
                deleteButton.isClickable = true
                deleteButton.alpha = 1f
            } else {
                showChecks(true)
                buttonManipulation(true)
                addButton.isClickable = false
                addButton.alpha = 0.5f
                deleteButton.isClickable = false
                deleteButton.alpha = 0.5f
            }
        }
        addButton.setOnClickListener {
            createContract.launch(Intent(this@HomeActivity, CreateProjectActivity::class.java))
        }

        profileButton.setOnClickListener {
            profileContract.launch(
                Intent(
                    this@HomeActivity,
                    ModifyProfile::class.java
                ).putExtra("userData", userData)
            )
        }
    }

    // Methods of action buttons
    private fun addCards(
        project: Project,
        modifyContract: ActivityResultLauncher<Intent>,
        session_id: String
    ) {
        val cardZone = findViewById<LinearLayout>(R.id.CardZone)
        val card = LayoutInflater.from(this).inflate(
            R.layout.card_element,
            cardZone,
            false
        )

        // Set the card values
        card.findViewById<TextView>(R.id.CardText).text = project.p_name
        card.findViewById<ProgressBar>(R.id.PbarHorizontal).progress = project.perc_compl.toInt()
        card.findViewById<CheckBox>(R.id.checkBox).tag = project.p_id

        //set listener
        card.findViewById<Button>(R.id.open_button).setOnClickListener {
            val intent = Intent(
                this@HomeActivity,
                ModifyProject::class.java
            )
            intent.putExtra("projectData", project)
            intent.putExtra("session_id", session_id)
            modifyContract.launch(intent)
        }
        cardZone.addView(card)

    }

    private fun removeCards(
        database: Database,
        modifyContract: ActivityResultLauncher<Intent>
    ): Boolean {
        val cardZone = findViewById<LinearLayout>(R.id.CardZone)
        val checkedCards = mutableListOf<String>()
        for (card in cardZone.children.toList()) {
            if (card.findViewById<CheckBox>(R.id.checkBox).isChecked) {
                checkedCards.add(card.findViewById<CheckBox>(R.id.checkBox).tag as String)
            }
        }

        var result = Pair(false, mutableMapOf<String, Boolean>())
        runBlocking {
            launch {
                result = deleteProjects(checkedCards, database.session_id, client1)
            }.join()
        }

        if (result.first) {
            if (result.second.isNotEmpty()) {
                Snackbar.make(
                    findViewById(R.id.background),
                    "Some projects couldn't be deleted", Snackbar.LENGTH_SHORT
                )
                    .show()
            } else {
                Snackbar.make(
                    findViewById(R.id.background),
                    "Projects deleted", Snackbar.LENGTH_SHORT
                )
                    .show()
            }
            database.user_projects.removeAll(database.user_projects.toSet())
            database.user_projects.addAll(retrieveProjects(database.session_id).second)
            for (card in cardZone.children.toList()) {
                cardZone.removeView(card)
            }
            if (database.user_projects.isEmpty()) {
                addEmptyCard()
            } else {
                for (projects in database.user_projects) {
                    addCards(projects, modifyContract, database.session_id)
                }
            }
            return true
        } else {
            Snackbar.make(
                findViewById(R.id.background),
                "There was a problem try again later", Snackbar.LENGTH_SHORT
            )
                .show()
            return false
        }
    }

    private fun addEmptyCard() {
        val cardZone = findViewById<LinearLayout>(R.id.CardZone)
        val card = LayoutInflater.from(this).inflate(
            R.layout.card_element,
            cardZone,
            false
        ) as ConstraintLayout

        (card.layoutParams as ConstraintLayout.LayoutParams).topToTop = PARENT_ID
        cardZone.addView(card)

    }

    private fun buttonManipulation(visibile: Boolean) {
        val confirm = findViewById<Button>(R.id.confirm_delete)
        val cancel = findViewById<Button>(R.id.cancel_button)

        if (visibile) {
            confirm.visibility = View.VISIBLE
            cancel.visibility = View.VISIBLE
            confirm.isClickable = true
            cancel.isClickable = true
            confirm.alpha = 1f
            cancel.alpha = 1f
        } else {
            confirm.visibility = View.GONE
            cancel.visibility = View.GONE
            confirm.isClickable = false
            cancel.isClickable = false
            confirm.alpha = 0.5f
            cancel.alpha = 0.5f
        }
    }

    private fun showChecks(status: Boolean) {
        if (status) {
            for (child in findViewById<LinearLayout>(R.id.CardZone).children.toList()) {
                child.findViewById<CheckBox>(R.id.checkBox).visibility = View.VISIBLE
            }
        } else {
            for (child in findViewById<LinearLayout>(R.id.CardZone).children.toList()) {
                child.findViewById<CheckBox>(R.id.checkBox).visibility = View.INVISIBLE
            }
        }
    }

    private fun retrieveProjects(session_id: String): Pair<Boolean, ArrayList<Project>> {

        var result = Pair(false, arrayListOf<Project>())
        runBlocking {
            launch {
                result = getProjects(session_id, client1)
            }.join()
        }

        return when (result.first) {
            false -> {
                Snackbar.make(
                    findViewById(R.id.background),
                    "There was a problem try again later", Snackbar.LENGTH_SHORT
                )
                    .show()
                Pair(false, arrayListOf())
            }
            else -> {
                result
            }
        }
    }

    private fun addNewProject(project: Project, session_id: String): Boolean {

        var result = false

        runBlocking {
            launch {
                result = addAProject(session_id, project, client1)
            }.join()
        }

        return if (result) {
            Snackbar.make(
                findViewById(R.id.background),
                "Project added", Snackbar.LENGTH_SHORT
            ).show()
            true
        } else {
            Snackbar.make(
                findViewById(R.id.background),
                "There was a problem try again later", Snackbar.LENGTH_SHORT
            ).show()
            false
        }
    }
}

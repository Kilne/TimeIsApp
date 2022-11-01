package com.example.timeisapp

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.example.timeisapp.backend.client1
import com.example.timeisapp.backend.modifyExisting
import com.example.timeisapp.database.Project
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime

class ModifyProject : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_details)

        val sessionId = intent.getStringExtra("session_id") as String
        val project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("projectData", Project::class.java) as Project
        } else {
            intent.getSerializableExtra("projectData") as Project
        }

        // Relevant buttons
        val modifyButton = findViewById<Button>(R.id.modify_project_button)
        val confirmbutton = findViewById<Button>(R.id.confirm_changes_button)
        val cancelButton = findViewById<Button>(R.id.cancel_changes_button)
        val backToHome = findViewById<Button>(R.id.back_to_projects)

        // Relevant layout and buttons
        val buttonContainer = findViewById<ConstraintLayout>(R.id.step_modifiers)
        val addStep = findViewById<Button>(R.id.button_add_step)
        val removeStep = findViewById<Button>(R.id.remove_step)

        // Place Holders
        var projectName = TextView(this)
        for (child in findViewById<ConstraintLayout>(R.id.project_background).children.toSet()) {
            if (child is TextView && child.id == R.id.project_name || child.id == R.id.project_name2) {
                projectName = findViewById(child.id)
            }
        }

        val fieldCollection = mutableListOf<View>()

        for (child in findViewById<LinearLayout>(R.id.field_linear_layout).children.toSet()) {
            if (child is EditText || child is ProgressBar) {
                fieldCollection.add(child)
            }
        }

        // Populate the fields
        projectName.text = project.p_name
        for (child in fieldCollection) {
            when (child) {
                is EditText -> {
                    when (child.hint) {
                        getString(R.string.p_name) -> child.setText(
                            project.p_name,
                            TextView.BufferType.EDITABLE
                        )
                        getString(R.string.p_description) -> child.setText(project.description)
                        getString(R.string.p_objective_text) -> child.setText(project.obj)
                        getString(R.string.p_objective_quantity) -> child.setText(project.goal.toString())
                        getString(R.string.p_step_number_tot) -> child.setText(project.num_steps_total.toString())
                        getString(R.string.p_step_completed) -> child.setText(project.num_steps_completed.toString())
                        getString(R.string.p_step_value) -> child.setText(project.single_step_value.toString())
                        getString(R.string.p_end_date) -> {
                            val splitted = project.due_date.split("-")
                            val effective =
                                "${splitted[0]}/${splitted[1]}/${splitted[2].split("T")[0]}"
                            child.setText(effective)
                        }
                        getString(R.string.p_time_left) -> child.setText(project.time_left.toString())
                    }
                }
                is ProgressBar -> {
                    child.progress = project.perc_compl.toInt()
                }
            }
        }

        // Listeners
        modifyButton.setOnClickListener {
            fieldActivation(fieldCollection, true)
            it.visibility = View.GONE
            confirmbutton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            addStep.visibility = View.VISIBLE
            removeStep.visibility = View.VISIBLE
            buttonContainer.visibility = View.VISIBLE
            backToHome.isClickable = false
            backToHome.isEnabled = false
            backToHome.alpha = 0.5f

        }
        cancelButton.setOnClickListener {
            fieldActivation(fieldCollection, false)
            modifyButton.visibility = View.VISIBLE
            confirmbutton.visibility = View.GONE
            it.visibility = View.GONE
            addStep.visibility = View.GONE
            removeStep.visibility = View.GONE
            buttonContainer.visibility = View.GONE
            backToHome.isClickable = true
            backToHome.isEnabled = true
            backToHome.alpha = 1f
        }
        backToHome.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        confirmbutton.setOnClickListener {

            var preliminaryChecks = true
            for (view in fieldCollection) {
                when (view) {
                    is EditText -> {
                        when (view.hint) {
                            getString(R.string.p_name),
                            getString(R.string.p_objective_text),
                            getString(R.string.p_description) -> {
                                preliminaryChecks = !textValidator(view.text.toString())
                            }
                            getString(R.string.p_objective_quantity) -> {
                                view.text.toString().toIntOrNull()?.let {
                                    if (it <= 0) {
                                        preliminaryChecks = false
                                    }
                                }
                            }
                            getString(R.string.p_step_number_tot) -> {
                                view.text.toString().toIntOrNull()?.let {
                                    if (it <= 0) {
                                        preliminaryChecks = false
                                    }
                                }
                            }
                            getString(R.string.p_end_date) -> {
                                view.text.toString().let {
                                    if (it.isEmpty() || !dateValidator(it)) {
                                        preliminaryChecks = false
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (preliminaryChecks) {

                val dateSlpitter = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_end_date)
                } as EditText
                        ).text.toString().split("/")
                val date = LocalDateTime.of(
                    dateSlpitter[0].toInt(),
                    dateSlpitter[1].toInt(),
                    dateSlpitter[2].toInt(),
                    0,
                    0
                )

                val goal = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_objective_quantity)
                } as EditText).text.toString().toFloat()
                val steps = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_step_number_tot)
                } as EditText).text.toString().toInt()

                val name = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_name)
                } as EditText).text.toString()
                val obj = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_objective_text)
                } as EditText).text.toString()
                val desc = (fieldCollection.find {
                    it is EditText && it.hint == getString(R.string.p_description)
                } as EditText).text.toString()

                val timeLeft =
                    if (project.time_left == (Duration.between(LocalDateTime.now(), date)).toDays()
                            .toInt()
                    ) {
                        project.time_left
                    } else {
                        (Duration.between(LocalDateTime.now(), date)).toDays().toInt()
                    }
                var stepVal = (
                        fieldCollection.find {
                            it is EditText && it.hint == getString(R.string.p_step_value)
                        } as EditText).text.toString().toFloat()

                // Modifications to the project
                if (goal != project.goal ||
                    date.toString() != project.due_date ||
                    steps != project.num_steps_total
                ) {

                    stepVal = if (steps > timeLeft) {
                        (goal / steps.toFloat())
                    } else {
                        (goal / timeLeft.toFloat())
                    }
                }


                val newProject = Project(
                    p_id = project.p_id,
                    p_name = name,
                    obj = obj,
                    description = desc,
                    goal = goal,
                    num_steps_total = steps,
                    num_steps_completed = fieldCollection.find {
                        it is EditText && it.hint == getString(R.string.p_step_completed)
                    }?.let {
                        (it as EditText).text.toString().toInt()
                    } ?: project.num_steps_completed,
                    due_date = date.toString(),
                    single_step_value = stepVal,
                    perc_compl = fieldCollection.find {
                        it is ProgressBar
                    }?.let {
                        (it as ProgressBar).progress.toString().toFloat()
                    } ?: project.perc_compl,
                    time_left = timeLeft,
                )

                var modified = true
                runBlocking {
                    launch {
                        modified = modifyExisting(sessionId, newProject, client1)
                    }.join()
                }
                if (modified) {
                    setResult(RESULT_OK, intent.putExtra("projectData", newProject))
                    finish()
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_CANCELED)
                    finish()
                }
            } else {
                Snackbar.make(
                    this.findViewById(R.id.project_background),
                    "Please fill all fields correctly",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        addStep.setOnClickListener {
            val currentStepCompletedVal = fieldCollection.find {
                it is EditText && it.hint == getString(R.string.p_step_completed)
            }?.let {
                (it as EditText).text.toString().toInt()
            } ?: project.num_steps_completed
            val currentStepTotalVal = fieldCollection.find {
                it is EditText && it.hint == getString(R.string.p_step_number_tot)
            }?.let {
                (it as EditText).text.toString().toInt()
            } ?: project.num_steps_total

            val correctedStepCompleted = currentStepCompletedVal + 1
            if (correctedStepCompleted > currentStepTotalVal) {
                Snackbar.make(
                    this.findViewById(R.id.project_background),
                    "You can't add more steps than the total",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                currentStepCompletedVal.let {
                    fieldCollection.find {
                        it is EditText && it.hint == getString(R.string.p_step_completed)
                    }?.let {
                        (it as TextView).text = it.text.toString().toInt().plus(1).toString()
                    }
                }
                fieldCollection.find {
                    it is ProgressBar
                }?.let {
                    (it as ProgressBar).progress =
                        (correctedStepCompleted.toFloat() / currentStepTotalVal.toFloat() * 100).toInt()
                }
            }
        }
        removeStep.setOnClickListener {
            val currentStepCompletedVal = fieldCollection.find {
                it is EditText && it.hint == getString(R.string.p_step_completed)
            }?.let {
                (it as EditText).text.toString().toInt()
            } ?: project.num_steps_completed
            val currentStepTotalVal = fieldCollection.find {
                it is EditText && it.hint == getString(R.string.p_step_number_tot)
            }?.let {
                (it as EditText).text.toString().toInt()
            } ?: project.num_steps_total

            val correctedStepCompleted = currentStepCompletedVal - 1
            if (correctedStepCompleted <= 0) {
                Snackbar.make(
                    this.findViewById(R.id.project_background),
                    "You can't remove more steps than the minimum(0)",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                currentStepCompletedVal.let {
                    fieldCollection.find {
                        it is EditText && it.hint == getString(R.string.p_step_completed)
                    }?.let {
                        (it as TextView).text = it.text.toString().toInt().minus(1).toString()
                    }
                }
                fieldCollection.find {
                    it is ProgressBar
                }?.let {
                    (it as ProgressBar).progress =
                        (correctedStepCompleted.toFloat() / currentStepTotalVal.toFloat() * 100).toInt()
                }
            }
        }
    }

    private fun textValidator(str: String): Boolean {
        val regex = Regex("[^\\sa-zA-Z0-9_]*")
        return regex.matches(str)

    }

    private fun dateValidator(str: String): Boolean {
        val regex = Regex("[^\\d/]*")
        if (regex.matches(str)) {
            return false
        } else {
            val splitted = str.split("/")
            if (splitted.size > 3 || splitted.size < 3) {
                return false
            }
            if (splitted[0].length != 4 || splitted[1].toInt() > 12 || splitted[1].toInt() < 1 || splitted[2].toInt() > 31 || splitted[2].toInt() < 1) {
                return false
            }
            if (LocalDateTime.parse(splitted[0] + "-" + splitted[1] + "-" + splitted[2] + "T00:00:00")
                    .isBefore(
                        LocalDateTime.now()
                    )
            ) {
                return false
            }
            return true
        }
    }

    private fun fieldActivation(listOfViews: MutableList<View>, status: Boolean) {

        for (field in listOfViews) {
            when (field) {
                is EditText -> {
                    when (field.hint) {
                        getString(R.string.p_name),
                        getString(R.string.p_objective_text),
                        getString(R.string.p_description),
                        getString(R.string.p_objective_quantity),
                        getString(R.string.p_end_date),
                        getString(R.string.p_step_number_tot) -> field.isEnabled = status
                    }
                }
            }
        }
    }
}
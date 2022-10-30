package com.example.timeisapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.example.timeisapp.database.Project
import com.google.android.material.snackbar.Snackbar
import java.time.Duration
import java.time.LocalDateTime

class CreateProjectActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_form)

        //Buttons
        val confirm = findViewById<TextView>(R.id.confirm_project_creation_button)
        val cancel = findViewById<TextView>(R.id.cancel_project_creation_button)

        //Field Collections
        val fields = findViewById<ConstraintLayout>(R.id.form_container).children.toList()


        //Listeners
        findViewById<TextView>(R.id.project_name_field).setOnClickListener {
            val myView = it as TextView
            if (myView.text.isNotEmpty()){
                if (textValidator(myView.text.toString())) {
                    Snackbar.make(
                        it,
                        "Project name contains forbidden chars",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    myView.background = AppCompatResources.getDrawable(this, R.color.warn)
                }
            } else {
                myView.background = AppCompatResources.getDrawable(this, R.color.teal_200)
            }
        }

        findViewById<TextView>(R.id.project_decription).setOnClickListener {
            val myView = it as TextView
            if (myView.text.isNotEmpty()){
                if (textValidator(myView.text.toString())) {
                    Snackbar.make(
                        it,
                        "Project description contains forbidden chars",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    myView.background = AppCompatResources.getDrawable(this, R.color.warn)
                }
            } else {
                myView.background = AppCompatResources.getDrawable(this, R.color.teal_200)
            }
        }

        findViewById<TextView>(R.id.objective_text).setOnClickListener {
            val myView = it as TextView
            if (myView.text.isNotEmpty()){
                if (textValidator(myView.text.toString())) {
                    Snackbar.make(
                        it,
                        "Project objective contains forbidden chars",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    myView.background = AppCompatResources.getDrawable(this, R.color.warn)
                }
            } else {
                myView.background = AppCompatResources.getDrawable(this, R.color.teal_200)
            }
        }

        findViewById<TextView>(R.id.objective_number).setOnClickListener {
            val myView = it as TextView
            if (myView.text.isNotEmpty()){
                if(myView.text.toString().toInt() <0){
                    Snackbar.make(
                        it,
                        "Objective number must be positive",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    myView.background = AppCompatResources.getDrawable(this, R.color.warn)
                }
            }else{
                myView.background = AppCompatResources.getDrawable(this, R.color.teal_200)
            }
        }

        findViewById<TextView>(R.id.objective_date).setOnClickListener {
            val myView = it as TextView
            if (myView.text.isNotEmpty()){
                if (!dateValidator(myView.text.toString())) {
                    Snackbar.make(
                        it,
                        "Date format must be yyyy/mm/dd",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    myView.background = AppCompatResources.getDrawable(this, R.color.warn)
                }
            } else {
                myView.background = AppCompatResources.getDrawable(this, R.color.teal_200)
            }

        }

        cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        confirm.setOnClickListener {
            var valid = true
            for (field in fields){
                if (field is TextView){
                    if (field.text.isEmpty()){
                        valid = false
                        field.background = AppCompatResources.getDrawable(this, R.color.warn)
                    }
                }
            }
            if (valid){
                val date = findViewById<TextView>(R.id.objective_date).text.toString().split("/")
                val project : Project = makeAproject(
                    findViewById<TextView>(R.id.project_name_field).text.toString(),
                    findViewById<TextView>(R.id.project_decription).text.toString(),
                    findViewById<TextView>(R.id.objective_text).text.toString(),
                    findViewById<TextView>(R.id.objective_number).text.toString().toInt(),
                    LocalDateTime.parse("${date[0]}-${date[1]}-${date[2]}T00:00:00"))
                Log.d("Project", project.toString())
                setResult(RESULT_OK, Intent().putExtra("newProject", project))
                finish()
            }else{
                Snackbar.make(
                    findViewById(R.id.form_layout),
                    "The form is not complete or contains errors",Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    private fun textValidator(str: String) :Boolean {
        val regex = Regex("[^\\sa-zA-Z0-9_]*")
        return regex.matches(str)

    }

    private fun dateValidator(str: String) :Boolean {
        val regex = Regex("[^\\d/]*")
        if (regex.matches(str)){
            return false
        } else {
            val splitted = str.split("/")
            if (splitted.size>3 || splitted.size <3){
                return false
            }
            if(splitted[0].length !=4 || splitted[1].length !=2 || splitted[2].length !=2){
                return false
            }
            if(LocalDateTime.parse(splitted[0]+"-"+splitted[1]+"-"+splitted[2]+"T00:00:00").isBefore(LocalDateTime.now())){
                return false
            }
            return true
        }
    }

    private fun makeAproject(name: String, des:String, objstr:String, objNum: Int, date: LocalDateTime) : Project{

        val timeLeft = Duration.between(LocalDateTime.now(), date).toDays().toInt()
        val percCompl = 0f
        val stepsVal = (objNum/timeLeft).toFloat()
        val stepsCompl = 0
        val pID = (0..1).random().toString(16)

        return Project(
            name,
            objstr,
            objNum.toFloat(),
            timeLeft,
            stepsCompl,
            stepsVal,
            date.toString(),
            timeLeft,
            percCompl,
            des,
            pID
        )
    }
}

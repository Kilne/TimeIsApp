package com.example.timeisapp

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.example.timeisapp.database.Project
import java.time.LocalDateTime

class ModifyProject: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_details)

        val session_id = intent.getStringExtra("session_id")
        val project = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
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
        val appStep= findViewById<Button>(R.id.button_add_step)
        val removeStep= findViewById<Button>(R.id.remove_step)

        // Place Holders
        val projectName = findViewById<TextView>(R.id.project_name)
        val fieldCollection = mutableListOf<View>()

        for ( child in findViewById<LinearLayout>(R.id.field_linear_layout).children.toSet()){
            if (child !is ConstraintLayout){
                fieldCollection.add(child)
            }
        }

        // Listeners
        modifyButton.setOnClickListener {
            for (field in fieldCollection){
                field.isEnabled = true
            }
            it.visibility = View.GONE
            confirmbutton.visibility = View.VISIBLE
            cancelButton.visibility = View.VISIBLE
            appStep.visibility = View.VISIBLE
            removeStep.visibility = View.VISIBLE
            buttonContainer.visibility = View.VISIBLE
            backToHome.isClickable = false
            backToHome.isEnabled = false
            backToHome.alpha = 0.5f

        }
        cancelButton.setOnClickListener {
            for (field in fieldCollection){
                field.isEnabled = false
            }
            modifyButton.visibility = View.VISIBLE
            confirmbutton.visibility = View.GONE
            it.visibility = View.GONE
            appStep.visibility = View.GONE
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
        confirmbutton.setOnClickListener{

            var preliminaryChecks = false
            for( view in fieldCollection){
                when(view.tag){
                    "p_name","p_obj","p_desc"-> {
                        preliminaryChecks = textValidator((view as TextView).text.toString())
                    }
                    "p_goal"->{
                        // TODO FINIRE I CHECK
                    }
                }

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
            if(LocalDateTime.parse(splitted[0]+"-"+splitted[1]+"-"+splitted[2]+"T00:00:00").isBefore(
                    LocalDateTime.now())){
                return false
            }
            return true
        }
    }

    private fun fieldActivation(listOfViews: MutableList<View>, status: Boolean){

        for (field in listOfViews){
            when(field.tag){
                "p_name","p_obj","p_desc","p_goal","p_steps","p_date" -> field.isClickable = status
            }
        }
    }
}
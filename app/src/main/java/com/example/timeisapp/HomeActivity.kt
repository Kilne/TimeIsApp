package com.example.timeisapp

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import com.example.timeisapp.database.Database
import com.example.timeisapp.database.Project
import com.example.timeisapp.database.ProjectORM
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.home_layout)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){

        // TODO: Eventualmente costruire un scroll orizzontale e allineare le schede dal bordo sinistro alla scheda destra e bordo destro  per l'ultimo figlo
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        // Get the values from intent
        val userData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getSerializableExtra("userData",Database::class.java)
        } else {
            intent.getSerializableExtra("userData")
        } as Database

        // Check if projects need to be placed
        if(userData.user_projects.isEmpty()){
            addEmptyCard()
        }else{
            for (projects in userData.user_projects) {
                addCard(projects)
            }
        }

        // Get the buttons and collect all hidden checks
        // TODO: Button profile e LOGOUT con metodi
        // TODO: MANCA IL BOTTONE PER CREARE!!!!!!
        val selectButton = findViewById<Button>(R.id.select_button)
        val cancelButton = findViewById<Button>(R.id.cancel_selection_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val cardZone= findViewById<LinearLayout>(R.id.CardZone)
        val checkCollection: MutableMap<String,CheckBox> = mutableMapOf()

        if (cardZone.children.count()>0 &&
                cardZone.children.first().tag != "empty_card") {
            for (card in cardZone.children){
                val checkBox = card.findViewById<CheckBox>(R.id.checkBox)
                checkCollection[checkBox.tag.toString()] = checkBox
            }
        }

        // Set the buttons actions

        selectButton.setOnClickListener{

            if (checkCollection.isNotEmpty()
                && cardZone.children.first().tag != "empty_card") {
                (it as Button).isClickable = false
                it.alpha = 0.5f
                cancelButton.isClickable = true
                cancelButton.alpha = 1f
                deleteButton.isClickable = true
                deleteButton.alpha = 1f
                for (checkBox in checkCollection.values){
                    checkBox.isVisible = true
                    checkBox.isClickable = true
                    checkBox.alpha = 1f
                }
            }else{
                Snackbar.make(it, "No projects to select", Snackbar.LENGTH_SHORT).show()
            }


        }

        cancelButton.setOnClickListener{
            (it as Button).isClickable = false
            it.alpha = 0.5f
            selectButton.isClickable = true
            selectButton.alpha = 1f
            deleteButton.isClickable = false
            deleteButton.alpha = 0.5f

            for (checkBox in checkCollection.values){
                checkBox.isVisible = true
                checkBox.isClickable = true
                checkBox.alpha = 1f
            }

        }

        deleteButton.setOnClickListener{
            (it as Button).isClickable = false
            it.alpha = 0.5f
            selectButton.isClickable = true
            selectButton.alpha = 1f
            cancelButton.isClickable = false
            cancelButton.alpha = 0.5f

            removeCards(userData,checkCollection)

            for (checkBox in checkCollection.values){
                checkBox.isVisible = true
                checkBox.isClickable = true
                checkBox.alpha = 1f
            }

        }
    }

    // Methods of action buttons
    private fun addCard(project: Project) {
        // Main layout and childrens
        val mainLayout = findViewById<ConstraintLayout>(R.id.card_section)
        val childrens = mainLayout.findViewById<LinearLayout>(R.id.CardZone).children

        // My stub card layout inflated
        val myLayout =
            LayoutInflater.from(this).inflate(R.layout.card_element, mainLayout, false)
        val outerConstraint = myLayout.findViewById<ConstraintLayout>(R.id.OuterConstraint)
        val card = myLayout.findViewById<CardView>(R.id.InnerCard)
        val text = myLayout.findViewById<TextView>(R.id.CardText)
        val button = myLayout.findViewById<TextView>(R.id.button)
        val check = findViewById<CheckBox>(R.id.checkBox)
        val progress = findViewById<ProgressBar>(R.id.PbarHorizontal)

        //General card settings
        val connections = myLayout.layoutParams as ConstraintLayout.LayoutParams
        connections.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        connections.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        when (childrens.count()>0){
            true -> {
                connections.topToBottom = childrens.last().id
            }
            false -> {
                connections.topToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
        connections.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
        connections.marginStart = 8
        connections.marginEnd = 8
        connections.topMargin = 8

        // new ID generation
        outerConstraint.id = View.generateViewId()
        card.id = View.generateViewId()
        text.id = View.generateViewId()
        button.id = View.generateViewId()
        check.id = View.generateViewId()
        progress.id = View.generateViewId()

        // Card contents
        text.text = project.p_name
        check.tag = project.p_id

        mainLayout.findViewById<LinearLayout>(R.id.CardZone).addView(myLayout)
    }

    private fun removeCards(database: Database, checkColl: MutableMap<String,CheckBox>){
        val cardZone= findViewById<LinearLayout>(R.id.CardZone)
        val toRemove: MutableList<String> = mutableListOf()
        for (checkBox in checkColl.values){
            if (checkBox.isChecked){
                toRemove.add(checkBox.tag.toString())
            }
        }
        for (id in toRemove){
            val project = database.user_projects.find { it.p_id == id }
            database.user_projects.remove(project)
            for (child in cardZone.children){
                if (child.findViewById<CheckBox>(R.id.checkBox).tag == id){
                    cardZone.removeView(child.parent.parent.parent as View)
                    checkColl.remove(id)
                }
            }
        }
    }

    private fun addEmptyCard(){
        val mainLayout = findViewById<ConstraintLayout>(R.id.card_section)
        val infalted = LayoutInflater.from(this).inflate(R.layout.empty_card, mainLayout, false)

        val outerConstraint = infalted.findViewById<ConstraintLayout>(R.id.empty_card_layout)
        val card = infalted.findViewById<CardView>(R.id.empty_card)
        val text = infalted.findViewById<TextView>(R.id.empty_card_text)

        outerConstraint.id = ConstraintLayout.generateViewId()
        outerConstraint.tag = "empty_card"
        card.id = ConstraintLayout.generateViewId()
        text.id = ConstraintLayout.generateViewId()

        val connections = infalted.layoutParams as ConstraintLayout.LayoutParams
        connections.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        connections.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        connections.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        connections.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
        connections.marginStart = 8
        connections.marginEnd = 8
        connections.topMargin = 8

        mainLayout.findViewById<LinearLayout>(R.id.CardZone).addView(infalted)

    }
}

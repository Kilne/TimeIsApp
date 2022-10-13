package com.example.timeisapp

import android.os.Bundle
import android.util.AttributeSet
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout

class HomeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout)

        // Setting the generic elements ready to be used
        val genericCard: CardView = CardView(this)
        val genericConstraintLayout: ConstraintLayout = ConstraintLayout(this)
        val genericButton: Button = Button(this)
        val genericTextView: TextView = TextView(this)
        val genericProgressBar: ProgressBar = ProgressBar(this)

        // Element for empty list
        val emptyListCard: CardView = findViewById(R.id.ProjectCards)
        emptyListCard.visibility = CardView.GONE

        // Settings for elements cards
        val ConstSettings: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(
            this,
            AttributeSet()
        )

    }
}

package com.example.timeisapp.database

import android.content.Context
import android.view.View.generateViewId
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.timeisapp.R

fun createEmpty(context: Context): CardView {
    val genericCard = CardView(context)
    val genericConstraintLayout = ConstraintLayout(context)
    val genericTextView = TextView(context)

    // Settings for elements cards
    genericCard.layoutParams.height = MATCH_PARENT
    genericCard.layoutParams.width = 0
    (genericCard.layoutParams as ViewGroup.MarginLayoutParams).setMargins(16, 8, 16, 0)
    genericCard.visibility = CardView.VISIBLE
    genericCard.radius = 8f
    genericCard.cardElevation = 8f
    genericCard.useCompatPadding = true
    genericCard.id = generateViewId()
    genericCard.tag = "emptyCard"
    genericCard.setCardBackgroundColor(context.getColor(R.color.white))
    // Constraints for elements cards
    val connections = genericCard.layoutParams as ConstraintLayout.LayoutParams
    connections.apply {
        connections.endToEnd = ConstraintSet.PARENT_ID
        connections.topToTop = ConstraintSet.PARENT_ID
        connections.startToStart = ConstraintSet.PARENT_ID
        connections.horizontalBias = 0.5f
    }


    // Inner constraints for elements cards
    genericConstraintLayout.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
    genericConstraintLayout.layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
    genericConstraintLayout.id = generateViewId()
    genericConstraintLayout.tag = "NoProjectsContent"
    genericConstraintLayout.visibility = ConstraintLayout.VISIBLE

    // Inner text for elements cards
    genericTextView.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
    genericTextView.layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
    (genericTextView.layoutParams as ViewGroup.MarginLayoutParams).setMargins(0, 8, 0, 8)
    genericTextView.id = generateViewId()
    genericTextView.tag = "NoProjectsText"
    genericTextView.visibility = TextView.VISIBLE
    genericTextView.text = R.string.No_projects.toString()
    genericTextView.textSize = 20f
    genericTextView.setTextColor(context.getColor(R.color.black))
    genericTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
    genericTextView.setTextAppearance(com.google.android.material.R.style.TextAppearance_AppCompat_Medium)
    genericTextView.setTypeface(genericTextView.typeface, android.graphics.Typeface.BOLD)
    // Constraints for inner text for elements cards
    val connections2 = genericTextView.layoutParams as ConstraintLayout.LayoutParams
    connections2.apply {
        connections2.endToEnd = ConstraintSet.PARENT_ID
        connections2.topToTop = ConstraintSet.PARENT_ID
        connections2.startToStart = ConstraintSet.PARENT_ID
        connections2.bottomToBottom = ConstraintSet.PARENT_ID
        connections2.horizontalBias = 0.5f
        connections2.verticalBias = 0.5f
    }



    return genericCard
}
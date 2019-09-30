package com.myprice.value.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

/**
Created by  on 30-Sep-19.
 */

fun showSnack(view: View, message: String) {

    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun refresh(intent: Intent? = null, activity: Activity) {

    activity.overridePendingTransition(0, 0)
    intent?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    activity.finish()
    activity.overridePendingTransition(0, 0)
    activity.startActivity(intent)
}

fun enableViews(vararg views: TextView) {
    for (v in views) {
        v.text = ""
    }
}
package com.myprice.value.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

/**
Created by  on 30-Sep-19.
 */

fun showSnack(view: View, message: String) {

    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

}

fun showToast(context: Context, message: String? = null) {

    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Context.showLog(message: String, e: Exception? = null) {
    Log.d(
        this.javaClass.simpleName,
        "Line Number ${e?.stackTrace?.get(0)?.lineNumber} --> $message"
    )
}
fun refresh(intent: Intent? = null, activity: Activity) {

    activity.overridePendingTransition(0, 0)
    intent?.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    activity.finish()
    activity.overridePendingTransition(0, 0)
    activity.startActivity(intent)
}

fun clearTheViews(vararg views: TextView) {
    for (v in views) {
        v.text = ""
    }
}

val Double.toCurrency: String?
    get() {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "in"))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(this)


    }
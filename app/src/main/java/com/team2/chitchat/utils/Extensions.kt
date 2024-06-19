package com.team2.chitchat.utils

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.team2.chitchat.R

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Context.toastMessageDuration(message: String, durationToast: Int) {
    val mainHandler = Handler(this.mainLooper)
    val runnable = Runnable {
        Toast.makeText(this, message, durationToast).show()
    }
    mainHandler.post(runnable)
}

fun Context.toastLong(message: String) {
    toastMessageDuration(message, Toast.LENGTH_LONG)
}

fun EditText.setErrorBorder(context: Context) {
    this.background = ContextCompat.getDrawable(context, R.drawable.edittext_error_background)
}

val Any.TAG: String
    get() {
        val tagSimpleName = javaClass.simpleName
        val tagName = javaClass.name
        return when {
            tagSimpleName.isNotBlank() -> {
                if (tagSimpleName.length > 23) {
                    tagSimpleName.takeLast(23)
                } else {
                    tagSimpleName
                }
            }

            tagName.isNotBlank() -> {
                if (tagName.length > 23) {
                    tagName.takeLast(23)
                } else {
                    tagName
                }
            }

            else -> {
                "TAG unknow"
            }
        }
    }

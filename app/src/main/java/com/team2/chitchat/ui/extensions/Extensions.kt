package com.team2.chitchat.ui.extensions

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
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

fun EditText.setErrorBorder(hasError: Boolean, context: Context, errorTextView: TextView?) {
    val drawableId = if (hasError) {
        R.drawable.edittext_error_background
    } else {
        R.drawable.edittext_background
    }

    errorTextView?.visibility = if (hasError) {
        View.VISIBLE
    } else {
        View.GONE
    }
    this.background = ContextCompat.getDrawable(context, drawableId)
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
                "TAG unknown"
            }
        }
    }

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard(it) }
}

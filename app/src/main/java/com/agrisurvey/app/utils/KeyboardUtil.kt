package com.agrisurvey.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardUtil {

    // Hides the keyboard
    private fun hideKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Shows the keyboard
    fun showKeyboard(context: Context, view: View) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    // Hide keyboard when user clicks anywhere outside of the EditText or Dropdown
    @SuppressLint("ClickableViewAccessibility")
    fun setupKeyboardHideOnTouch(view: View, context: Context) {
        view.setOnTouchListener { _, _ ->
            hideKeyboard(context, view)
            false
        }
    }
}
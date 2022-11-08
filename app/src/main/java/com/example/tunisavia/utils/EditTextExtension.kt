package com.example.tunisavia.utils

import android.annotation.SuppressLint
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import com.example.tunisavia.R

@SuppressLint("ClickableViewAccessibility")
fun EditText.changeVisibilityPassword() {
    var isPasswordVisible = false
    this.setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= this.right - this.compoundDrawables[2].bounds.width()) {
                val selection: Int = this.selectionEnd
                if (isPasswordVisible) {
                    // set drawable image
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_baseline_visibility_off_24,
                        0
                    )
                    // hide Password
                    this.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    isPasswordVisible = false
                } else {
                    // set drawable image
                    this.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_baseline_visibility_24,
                        0
                    )
                    // show Password
                    this.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    isPasswordVisible = true
                }
                this.setSelection(selection)
                return@OnTouchListener true
            }
        }
        false
    })
}
package com.example.tunisavia.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
fun EditText.textChanges(): Flow<CharSequence?> = callbackFlow {
    val listener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //offer(s)
            this@callbackFlow.trySend(s).isSuccess
        }
    }
    addTextChangedListener(listener)
    awaitClose { removeTextChangedListener(listener) }
}.onStart { emit(text) }
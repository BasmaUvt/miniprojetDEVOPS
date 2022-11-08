package com.example.tunisavia.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tunisavia.R

class Toolbar : ConstraintLayout, View.OnClickListener {

    ///////////////////////////////////////////////////////////////////////////
    // UI REFERENCES
    ///////////////////////////////////////////////////////////////////////////
    var toolbarInteractionInterface: ToolbarInteractionInterface? = null

    lateinit var toolbarTitle: TextView

    constructor(@NonNull context: Context) : super(context)

    constructor(@NonNull context: Context, @NonNull attr: AttributeSet) : super(context, attr) {
        val view = LayoutInflater.from(context).inflate(R.layout.toolbar, this)
        //view.findViewById<TextView>(R.id.toolbar_previous).setOnClickListener(this)

        toolbarTitle = view.findViewById(R.id.toolbar_title)

        view.findViewById<TextView>(R.id.toolbar_logo).setOnClickListener(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // CLICK HANDLING
    ///////////////////////////////////////////////////////////////////////////
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.toolbar_logo -> toolbarInteractionInterface?.clickQuitHandling()
        }
    }

    fun setTitle(title: String) {
        toolbarTitle.text = title
    }

    interface ToolbarInteractionInterface {
        fun clickQuitHandling()
    }
}

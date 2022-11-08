package com.example.tunisavia.visitor.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tunisavia.R
import com.example.tunisavia.entity.TechnicalParam
import com.example.tunisavia.visitor.VisitorInteraction

class PlaneAdapter(
    private val visitorInteraction: VisitorInteraction
) : RecyclerView.Adapter<PlaneAdapter.PlaneViewHolder>() {

    private var listPlane: MutableList<TechnicalParam> = mutableListOf()
    private var fromTech: Boolean = true

    fun addList(data: MutableList<TechnicalParam>) {
        val diffCallback = PlaneDiffUtils(listPlane, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listPlane.clear()
        listPlane.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class PlaneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ResourceType")
        fun bindView(plane: TechnicalParam, position: Int) {
            val removeFlight = itemView.findViewById<TextView>(R.id.remove_flight)
            removeFlight.visibility = if (fromTech) View.VISIBLE else View.GONE
            removeFlight.setOnClickListener {
                visitorInteraction.removePlane(plane)
            }
            val progressTeck = itemView.findViewById<View>(R.id.progress_check)
            val color = if (plane.isChecked == true) R.color.green_lite
            else R.color.yellow
            progressTeck.setBackgroundColor(ContextCompat.getColor(itemView.context, color))
            val namePlane = itemView.findViewById<TextView>(R.id.name_plane)
            namePlane.text = if (plane.nameVol?.isNotEmpty() == true) plane.nameVol
            else "undefined"
            itemView.setOnClickListener {
                visitorInteraction.onClick(plane)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaneAdapter.PlaneViewHolder {
        return PlaneViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.plane_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaneAdapter.PlaneViewHolder, position: Int) {
        holder.bindView(listPlane[position], position)
    }

    override fun getItemCount(): Int = listPlane.size

    fun checkVisitor(fromTech: Boolean) {
        this.fromTech = fromTech
    }
}

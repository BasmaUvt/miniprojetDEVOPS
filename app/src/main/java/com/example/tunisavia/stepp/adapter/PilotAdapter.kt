package com.example.tunisavia.stepp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tunisavia.R
import com.example.tunisavia.stepp.FragmentInteraction
import com.example.tunisavia.entity.Pilot

class PilotAdapter(
    val pilotInteraction: FragmentInteraction.PilotInteraction
) : RecyclerView.Adapter<PilotAdapter.PilotViewHolder>() {

    var list: MutableList<Pilot> = mutableListOf()

    fun addList(list: MutableList<Pilot>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class PilotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var image: ImageView
        lateinit var name: TextView
        fun bindView(pilot: Pilot, position: Int) {
            name = itemView.findViewById(R.id.pilot_name)
            image = itemView.findViewById(R.id.pilot_img)
            name.text = pilot.name
            itemView.setOnClickListener {
                pilotInteraction.onClick(pilot, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilotViewHolder =
        PilotViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pilot_item, parent, false))

    override fun onBindViewHolder(holder: PilotViewHolder, position: Int) {
        holder.bindView(list[position], position)
    }

    override fun getItemCount(): Int = list.size

}

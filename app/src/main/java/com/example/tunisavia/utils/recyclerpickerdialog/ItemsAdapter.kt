package com.example.tunisavia.utils.recyclerpickerdialog

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tunisavia.R
import com.example.tunisavia.databinding.ViewRowForItemBinding
import java.util.*

class ItemsAdapter(
    selection: SelectionType,
    val selectorType: SelectorType,
    val withImage: Boolean? = false,
    private val onDataUpdate: (old: Item?, new: Item, isSingle: Boolean) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    private val data: ArrayList<Item> = arrayListOf()
    private val isSingle = selection == SelectionType.SINGLE
    private var lastItemSelected: Item? = null
    var clickedPosition: Int? = -1

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int) = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewRowForItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.item = data[position]
        holder.binding.selector = selectorType
        holder.binding.withImage = withImage
        Log.i("itemsAdapter", "onBindViewHolder: $clickedPosition  ${data[position].isSelected}")

        if (!data[position].isSelected) {
            holder.binding.itemContainer.setBackgroundColor(Color.TRANSPARENT)
        } else {
            clickedPosition = position
            holder.binding.itemContainer.setBackgroundResource(R.drawable.color_status_1)
        }

        data[position].img?.let { holder.binding.itemImg.setImageResource(it) }

        with(holder.context) {
            holder.binding.choicePaddingStart =
                resources.getDimension(if (selectorType == SelectorType.SWITCH) R.dimen.padding_for_switch else if (selectorType == SelectorType.NONE) R.dimen.padding_for_none else R.dimen.padding_start_default)
        }
        holder.binding.executePendingBindings() //because selector visibility

        holder.binding.root.setOnClickListener {
            when (selectorType) {
                SelectorType.CHECK_BOX -> holder.binding.checkButton.performClick()
                SelectorType.RADIO_BUTTON -> holder.binding.radioButton.performClick()
                SelectorType.SWITCH -> holder.binding.switchButton.performClick()
                else -> {
                    holder.binding.switchButton.visibility = View.GONE
                    holder.binding.radioButton.visibility = View.GONE
                    holder.binding.checkButton.visibility = View.GONE
                    holder.binding.itemContainer.performClick()
                }
            }
        }
        holder.binding.radioButton.setOnClickListener { onUpdate(holder) }
        holder.binding.checkButton.setOnClickListener {
            /*clickedPosition?.let { pos ->
                if (position != pos && pos != -1) {
                    data[position].isSelected = true
                    data[pos].isSelected = false
                    Log.i("itemsAdapterAAAAAA", "onBindViewHolder: $pos  ${data[pos].isSelected}")
                    Log.i("itemsAdapterAAAAAA", "onBindViewHolder11: $position  ${data[position].isSelected}")
                    notifyItemChanged(position)
                    notifyItemChanged(pos)
                }
            }*/
            onUpdate(holder)
        }
        holder.binding.switchButton.setOnClickListener { onUpdate(holder) }
        holder.binding.itemContainer.setOnClickListener {
            /*clickedPosition?.let { pos ->
                if (position != pos && pos != -1) {
                    data[position].isSelected = true
                    data[pos].isSelected = false
                    notifyItemChanged(position)
                    notifyItemChanged(pos)
                }
            }*/
            onUpdate(holder)
        }
    }

    private fun onUpdate(holder: ViewHolder) {
        Log.i("RecyclerPickerDialogFragment", "onUpdate: old = $lastItemSelected  new = ${data[holder.adapterPosition]}")

        onDataUpdate.invoke(lastItemSelected, data[holder.adapterPosition], isSingle)
        lastItemSelected = data[holder.adapterPosition]
    }

    fun updateItems(items: ArrayList<Item>, defaultPosition: Int? = -1) {
        data.clear()
        data.addAll(items)
        Log.i("itemsAdapter", "updateItems: $clickedPosition  $defaultPosition")
        clickedPosition = defaultPosition
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ViewRowForItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val context = binding.root.context
    }
}
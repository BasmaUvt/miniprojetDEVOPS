package com.example.tunisavia.visitor.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.tunisavia.entity.TechnicalParam

class PlaneDiffUtils(
    val oldList: MutableList<TechnicalParam>,
    val newList: MutableList<TechnicalParam>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

}

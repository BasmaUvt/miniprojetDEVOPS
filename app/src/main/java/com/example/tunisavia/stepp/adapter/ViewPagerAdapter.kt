package com.example.tunisavia.stepp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tunisavia.base.BaseFragment

class ViewPagerAdapter(
    fm: FragmentManager,
    val list: MutableList<BaseFragment>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): Fragment = list[position]
}
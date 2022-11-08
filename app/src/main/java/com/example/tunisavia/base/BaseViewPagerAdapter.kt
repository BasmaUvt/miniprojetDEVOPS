package com.example.tunisavia.base

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BaseViewPagerAdapter constructor(
    fm: FragmentManager,
    private val fragments: MutableList<BaseFragment>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): BaseFragment = fragments[position]

    override fun getCount(): Int = fragments.size
}

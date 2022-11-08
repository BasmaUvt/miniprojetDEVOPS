package com.example.tunisavia.utils.viewpager

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

object ViewPagerUtils {
    fun <T> getCurrent(viewPager: ViewPager, pages: List<T>): T? {
        return if (isNotEmpty(viewPager)) {
            if (viewPager.currentItem < pages.size)
                pages[viewPager.currentItem]
            else
                null
        } else null
    }

    fun <T> getCurrent(viewPager: ViewPager2, pages: List<T>): T? {
        return if (isNotEmpty(viewPager)) {
            if (viewPager.currentItem < pages.size)
                pages[viewPager.currentItem]
            else
                null
        } else {
            null
        }
    }

    fun isNotEmpty(viewPager: ViewPager2): Boolean = size(viewPager) > 0

    fun size(viewPager: ViewPager2?): Int {
        var count = 0
        if (viewPager != null && viewPager.adapter != null) {
            viewPager.adapter?.itemCount?.let {
                count = it
            }
        } else {
            count = 0
        }
        return count
    }

    fun isNotEmpty(viewPager: ViewPager): Boolean = size(viewPager) > 0

    fun size(viewPager: ViewPager?): Int {
        var count = 0
        if (viewPager != null && viewPager.adapter != null) {
            viewPager.adapter?.count?.let {
                count = it
            }
        } else {
            count = 0
        }
        return count
    }

    fun onNavigateTo(viewPager: ViewPager, index: Int) {
        if (isNotEmpty(viewPager) && index >= 0 && index < size(viewPager)) {
            viewPager.currentItem = index
        }
    }

    fun onNext(viewPager: ViewPager) {
        if (isNotEmpty(viewPager)) {
            onNavigateTo(viewPager, viewPager.currentItem + 1)
        }
    }

    fun onPrevious(viewPager: ViewPager) {
        if (isNotEmpty(viewPager)) {
            onNavigateTo(viewPager, viewPager.currentItem - 1)
        }
    }
}

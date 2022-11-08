package com.example.tunisavia.utils.viewpager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class WrapContentViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        initPageChangeListener()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initPageChangeListener()
    }

    private fun initPageChangeListener() {
        addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                requestLayout()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mesureSpec = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            // super has to be called in the beginning so the child views can be initialized.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val height = onMesureHandler(widthMeasureSpec)
            mesureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, mesureSpec)
    }

    private fun onMesureHandler(widthMeasureSpec: Int): Int {
        var height = 0
        for (i in 0 until childCount) {
            val chil = getChildAt(i)
            chil?.let { child ->
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                val childMeasuredHeight = child.measuredHeight
                if (childMeasuredHeight > height) {
                    height = childMeasuredHeight
                }
            }
        }

        return height
    }
}

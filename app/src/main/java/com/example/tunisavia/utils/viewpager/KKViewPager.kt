package com.example.tunisavia.utils.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class KKViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs), ViewPager.PageTransformer {
    private var MAX_SCALE = 0.0f
    private var mPageMargin: Int
    private var animationEnabled = true
    private var fadeEnabled = false
    private var fadeFactor = 0.5f
    fun dp2px(resource: Resources, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resource.displayMetrics
        ).toInt()
    }

    fun setAnimationEnabled(enable: Boolean) {
        animationEnabled = enable
    }

    fun setFadeEnabled(fadeEnabled: Boolean) {
        this.fadeEnabled = fadeEnabled
    }

    fun setFadeFactor(fadeFactor: Float) {
        this.fadeFactor = fadeFactor
    }

    override fun setPageMargin(marginPixels: Int) {
        mPageMargin = marginPixels
        setPadding(mPageMargin, mPageMargin, mPageMargin, mPageMargin)
    }

    override fun transformPage(page: View, position: Float) {
        var position = position
        if (mPageMargin <= 0 || !animationEnabled) return
        page.setPadding(mPageMargin / 3, mPageMargin / 3, mPageMargin / 3, mPageMargin / 3)
        if (MAX_SCALE == 0.0f && position > 0.0f && position < 1.0f) {
            MAX_SCALE = position
        }
        position = position - MAX_SCALE
        val absolutePosition = Math.abs(position)
        if (position <= -1.0f || position >= 1.0f) {
            if (fadeEnabled) page.alpha = fadeFactor
            // Page is not visible -- stop any running animations
        } else if (position == 0.0f) {

            // Page is selected -- reset any views if necessary
            page.scaleX = 1 + MAX_SCALE
            page.scaleY = 1 + MAX_SCALE
            page.alpha = 1f
        } else {
            page.scaleX = 1 + MAX_SCALE * (1 - absolutePosition)
            page.scaleY = 1 + MAX_SCALE * (1 - absolutePosition)
            if (fadeEnabled) page.alpha = Math.max(fadeFactor, 1 - absolutePosition)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = false

    companion object {
        const val TAG = "KKViewPager"
    }

    init {
        // clipping should be off on the pager for its children so that they can scale out of bounds.
        clipChildren = false
        clipToPadding = false
        // to avoid fade effect at the end of the page
        overScrollMode = 2
        setPageTransformer(false, this)
        offscreenPageLimit = 5
        mPageMargin = dp2px(context.resources, 40)
        setPadding(mPageMargin, mPageMargin, mPageMargin, mPageMargin)
    }
}
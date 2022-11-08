package com.example.tunisavia.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tunisavia.R

class SearchBar : AppCompatEditText, OnTouchListener {
    //private var context: Context? = null
    private var isFirstIn = false
    private var isStretch = false
    private var searchBarStretchWidth = 0f
    private var searchBarStretchHeight = 0f
    private var currentSearchBarStretchWidth = 0f
    private var currentSearchBarStretchHeight = 0f
    private var searchBarHandleHeight = 0f
    private var searchBarOriginX = 0f
    private var searchBarStretchDirection: SearchBarStretchDirection? = null
    private var searchBarHintString: String? = null
    private var searchBarEdgeColor = 0
    private var searchBarBackgroundColor = 0
    private var searchBarEdgePadding = 0
    private var isStartSearch = false
    private var searchBarSearchingColor = 0
    private var rotateDegrees = 0f
    private var leftStartDrawableX = 0f
    private var leftStartDrawableY = 0f
    private var leftEndDrawableX = 0f
    private var leftEndDrawableY = 0f
    private var leftDrawableA = 0f
    private var leftDrawableB = 0f
    private var leftDrawableC = 0f
    private var rightStartDrawableX = 0f
    private var rightStartDrawableY = 0f
    private var rightEndDrawableX = 0f
    private var rightEndDrawableY = 0f
    private var rightDrawableA = 0f
    private var rightDrawableB = 0f
    private var rightDrawableC = 0f
    private var mPaint: Paint? = null
    private var touchState: TouchState? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super (context, attrs) {
        isFirstIn = true
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBar)
        isStretch = typedArray.getBoolean(R.styleable.SearchBar_searchbar_stretch, false)
        searchBarBackgroundColor =
            typedArray.getColor(R.styleable.SearchBar_searchbar_background_color, Color.WHITE)
        searchBarEdgeColor =
            typedArray.getColor(R.styleable.SearchBar_searchbar_edge_color, Color.BLACK)
        searchBarSearchingColor =
            typedArray.getColor(R.styleable.SearchBar_searchbar_searching_color, Color.WHITE)
        searchBarStretchWidth = typedArray.getDimension(
            R.styleable.SearchBar_searchbar_stretch_width,
            dp2px(context, 700)
        )

        searchBarStretchHeight = typedArray.getDimension(
            R.styleable.SearchBar_searchbar_stretch_height,
            dp2px(context, 60)
        )

        searchBarStretchDirection = if (typedArray.getInteger(
                R.styleable.SearchBar_searchbar_stretch_direction,
                0
            ) == 0
        ) SearchBarStretchDirection.SEARCHBAR_STRETCH_LEFT else SearchBarStretchDirection.SEARCHBAR_STRETCH_RIGHT
        isStartSearch = false
        rotateDegrees = 0f
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = searchBarEdgeColor
        mPaint!!.strokeWidth = dp2px(context, 2)
        mPaint!!.style = Paint.Style.STROKE
        touchState = if (isStretch) TouchState.TOUCH_UP_STATE else TouchState.TOUCH_CANCEL_STATE
        setOnTouchListener(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        setSingleLine()
        searchBarHandleHeight =
            ((height / 2.0 - searchBarEdgePadding) * Math.cos(Math.PI / 4.0)).toFloat()
        setPadding(
            (height / 2.0).toInt(), 0,
            (height / 2.0 + searchBarHandleHeight / 2.0).toInt(), 0
        )
        if (isFirstIn) {
            isFirstIn = false
            searchBarOriginX = getLeft().toFloat()
            currentSearchBarStretchWidth = height.toFloat()
            currentSearchBarStretchHeight = height.toFloat()
            if (this.hint != null) {
                searchBarHintString = this.hint.toString()
            }
            if (!isStretch) {
                this.hint = ""
            }
        }
        if (searchBarStretchDirection == SearchBarStretchDirection.SEARCHBAR_STRETCH_RIGHT) {
            leftStartDrawableX = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            leftStartDrawableY = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            leftEndDrawableX =
                (searchBarStretchWidth - height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(
                    Math.PI / 4
                )).toFloat()
            leftEndDrawableY =
                (height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(Math.PI / 4)).toFloat()
            rightStartDrawableX = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            rightStartDrawableY = (height / 2.0 - (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            rightEndDrawableX =
                (searchBarStretchWidth - height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(
                    Math.PI / 4
                )).toFloat()
            rightEndDrawableY =
                (height / 2.0 + searchBarHandleHeight / 2.0 * Math.cos(Math.PI / 4)).toFloat()
        } else {
            leftStartDrawableX = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            leftStartDrawableY = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            leftEndDrawableX =
                (searchBarStretchWidth - height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(
                    Math.PI / 4
                )).toFloat()
            leftEndDrawableY =
                (height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(Math.PI / 4)).toFloat()
            rightStartDrawableX = (height / 2.0 + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            rightStartDrawableY = (height / 2.0 - (height / 2.0 - searchBarEdgePadding) * Math.cos(
                Math.PI / 4.0
            )).toFloat()
            rightEndDrawableX =
                (searchBarStretchWidth - height / 2.0 - searchBarHandleHeight / 2.0 * Math.cos(
                    Math.PI / 4
                )).toFloat()
            rightEndDrawableY =
                (height / 2.0 + searchBarHandleHeight / 2.0 * Math.cos(Math.PI / 4)).toFloat()
        }
        leftDrawableA = ((leftEndDrawableY - leftStartDrawableY) / Math.pow(
            (leftEndDrawableX - leftStartDrawableX).toDouble(),
            2.0
        )).toFloat()
        leftDrawableB = -2 * leftDrawableA * leftStartDrawableX
        leftDrawableC = (leftStartDrawableY + leftDrawableA * Math.pow(
            leftStartDrawableX.toDouble(),
            2.0
        )).toFloat()
        rightDrawableA = ((rightEndDrawableY - rightStartDrawableY) / Math.pow(
            (rightEndDrawableX - rightStartDrawableX).toDouble(),
            2.0
        )).toFloat()
        rightDrawableB = -2 * rightDrawableA * rightStartDrawableX
        rightDrawableC = (rightStartDrawableY + rightDrawableA * Math.pow(
            rightStartDrawableX.toDouble(),
            2.0
        )).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        when (touchState) {
            TouchState.TOUCH_DOWN_STATE -> {}
            TouchState.TOUCH_MOVE_STATE -> {}
            TouchState.TOUCH_UP_STATE -> if (isStretch) {
                if (currentSearchBarStretchWidth < searchBarStretchWidth && currentSearchBarStretchHeight < searchBarStretchHeight) {
                    currentSearchBarStretchWidth += searchBarStretchWidth / 10
                    currentSearchBarStretchHeight += searchBarStretchHeight / 10
                    if (currentSearchBarStretchWidth > searchBarStretchWidth) {
                        currentSearchBarStretchWidth = searchBarStretchWidth
                    }
                    if (currentSearchBarStretchHeight > searchBarStretchHeight) {
                        currentSearchBarStretchHeight = searchBarStretchHeight
                    }
                    val layoutParams = ConstraintLayout.LayoutParams(
                        currentSearchBarStretchWidth.toInt(),
                        currentSearchBarStretchHeight.toInt(),
                    )
                    if (searchBarStretchDirection == SearchBarStretchDirection.SEARCHBAR_STRETCH_RIGHT) {
                        layoutParams.leftMargin = left
                    } else {
                        layoutParams.leftMargin =
                            (searchBarOriginX - (currentSearchBarStretchWidth - height / 2)).toInt()
                    }
                    layoutParams.topMargin = top
                    setLayoutParams(layoutParams)
                }
            } else {
                if (currentSearchBarStretchWidth > height && currentSearchBarStretchHeight > height) {
                    currentSearchBarStretchWidth -= searchBarStretchWidth / 10
                    currentSearchBarStretchHeight -= searchBarStretchHeight / 10
                    if (currentSearchBarStretchWidth < height) {
                        currentSearchBarStretchWidth = height.toFloat()
                    }
                    if (currentSearchBarStretchHeight < height) {
                        currentSearchBarStretchHeight = height.toFloat()
                    }
                    val layoutParams = ConstraintLayout.LayoutParams(
                        currentSearchBarStretchWidth.toInt(),
                        currentSearchBarStretchHeight.toInt()
                    )
                    if (searchBarStretchDirection == SearchBarStretchDirection.SEARCHBAR_STRETCH_RIGHT) {
                        layoutParams.leftMargin = left
                    } else {
                        layoutParams.leftMargin =
                            (searchBarOriginX - (currentSearchBarStretchWidth - height / 2)).toInt()
                    }
                    layoutParams.topMargin = top
                    setLayoutParams(layoutParams)
                }
            }
            TouchState.TOUCH_CANCEL_STATE -> {}
        }
        setLayerBg()
        super.onDraw(canvas)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchState = TouchState.TOUCH_DOWN_STATE
            MotionEvent.ACTION_MOVE -> touchState = TouchState.TOUCH_MOVE_STATE
            MotionEvent.ACTION_UP -> if (!isStretch || isStretch && isInCloseRect(
                    event.x,
                    event.y
                )
            ) {
                isStretch = !isStretch
                isStartSearch = false
                setSearchBarStatus()
            }
            MotionEvent.ACTION_CANCEL -> touchState = TouchState.TOUCH_CANCEL_STATE
        }
        return super.onTouchEvent(event)
    }

    fun setSearchBarStatus() {
        touchState = TouchState.TOUCH_UP_STATE
        if (!isStretch) {
            this.isCursorVisible = false
            this.setText("")
            this.hint = ""
        } else {
            this.isCursorVisible = true
            this.hint = searchBarHintString
        }
    }

    fun startSearching(startSearchingAnimation: Boolean) {
        isStretch = false
        isStartSearch = startSearchingAnimation
        rotateDegrees = 0f
        setSearchBarStatus()
    }

    fun finishSearching() {
        isStretch = false
        isStartSearch = false
        setSearchBarStatus()
    }

    fun isInCloseRect(x: Float, y: Float): Boolean {
        val isInCloseRect: Boolean
        val x0 = searchBarStretchWidth - height + searchBarEdgePadding
        val y0 = searchBarEdgePadding.toFloat()
        val x1 = searchBarStretchWidth - searchBarEdgePadding * 2
        val y1 = (height - searchBarEdgePadding * 2).toFloat()
        isInCloseRect = x0 < x && x < x1 && y0 < y && y < y1
        return isInCloseRect
    }

    private fun setLayerBg() {
        val radius = (height / 2.0).toFloat()
        val outerR = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val roundRectShape = RoundRectShape(outerR, null, null)
        val backgroundDrawable = ShapeDrawable()
        backgroundDrawable.shape = roundRectShape
        backgroundDrawable.paint.color = searchBarBackgroundColor
        val edgeDrawable = ShapeDrawable()
        edgeDrawable.shape = roundRectShape
        edgeDrawable.paint.style = Paint.Style.STROKE
        edgeDrawable.paint.strokeWidth = dp2px(context, 2)
        edgeDrawable.paint.color = searchBarEdgeColor
        val leftDrawable: LeftDrawable = LeftDrawable(RectShape())
        leftDrawable.setBounds(0, 0, width, height)
        val rightDrawable: RightDrawable = RightDrawable(RectShape())
        rightDrawable.setBounds(0, 0, width, height)
        val layerDrawable: LayerDrawable
        layerDrawable = if (isStartSearch && width == height) {
            val circleDrawable = CircleDrawable(RectShape())
            circleDrawable.setBounds(0, 0, width, height)
            val layers = arrayOf<Drawable>(
                backgroundDrawable,
                edgeDrawable,
                leftDrawable,
                rightDrawable,
                circleDrawable
            )
            LayerDrawable(layers)
        } else {
            val layers =
                arrayOf<Drawable>(backgroundDrawable, edgeDrawable, leftDrawable, rightDrawable)
            LayerDrawable(layers)
        }
        searchBarEdgePadding = (mPaint!!.strokeWidth + dp2px(context, 5)).toInt()
        layerDrawable.setLayerInset(
            0,
            searchBarEdgePadding,
            searchBarEdgePadding,
            searchBarEdgePadding,
            searchBarEdgePadding
        )
        layerDrawable.setLayerInset(
            1,
            searchBarEdgePadding,
            searchBarEdgePadding,
            searchBarEdgePadding,
            searchBarEdgePadding
        )
        background = layerDrawable
    }

    internal enum class SearchBarStretchDirection {
        SEARCHBAR_STRETCH_LEFT, SEARCHBAR_STRETCH_RIGHT
    }

    internal enum class TouchState {
        TOUCH_DOWN_STATE, TOUCH_MOVE_STATE, TOUCH_UP_STATE, TOUCH_CANCEL_STATE
    }

    internal inner class CircleDrawable(shape: Shape?) : ShapeDrawable(shape) {
        public override fun onDraw(shape: Shape, canvas: Canvas, paint: Paint) {
            canvas.rotate(
                rotateDegrees, (height / 2.0).toFloat(),
                (height / 2.0).toFloat()
            )
            rotateDegrees += 8f
            if (rotateDegrees == 360f) {
                rotateDegrees = 0f
            }
            mPaint!!.color = searchBarSearchingColor
            val center = (height / 2.0).toFloat()
            val radius = (height / 2.0 - searchBarEdgePadding).toFloat()
            canvas.drawArc(
                RectF(center - radius, center - radius, center + radius, center + radius),
                -180f,
                45f,
                false,
                mPaint!!
            )
            mPaint!!.color = searchBarEdgeColor
        }
    }

    internal inner class LeftDrawable(shape: Shape?) : ShapeDrawable(shape) {
        public override fun onDraw(shape: Shape, canvas: Canvas, paint: Paint) {
            var startX =
                (height / 2.0 + (currentSearchBarStretchWidth - height) + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                    Math.PI / 4.0
                )).toFloat()
            if (startX > leftEndDrawableX) {
                startX = leftEndDrawableX
            }
            val startY = (leftDrawableA * Math.pow(
                startX.toDouble(),
                2.0
            ) + leftDrawableB * startX + leftDrawableC).toFloat()
            val stopX = (startX + searchBarHandleHeight * Math.cos(Math.PI / 4.0)).toFloat()
            val stopY = (startY + searchBarHandleHeight * Math.cos(Math.PI / 4.0)).toFloat()
            canvas.drawLine(startX, startY, stopX, stopY, mPaint!!)
        }
    }

    internal inner class RightDrawable(shape: Shape?) : ShapeDrawable(shape) {
        public override fun onDraw(shape: Shape, canvas: Canvas, paint: Paint) {
            if (currentSearchBarStretchWidth > searchBarStretchWidth * 3.0 / 4.0 && currentSearchBarStretchHeight > searchBarStretchHeight * 3.0 / 4.0) {
                if (!isStretch) {
                    mPaint!!.alpha = 0
                } else {
                    mPaint!!.alpha =
                        ((currentSearchBarStretchWidth - searchBarStretchWidth * 3.0 / 4.0) / (searchBarStretchWidth / 4.0) * 255).toInt()
                }
                var startX =
                    (height / 2.0 + (currentSearchBarStretchWidth - height) + (height / 2.0 - searchBarEdgePadding) * Math.cos(
                        Math.PI / 4.0
                    )).toFloat()
                if (startX > rightEndDrawableX) {
                    startX = rightEndDrawableX
                }
                val startY =
                    (rightDrawableA * Math.pow(
                        startX.toDouble(),
                        2.0
                    ) + rightDrawableB * startX + rightDrawableC).toFloat()
                val stopX = (startX + searchBarHandleHeight * Math.cos(Math.PI / 4.0)).toFloat()
                val stopY = (startY - searchBarHandleHeight * Math.cos(Math.PI / 4.0)).toFloat()
                canvas.drawLine(startX, startY, stopX, stopY, mPaint!!)
                mPaint!!.alpha = 255
            }
        }
    }

    companion object {
        fun dp2px(context: Context?, dpValue: Int): Float {
            val scale = context?.resources?.displayMetrics?.density ?: 0f
            return (dpValue * scale + 0.5).toFloat()
        }
    }

   /* init {
        init(context, attrs)
    }*/
}
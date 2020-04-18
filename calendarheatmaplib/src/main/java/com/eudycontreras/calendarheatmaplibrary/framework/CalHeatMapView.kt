package com.eudycontreras.calendarheatmaplibrary.framework

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.eudycontreras.calendarheatmaplibrary.findScrollParent
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapData
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

interface CalHeatMap {
    fun update()
    fun fullyVisible(): Boolean
    var onFullyVisible: ((CalHeatMap) -> Unit)?
}

class CalHeatMapView  : View, CalHeatMap {

    private var sizeRatio = 0.5f

    private var fullyVisible: Boolean = false
    private var initialized: Boolean = false

    private var shapeRenderer: ShapeRenderer = ShapeRenderer()
    private var heatMapBuilder: CalHeatMapBuilder = CalHeatMapBuilder(shapeRenderer)

    private var scrollingParent: ViewParent? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun initializeValues() {
        val width = width
        val height = height

        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        val usableWidth = width - (paddingLeft + paddingRight).toFloat()
        val usableHeight = height - (paddingTop + paddingBottom).toFloat()

        shapeRenderer.setBounds(Bounds(0f, 0f, usableWidth, usableHeight))

        initialized = true

        if(scrollingParent == null) {
            fullyVisible = true
            onFullyVisible?.invoke(this)
        }
    }

    fun setCalHeatMapData(calHeatMapData: HeatMapData) {
        heatMapBuilder.buildWith(calHeatMapData)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!initialized) {
            initializeValues()
            shapeRenderer.renderShapes(canvas)
            invalidate()
        } else {
            shapeRenderer.renderShapes(canvas)
        }
    }

    override var onFullyVisible: ((CalHeatMap) -> Unit)? = null

    override fun fullyVisible(): Boolean = fullyVisible

    fun observeVisibility() {
        val scrollBounds = Rect()

        scrollingParent = findScrollParent(this.parent as ViewGroup) {
            it is ScrollView || it is NestedScrollView
        }

        scrollingParent?.let { parent ->
            when (parent) {
                is ScrollView -> {
                    parent.getDrawingRect(scrollBounds)

                    notifyVisibility(scrollBounds)

                    parent.viewTreeObserver.addOnScrollChangedListener {
                        parent.getDrawingRect(scrollBounds)

                        notifyVisibility(scrollBounds)
                    }
                }
                is NestedScrollView -> {
                    parent.getDrawingRect(scrollBounds)

                    notifyVisibility(scrollBounds)
                    parent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->

                        notifyVisibility(scrollBounds)
                    }
                }
            }
        }
    }

    private fun notifyVisibility(
        scrollBounds: Rect
    ) {
        val top = this.y
        val bottom = top + this.height

        if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
            if (!fullyVisible) {
                fullyVisible = true
                onFullyVisible?.invoke(this)
            }
        }
    }

    private val myListener =  object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            super.onLongPress(event)
            parent.requestDisallowInterceptTouchEvent(true)
            shapeRenderer.delegateLongPressEvent(event, event.x, event.y)

            invalidate()
        }
    }

    private val detector: GestureDetector = GestureDetector(context, myListener)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let { result ->

            invalidate()

            val x = event.x
            val y = event.y

            shapeRenderer.delegateTouchEvent(event, x, y)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            result
        }
    }

    override fun update() {
        invalidate()
    }
}
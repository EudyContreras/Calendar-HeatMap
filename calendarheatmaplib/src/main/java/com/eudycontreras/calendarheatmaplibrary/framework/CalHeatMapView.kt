package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.*
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.R
import com.eudycontreras.calendarheatmaplibrary.findScrollParent
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.getTextMeasurement
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import com.eudycontreras.calendarheatmaplibrary.properties.Padding
import kotlin.math.max

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

class CalHeatMapView : View, CalHeatMap {

    private var sizeRatio = 0.5f

    private var fullyVisible: Boolean = false

    private var scrollingParent: ViewParent? = null

    private var measurements: Measurements = Measurements()

    private var calHeatMapStyle: HeatMapStyle = HeatMapStyle()
    private var calHeatMapOptions: HeatMapOptions = HeatMapOptions()

    private var shapeRenderer: ShapeRenderer = ShapeRenderer()
    private var heatMapBuilder: CalHeatMapBuilder = CalHeatMapBuilder(
        shapeRenderer = shapeRenderer,
        styleContext = { calHeatMapStyle },
        optionsContext = { calHeatMapOptions },
        contextProvider = { context }
    )

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalHeatMapView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        val defaultColor = MutableColor(255, 225, 225, 225).toColor()
        calHeatMapStyle.emptyCellColor = defaultColor

        typedArray.getColor(R.styleable.CalHeatMapView_legendLabelColor, -1).let {
            calHeatMapStyle.legendLabelStyle.textColor = if (it != -1) { it } else defaultColor
        }
        typedArray.getColor(R.styleable.CalHeatMapView_monthLabelColor, -1).let {
            calHeatMapStyle.monthLabelStyle.textColor = if (it != -1) { it } else defaultColor
        }
        typedArray.getColor(R.styleable.CalHeatMapView_weekDayLabelColor, -1).let {
            calHeatMapStyle.dayLabelStyle.textColor = if (it != -1) { it } else defaultColor
        }
    }

    fun setCalHeatMapData(calHeatMapData: HeatMapData) {
        heatMapBuilder.buildWithData(calHeatMapData)
    }

    fun setCalHeatMapStyle(calHeatMapStyle: HeatMapStyle) {
        this.calHeatMapStyle = calHeatMapStyle
    }

    fun setCalHeatMapOptions(calHeatMapOptions: HeatMapOptions) {
        this.calHeatMapOptions = calHeatMapOptions
    }

    fun setInterceptorElevation(@Dimension interceptorElevation: Float) {
        this.calHeatMapStyle.interceptorElevation = interceptorElevation
    }

    fun setInterceptorLinesColor(@ColorInt interceptorLinesColor: Int) {
        this.calHeatMapStyle.interceptorLinesColor = interceptorLinesColor
    }

    fun setInterceptorCenterColor(@ColorInt interceptorCenterColor: Int) {
        this.calHeatMapStyle.interceptorCenterColor = interceptorCenterColor
    }

    fun setInterceptorLineThickness(@Dimension interceptorLineThickness: Float) {
        this.calHeatMapStyle.interceptorLineThickness = interceptorLineThickness
    }

    fun setShowCellDayText(showCellDayText: Boolean) {
        this.calHeatMapOptions.showCellDayText = showCellDayText
    }

    fun setCellElevation(@Dimension cellElevation: Float) {
        this.calHeatMapStyle.cellElevation = cellElevation
    }

    fun setCellColorMin(@ColorInt minCellColor: Int) {
        this.calHeatMapStyle.minCellColor = minCellColor
    }

    fun setCellColorMax(@ColorInt maxCellColor: Int) {
        this.calHeatMapStyle.maxCellColor = maxCellColor
    }

    fun setCellColorEmpty(@ColorInt emptyCellColor: Int) {
        this.calHeatMapStyle.emptyCellColor = emptyCellColor
    }

    fun setMonthLabelColor(@ColorInt labelColor: Int) {
        this.calHeatMapStyle.monthLabelStyle.textColor = labelColor
    }

    fun setMonthLabelTypeFace(typeFace: Typeface) {
        this.calHeatMapStyle.monthLabelStyle.typeFace = typeFace
    }

    fun setMonthLabelTextSize(labelSize: Float) {
        this.calHeatMapStyle.monthLabelStyle.textSize = labelSize
    }

    fun setWeekDayLabelColor(@ColorInt labelColor: Int) {
        this.calHeatMapStyle.dayLabelStyle.textColor = labelColor
    }

    fun setWeekDayLabelTypeFace(typeFace: Typeface) {
        this.calHeatMapStyle.dayLabelStyle.typeFace = typeFace
    }

    fun setWeekDayLabelTextSize(labelSize: Float) {
        this.calHeatMapStyle.dayLabelStyle.textSize = labelSize
    }

    fun setLegendLabelColor(@ColorInt labelColor: Int) {
        this.calHeatMapStyle.legendLabelStyle.textColor = labelColor
    }

    fun setLegendLabelTypeFace(typeFace: Typeface) {
        this.calHeatMapStyle.legendLabelStyle.typeFace = typeFace
    }

    fun setLegendLabelTextSize(labelSize: Float) {
        this.calHeatMapStyle.legendLabelStyle.textSize = labelSize
    }

    fun setShowDayLabels(showDayLabels: Boolean) {
        this.calHeatMapOptions.showDayLabels = showDayLabels
    }

    fun setShowMonthLabels(showMonthLabels: Boolean) {
        this.calHeatMapOptions.showMonthLabels = showMonthLabels
    }

    fun setShowLegend(showLegend: Boolean) {
        this.calHeatMapOptions.showLegend = showLegend
    }

    fun setLegendAlignment(alignment: Alignment) {
        this.calHeatMapOptions.legendAlignment = alignment
    }

    fun setLegendLessLabelText(lessLabelText: String) {
        this.calHeatMapOptions.legendLessLabel = lessLabelText
    }

    fun setLegendMoreLabelText(moreLabelText: String) {
        this.calHeatMapOptions.legendMoreLabel = moreLabelText
    }

    fun setRevealOnVisible(revealOnVisible: Boolean) {

    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val padding = Padding(
            paddingStart = max(paddingLeft, paddingStart),
            paddingEnd = max(paddingRight, paddingEnd),
            paddingTop = paddingTop,
            paddingBottom = paddingBottom
        )

        this.heatMapBuilder.buildWithBounds(
            this,
            bounds = Bounds(
                left = padding.paddingStart.toFloat(),
                right = (width - padding.paddingEnd).toFloat(),
                top = padding.paddingTop.toFloat(),
                bottom = (height - padding.paddingBottom).toFloat()
            ),
            measurements = measurements
        )

        observeVisibility()

        if(scrollingParent == null){
            fullyVisible = true
            onFullyVisible?.invoke(this)
            return
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        shapeRenderer.renderShapes(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)

        heatMapBuilder.getData()?.let { data ->

            buildMeasurements(data = data, measuredHeight = specHeight) { width, height ->
                setMeasuredDimension(
                    resolveSize(width, widthMeasureSpec),
                    resolveSize(height, heightMeasureSpec)
                )
            }
        }
    }

    private fun buildMeasurements(data: HeatMapData, measuredHeight: Int, onMeasured: (Int, Int) -> Unit) {
        val cellSize = data.cellSize

        val gapSize = data.cellGap ?: if (cellSize != null) { cellSize * HeatMapData.CELL_SIZE_RATIO } else {
            (measuredHeight / TimeSpan.MAX_DAYS) * HeatMapData.CELL_SIZE_RATIO
        }

        val legendAreaHeight = getLegendAreaMeasurement(gapSize)
        val dayAreaWidth = getDayLabelAreaMeasurement(gapSize)
        val monthAreaHeight = getMonthLabelAreaMeasurement(gapSize)

        val gapRatio = (gapSize * TimeSpan.MAX_DAYS)
        val matrixHeight = measuredHeight.toFloat() - (legendAreaHeight + monthAreaHeight)

        val size = data.cellSize ?: ((matrixHeight  - gapRatio) / TimeSpan.MAX_DAYS)

        val count = data.getColumnCount()
        val width = dayAreaWidth + (size * count + (gapSize * count)) + gapSize

        measurements = Measurements(
            cellGap = gapSize,
            cellSize = size,
            matrixWidth = width,
            matrixHeight = matrixHeight,
            legendAreaHeight = legendAreaHeight,
            dayLabelAreaWidth = dayAreaWidth,
            monthLabelAreaHeight = monthAreaHeight
        )

        onMeasured(width.toInt(), measuredHeight)
    }

    private fun getLegendAreaMeasurement(gapSize: Float): Float {
        val legendAreaTextHeight = if (calHeatMapOptions.showLegend) {
            val textMeasurement = getTextMeasurement(
                paint = shapeRenderer.paint,
                text = calHeatMapOptions.legendLessLabel,
                textSize = calHeatMapStyle.legendLabelStyle.textSize,
                typeFace = calHeatMapStyle.legendLabelStyle.typeFace
            )
            textMeasurement.height() + gapSize
        } else MIN_OFFSET

        return if (calHeatMapOptions.showLegend) {
            max(HeatMapOptions.LEGEND_AREA_HEIGHT, legendAreaTextHeight + (gapSize * 2))
        } else MIN_OFFSET
    }

    private fun getDayLabelAreaMeasurement(gapSize: Float): Float {
        return if (calHeatMapOptions.showDayLabels) {
            val textMeasurement = calHeatMapOptions.dayLabels.map { label ->
                getTextMeasurement(
                    paint = shapeRenderer.paint,
                    text = label.text,
                    textSize = calHeatMapStyle.dayLabelStyle.textSize,
                    typeFace = calHeatMapStyle.dayLabelStyle.typeFace
                )
            }.maxBy { it.width() }
            (textMeasurement?.width()?.toFloat() ?: MIN_OFFSET) + gapSize
        } else MIN_OFFSET
    }

    private fun getMonthLabelAreaMeasurement(gapSize: Float): Float {
        return if (calHeatMapOptions.showMonthLabels) {
            val textMeasurement = getTextMeasurement(
                paint = shapeRenderer.paint,
                text = calHeatMapOptions.monthLabels.map { it.text }.first { it.any { text -> Character.isUpperCase(text) } },
                textSize = calHeatMapStyle.monthLabelStyle.textSize,
                typeFace = calHeatMapStyle.monthLabelStyle.typeFace
            )
            textMeasurement.height() + (gapSize * 2)
        } else MIN_OFFSET
    }

    override var onFullyVisible: ((CalHeatMap) -> Unit)? = null

    override fun fullyVisible(): Boolean = fullyVisible

    private fun observeVisibility() {
        val scrollBounds = Rect()
        val viewBounds = Rect()

        scrollingParent = findScrollParent(this.parent as ViewGroup) {
            it !is NestedScrollView && it is ScrollView || it is NestedScrollView
        }

        scrollingParent?.let { parent ->
            when (parent) {
                is ScrollView -> {
                    retrieveBounds(viewBounds, parent, scrollBounds)
                    notifyVisibility(viewBounds, scrollBounds)

                    parent.viewTreeObserver.addOnScrollChangedListener {
                        retrieveBounds(viewBounds, parent, scrollBounds)
                        notifyVisibility(viewBounds, scrollBounds)
                    }
                }
                is NestedScrollView -> {
                    retrieveBounds(viewBounds, parent, scrollBounds)
                    notifyVisibility(viewBounds, scrollBounds)

                    parent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                        retrieveBounds(viewBounds, parent, scrollBounds)
                        notifyVisibility(viewBounds, scrollBounds)
                    }
                }
            }
        }
    }

    private fun retrieveBounds(
        viewBounds: Rect,
        parent: ViewGroup,
        scrollBounds: Rect
    ) {
        getDrawingRect(viewBounds)
        parent.offsetDescendantRectToMyCoords(this, viewBounds)
        parent.getDrawingRect(scrollBounds)
    }

    private fun notifyVisibility(
        hitBounds: Rect,
        scrollBounds: Rect
    ) {
        val top = hitBounds.top
        val bottom = hitBounds.bottom

        if (scrollBounds.top < (top + ((bottom - top) * sizeRatio)) && scrollBounds.bottom > (bottom - ((bottom - top) * sizeRatio))) {
            if (!fullyVisible) {
                fullyVisible = true
                onFullyVisible?.invoke(this)
            }
        }
    }

    private val detector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            super.onLongPress(event)
            parent.requestDisallowInterceptTouchEvent(true)
            shapeRenderer.delegateLongPressEvent(event, event.x, event.y)

            invalidate()
        }
    }).apply {
        setIsLongpressEnabled(true)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return detector.onTouchEvent(event).let { result ->

            invalidate()

            val x = event.x
            val y = event.y

            shapeRenderer.delegateTouchEvent(event, x, y)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    performClick()
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
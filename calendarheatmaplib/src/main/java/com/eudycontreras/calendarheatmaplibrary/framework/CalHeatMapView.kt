package com.eudycontreras.calendarheatmaplibrary.framework

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.MainThread
import androidx.core.widget.NestedScrollView
import androidx.databinding.ViewDataBinding
import com.eudycontreras.calendarheatmaplibrary.*
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.extensions.findMaster
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Padding
import kotlin.math.max

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

@MainThread
class CalHeatMapView : View, CalHeatMap {

    private var sizeRatio = 0.75f

    private var interactive: Boolean = false
    private var animStarted: Boolean = false
    private var fullyVisible: Boolean = false

    private var infoViewBinding: ViewDataBinding? = null

    private val viewBounds: Rect = Rect()
    private val scrollBounds: Rect = Rect()

    private var scrollingParent: ViewParent? = null

    private var measurements: Measurements = Measurements()

    private var calHeatMapStyle: HeatMapStyle = HeatMapStyle()
    private var calHeatMapOptions: HeatMapOptions = HeatMapOptions()

    private var animationProposals: MutableList<AnimationEvent> = mutableListOf()
    private var animationRemovals: MutableList<AnimationEvent> = mutableListOf()
    override var animationCollection: MutableList<AnimationEvent> = mutableListOf()

    private var infiniteAnimator: ValueAnimator? = ValueAnimator.ofFloat(MAX_OFFSET, MIN_OFFSET)

    private var shapeManager: ShapeManager = ShapeManager()

    private var heatMapBuilder: CalHeatMapBuilder = CalHeatMapBuilder(
        shapeManager = shapeManager,
        styleContext = { calHeatMapStyle },
        optionsContext = { calHeatMapOptions },
        viewportProvider = { viewBounds }
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

    private fun setUpAttributes(typedArray: TypedArray) { }

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

    fun setCellInfoView(cellInfoView: ViewDataBinding?) {
        this.infoViewBinding = cellInfoView
    }

    override fun hapticFeeback(feedback: Int) {
        if (!this.isHapticFeedbackEnabled) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            performHapticFeedback(feedback)
        }
    }

    override fun addAnimation(animation: AnimationEvent?) {
        if (animation == null) return
        animationProposals.add(animation)
        if (!animStarted) {
            startAnimation()
        }
    }

    override fun removeAnimation(animation: AnimationEvent?) {
        if (animation == null) return
        animationRemovals.add(animation)
        if (!animStarted) {
            startAnimation()
        }
    }

    override fun stopAnimation() {
        if (!animStarted)
            return

        animStarted = false
        infiniteAnimator?.cancel()
        infiniteAnimator = null
    }

    override fun startAnimation() {
        if (animStarted)
            return

        animStarted = true
        infiniteAnimator?.cancel()
        infiniteAnimator = ValueAnimator.ofFloat(MAX_OFFSET, MIN_OFFSET)
        infiniteAnimator?.duration = Long.MAX_VALUE
        infiniteAnimator?.repeatCount = ValueAnimator.INFINITE
        infiniteAnimator?.interpolator = LinearInterpolator()
        infiniteAnimator?.addUpdateListener(this)
        infiniteAnimator?.start()
    }

    override fun onAnimationUpdate(animator: ValueAnimator) {
        if (!animStarted) return

        if (fullyVisible) {
            if (animationProposals.size > 0) {
                animationCollection.addAll(animationProposals)
                animationProposals.clear()
            }
            if (animationCollection.size > 0) {
                for (animation in animationCollection) {
                    if (animation.hasStarted && animation.isRunning) {
                        if (!animation.startInvoked) {
                            animation.onStart?.invoke()
                            animation.startInvoked = true
                        }
                        val playTime = System.currentTimeMillis() - animation.startTime
                        val value = animator.animatedValue as Float
                        animation.onUpdate(playTime, value)
                    }
                    if (animation.hasEnded && !animation.endedInvoked) {
                        animation.onEnd?.invoke()
                        animationRemovals.add(animation)
                        animation.endedInvoked = true
                    }
                }
                invalidate()
            } else {
                stopAnimation()
            }
            if (animationRemovals.size > 0) {
                animationCollection.removeAll(animationRemovals)
                animationRemovals.clear()
            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val padding = Padding(
            paddingStart = max(paddingLeft, paddingStart),
            paddingEnd = max(paddingRight, paddingEnd),
            paddingTop = paddingTop,
            paddingBottom = paddingBottom
        )

        val cellBubbleLayout: BubbleLayout? = infoViewBinding?.run {
            val view = this.root
            if (view is BubbleLayoutView) {
                view.attachOverlay {
                    setRenderData(shapeManager.renderData)
                }
                view.setDataInteceptListener {
                    setVariable(VIEWMODEL, it as WeekDay)
                }
                view
            } else null
        }

        this.heatMapBuilder.buildWithBounds(
            this,
            bounds = Bounds(
                left = padding.paddingStart.toFloat(),
                right = (width - padding.paddingEnd).toFloat(),
                top = padding.paddingTop.toFloat(),
                bottom = (height - padding.paddingBottom).toFloat()
            ),
            measurements = measurements,
            bubbleLayout = cellBubbleLayout
        )

        if (!animStarted) {
            startAnimation()
        }
        scrollingParent = observeVisibility()

        if (scrollingParent == null){
            if (!fullyVisible) {
                fullyVisible = true
                onFullyVisible?.invoke(this)
            }
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        shapeManager.renderShapes(canvas)
    }

    private fun getTextMeasurement(paint: Paint, text: String?, textSize: Float, typeFace: Typeface): Rect {
        val textBounds = Rect()
        paint.recycle()
        paint.typeface = typeFace
        paint.textSize = textSize
        paint.getTextBounds(text, 0, text?.length ?: 0, textBounds)
        return textBounds
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)

        heatMapBuilder.getData()?.let { data ->
            buildMeasurements(
                data = data,
                measuredWidth = specWidth,
                measuredHeight = specHeight
            ) { width, height ->
                setMeasuredDimension(
                    resolveSize(width, widthMeasureSpec),
                    resolveSize(height, heightMeasureSpec)
                )
            }
        }
    }

    private fun buildMeasurements(
        data: HeatMapData,
        measuredWidth: Int,
        measuredHeight: Int,
        onMeasured: (Int, Int) -> Unit
    ) {

        val rawSize = (measuredHeight / TimeSpan.MAX_DAYS).toFloat()
        val gapSize = rawSize * HeatMapData.CELL_SIZE_RATIO

        val legendAreaHeight = getLegendAreaMeasurement(rawSize, gapSize)
        val dayAreaWidth = getDayLabelAreaMeasurement(gapSize)
        val monthAreaHeight = getMonthLabelAreaMeasurement(gapSize)

        val matrixHeight = measuredHeight.toFloat() - (legendAreaHeight + monthAreaHeight)
        val viewportWidth = measuredWidth.toFloat() - (dayAreaWidth + gapSize)

        val size = ((matrixHeight - (gapSize * TimeSpan.MAX_DAYS)) / TimeSpan.MAX_DAYS)

        val count = data.getColumnCount()
        val width = dayAreaWidth + (size * count + (gapSize * count)) + gapSize

        measurements = Measurements(
            cellGap = gapSize,
            cellSize = size,
            matrixWidth = width,
            matrixHeight = matrixHeight,
            legendAreaHeight = legendAreaHeight,
            dayLabelAreaWidth = dayAreaWidth,
            monthLabelAreaHeight = monthAreaHeight,
            viewportWidth = viewportWidth,
            viewportHeight = matrixHeight
        )

        onMeasured(width.toInt(), measuredHeight)
    }

    private fun getLegendAreaMeasurement(rawSize: Float, gapSize: Float): Float {
        val legendAreaTextHeight = if (calHeatMapOptions.showLegend) {
            val textMeasurement = getTextMeasurement(
                paint = shapeManager.paint,
                text = calHeatMapOptions.legendLessLabel,
                textSize = calHeatMapStyle.legendLabelStyle.textSize,
                typeFace = calHeatMapStyle.legendLabelStyle.typeFace
            )
            textMeasurement.height() + (gapSize * 2)
        } else MIN_OFFSET

        return if (calHeatMapOptions.showLegend) {
            max(legendAreaTextHeight, rawSize)
        } else MIN_OFFSET
    }

    private fun getDayLabelAreaMeasurement(gapSize: Float): Float {
        return if (calHeatMapOptions.showDayLabels && calHeatMapOptions.dayLabels.any { it.active }) {
            val textMeasurement = calHeatMapOptions.dayLabels.mapIndexed { _, label ->
                getTextMeasurement(
                    paint = shapeManager.paint,
                    text = label.text,
                    textSize = calHeatMapStyle.dayLabelStyle.textSize,
                    typeFace = calHeatMapStyle.dayLabelStyle.typeFace
                )
            }.maxBy { it.width() }
            (textMeasurement?.width()?.toFloat() ?: MIN_OFFSET) + gapSize
        } else MIN_OFFSET
    }

    private fun getMonthLabelAreaMeasurement(gapSize: Float): Float {
        return if (calHeatMapOptions.showMonthLabels && calHeatMapOptions.monthLabels.any { it.active }) {
            val textMeasurement = getTextMeasurement(
                paint = shapeManager.paint,
                text = calHeatMapOptions.monthLabels.mapIndexed { _, it -> it.text }.first { it.any { text -> Character.isUpperCase(text) } },
                textSize = calHeatMapStyle.monthLabelStyle.textSize,
                typeFace = calHeatMapStyle.monthLabelStyle.typeFace
            )
            textMeasurement.height() + (gapSize * 2)
        } else gapSize * 2
    }

    override var onFullyVisible: ((CalHeatMap) -> Unit)? = null

    override fun fullyVisible(): Boolean = fullyVisible

    private fun observeVisibility(): ViewGroup? {
        val viewBounds = Rect()
        val scrollBounds = Rect()

        val scrollingParent = findScrollParent(this.parent as ViewGroup) {
            it !is NestedScrollView && it is ScrollView || it is NestedScrollView
        }

        if (scrollingParent == null) {
            retrieveBounds(viewBounds, scrollBounds, this.findMaster())
            notifyVisibility(viewBounds, scrollBounds)
        } else {
            retrieveBounds(viewBounds, scrollBounds, scrollingParent as ViewGroup)
            notifyVisibility(viewBounds, scrollBounds)
        }
        scrollingParent?.let { parent ->
            when (parent) {
                is ScrollView -> {
                    parent.viewTreeObserver.addOnScrollChangedListener {
                        retrieveBounds(viewBounds, scrollBounds, parent)
                        notifyVisibility(viewBounds, scrollBounds)
                    }
                }
                is NestedScrollView -> {
                    parent.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
                        retrieveBounds(viewBounds, scrollBounds, parent)
                        notifyVisibility(viewBounds, scrollBounds)
                    }
                }
            }
        }
        return scrollingParent as? ViewGroup?
    }

    private fun retrieveBounds(
        viewBounds: Rect,
        scrollBounds: Rect,
        parent: ViewGroup?,
        modifyBounds: Boolean = false
    ) {
        getDrawingRect(viewBounds)
        parent?.offsetDescendantRectToMyCoords(this, viewBounds)
        parent?.getDrawingRect(scrollBounds)

        if (modifyBounds) {
            viewBounds.top = scrollBounds.top
            viewBounds.bottom = scrollBounds.bottom
        }
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

            retrieveBounds(viewBounds, scrollBounds, scrollingParent as? ViewGroup, modifyBounds = true)

            getDrawingRect(viewBounds)
            (scrollingParent as? ViewGroup)?.offsetDescendantRectToMyCoords(this@CalHeatMapView, viewBounds)

            viewBounds.top = viewBounds.top - scrollBounds.top
            viewBounds.bottom = scrollBounds.bottom

            hapticFeeback(HapticFeedbackConstants.LONG_PRESS)

            shapeManager.delegateLongPressEvent(event, event.x, event.y, viewBounds)

            interactive = true
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
            if (animationCollection.size <= 0 && interactive) {
                invalidate()
            }

            shapeManager.delegateTouchEvent(event, event.x, event.y, viewBounds)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    performClick()
                    parent.requestDisallowInterceptTouchEvent(false)
                    interactive = false
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    hapticFeeback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
                }
            }
            result
        }
    }
}
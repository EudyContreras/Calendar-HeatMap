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
import android.view.animation.OvershootInterpolator
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.MainThread
import androidx.core.widget.NestedScrollView
import androidx.databinding.ViewDataBinding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.*
import com.eudycontreras.calendarheatmaplibrary.animations.AnimationEvent
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.extensions.findMaster
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager
import com.eudycontreras.calendarheatmaplibrary.common.DrawOverlay
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate
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

interface CalHeatMap: ValueAnimator.AnimatorUpdateListener {
    fun startAnimation()
    fun stopAnimation()
    fun hapticFeeback(feedback: Int)
    fun fullyVisible(): Boolean
    fun addAnimation(animation: AnimationEvent?)
    fun removeAnimation(animation: AnimationEvent?)
    var animationCollection: MutableList<AnimationEvent>
    var onFullyVisible: ((CalHeatMap, Boolean) -> Unit)?
}

@MainThread
class CalHeatMapView : View, CalHeatMap {

    private var sizeRatio = 0.75f

    private var animStarted: Boolean = false
    private var fullyVisible: Boolean = false

    private var infoViewBinding: ViewDataBinding? = null

    private val viewBounds: Rect = Rect()
    private val scrollBounds: Rect = Rect()

    private var drawOverlayView: DrawOverlay? = null
    private var scrollingParent: ViewParent? = null

    private var measurements: Measurements = Measurements()

    private var calHeatMapStyle: HeatMapStyle = HeatMapStyle()
    private var calHeatMapOptions: HeatMapOptions = HeatMapOptions()

    override var animationCollection: MutableList<AnimationEvent> = mutableListOf()
    private var animationProposals: MutableList<AnimationEvent> = mutableListOf()
    private var animationRemovals: MutableList<AnimationEvent> = mutableListOf()

    private var infiniteAnimator: ValueAnimator? = ValueAnimator.ofFloat(MAX_OFFSET, MIN_OFFSET)

    private var shapeManager: ShapeManager = ShapeManager()

    private var cellBubbleLayout: (View, ViewGroup, DrawOverlay?, (WeekDay) -> Unit) -> BubbleLayout<WeekDay> = { bubbleView, parent, drawOverlay, listener ->
        object : BubbleLayout<WeekDay> {

            override val x: Float
                get() = bubbleView.translationX

            override val y: Float
                get() = bubbleView.translationY

            override val scaleX: Float
                get() = bubbleView.scaleX

            override val scaleY: Float
                get() = bubbleView.scaleY

            override val width: Float
                get() = bubbleView.measuredWidth.toFloat()

            override val height: Float
                get() = bubbleView.measuredHeight.toFloat()

            override val boundsWidth: Float
                get() = parent.measuredWidth.toFloat()

            override val boundsHeight: Float
                get() = parent.measuredHeight.toFloat()

            override val elevation: Float
                get() = bubbleView.elevation

            override val drawOverlay: DrawOverlay?
                get() = drawOverlay

            override fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long) {
                bubbleView.pivotX = pivotX
                bubbleView.pivotY = pivotY
                bubbleView.scaleX = 0.75f
                bubbleView.scaleY = 0.75f
                bubbleView.animate()
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(duration)
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()
            }

            override fun reveal(offset: Float, pivot: Coordinate, duration: Long) {
                if (bubbleView.visibility != VISIBLE) {
                    bubbleView.visibility = VISIBLE
                }
                bubbleView.pivotX = pivot.x
                bubbleView.pivotY = pivot.y
                bubbleView.scaleX = 0.25f
                bubbleView.scaleY = 0.25f
                bubbleView.animate()
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(duration)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .start()
            }

            override fun conceal(offset: Float, pivot: Coordinate, duration: Long) {
                bubbleView.pivotX = pivot.x
                bubbleView.pivotY = pivot.y
                bubbleView.animate()
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(duration)
                    .alpha(0f)
                    .scaleX(0.4f)
                    .scaleY(0.4f)
                    .start()
            }

            override fun onDataIntercepted(data: WeekDay) {
                listener(data)
            }

            override fun onMove(x: Float, y: Float) {
                bubbleView.x = x
                bubbleView.y = y
            }

            override fun onRender(canvas: Canvas) {
                bubbleView.draw(canvas)
            }
        }
    }

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

    private fun setUpAttributes(typedArray: TypedArray) {
        val defaultColor = MutableColor(AndroidColor.LTGRAY).toColor()
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

    fun setDrawOverlayView(drawOverlayView: DrawOverlay) {
        this.drawOverlayView = drawOverlayView
        this.drawOverlayView?.setRenderData(shapeManager.renderData)
    }

    fun setRevealOnVisible(revealOnVisible: Boolean) {

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

        val cellBubbleLayout: BubbleLayout<WeekDay>? = infoViewBinding?.run {
            val parent = this.root.parent as? ViewGroup ?: return
            this@CalHeatMapView.cellBubbleLayout(root, parent, drawOverlayView) {
                setVariable(VIEWMODEL, it)
            }.apply {
                conceal(MIN_OFFSET, Coordinate(MIN_OFFSET, MIN_OFFSET),0L)
            }
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

        observeVisibility()

        if (scrollingParent == null){
            fullyVisible = true
            onFullyVisible?.invoke(this, fullyVisible)
            return
        }

        if (!animStarted) {
            startAnimation()
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
        val cellSize = data.cellSize

        val gapSize = data.cellGap ?: if (cellSize != null) {
            cellSize * HeatMapData.CELL_SIZE_RATIO
        } else {
            (measuredHeight / TimeSpan.MAX_DAYS) * HeatMapData.CELL_SIZE_RATIO
        }

        val legendAreaHeight = getLegendAreaMeasurement(gapSize)
        val dayAreaWidth = getDayLabelAreaMeasurement(gapSize)
        val monthAreaHeight = getMonthLabelAreaMeasurement(gapSize)

        val matrixHeight = measuredHeight.toFloat() - (legendAreaHeight + monthAreaHeight)
        val viewportWidth = measuredWidth.toFloat() - (dayAreaWidth + gapSize)

        val size = data.cellSize ?: ((matrixHeight - (gapSize * TimeSpan.MAX_DAYS)) / TimeSpan.MAX_DAYS)

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

    private fun getLegendAreaMeasurement(gapSize: Float): Float {
        val legendAreaTextHeight = if (calHeatMapOptions.showLegend) {
            val textMeasurement = getTextMeasurement(
                paint = shapeManager.paint,
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
        return if (calHeatMapOptions.showMonthLabels) {
            val textMeasurement = getTextMeasurement(
                paint = shapeManager.paint,
                text = calHeatMapOptions.monthLabels.mapIndexed { _, it -> it.text }.first { it.any { text -> Character.isUpperCase(text) } },
                textSize = calHeatMapStyle.monthLabelStyle.textSize,
                typeFace = calHeatMapStyle.monthLabelStyle.typeFace
            )
            textMeasurement.height() + (gapSize * 2)
        } else MIN_OFFSET
    }

    override var onFullyVisible: ((CalHeatMap, Boolean) -> Unit)? = null

    override fun fullyVisible(): Boolean = fullyVisible

    private fun observeVisibility() {
        scrollingParent = findScrollParent(this.parent as ViewGroup) {
            it !is NestedScrollView && it is ScrollView || it is NestedScrollView
        }

        if (scrollingParent == null) {
            retrieveBounds(viewBounds, scrollBounds, this.findMaster())
            notifyVisibility(viewBounds, scrollBounds)
        }
        scrollingParent?.let { parent ->
            retrieveBounds(viewBounds, scrollBounds, parent as ViewGroup)
            notifyVisibility(viewBounds, scrollBounds)

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
    }

    private fun retrieveBounds(
        viewBounds: Rect,
        scrollBounds: Rect,
        parent: ViewGroup?
    ) {
        getDrawingRect(viewBounds)
        parent?.offsetDescendantRectToMyCoords(this, viewBounds)
        parent?.getDrawingRect(scrollBounds)
        viewBounds.top = scrollBounds.top
        viewBounds.bottom = scrollBounds.bottom
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
                onFullyVisible?.invoke(this, fullyVisible)
            }
        } else {
            fullyVisible = false
            onFullyVisible?.invoke(this, false)
        }
    }

    private val detector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(event: MotionEvent) {
            super.onLongPress(event)
            parent.requestDisallowInterceptTouchEvent(true)

            retrieveBounds(viewBounds, scrollBounds, scrollingParent as? ViewGroup)

            getDrawingRect(viewBounds)
            (scrollingParent as? ViewGroup)?.offsetDescendantRectToMyCoords(this@CalHeatMapView, viewBounds)

            viewBounds.top = viewBounds.top - scrollBounds.top
            viewBounds.bottom = scrollBounds.bottom

            hapticFeeback(HapticFeedbackConstants.LONG_PRESS)

            shapeManager.delegateLongPressEvent(event, event.x, event.y, viewBounds)

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
            if (animationCollection.size <= 0) {
                invalidate()
            }

            shapeManager.delegateTouchEvent(event, event.x, event.y, viewBounds)

            when (event.action) {
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    performClick()
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            if (event.action == MotionEvent.ACTION_UP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    hapticFeeback(HapticFeedbackConstants.KEYBOARD_RELEASE)
                }
            }
            result
        }
    }
}
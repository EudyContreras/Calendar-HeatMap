package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.*
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.DrawOverlay
import com.eudycontreras.calendarheatmaplibrary.extensions.clamp
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Bubble
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MIN_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

class BubbleLayoutView: FrameLayout, BubbleLayout {
    private var dirty: Boolean = false

    internal var overlay: DrawOverlay? = null

    internal var dataListener: ((Any) -> Unit)? = null

    override var bubbleColor: Int = AndroidColor.TRANSPARENT
    override var bubblePointerLength: Float = DEFAULT_BUBBLE_POINTER_LENGTH
    override var bubbleCornerRadius: Float = DEFAULT_BUBBLE_CORNER_RADIUS

    private val bubble: Bubble by lazy {
        Bubble().also {
            it.render = false
            it.pointerOffset = MIN_OFFSET
            it.elevation = elevation
            it.shadowAlphaOffset = 1.58f
            it.maxShadowRadius = 25f
            it.cornerRadius = bubbleCornerRadius
            it.pointerLength = bubblePointerLength
            it.bounds = Bounds().apply {
                this.width = bubbleWidth
                this.height = bubbleHeight
            }
            it.color = MutableColor.fromColor(bubbleColor)
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayoutView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
        doOnLayout {
            this.visibility = View.INVISIBLE
            if (overlay == null) {
                attachOverlay {  }
            }
        }
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        this.bubbleColor = typedArray.getColor(R.styleable.BubbleLayoutView_bubbleColor, AndroidColor.TRANSPARENT)
        this.bubblePointerLength = typedArray.getDimension(R.styleable.BubbleLayoutView_bubblePointerLength, DEFAULT_BUBBLE_POINTER_LENGTH)
        this.bubbleCornerRadius = typedArray.getDimension(R.styleable.BubbleLayoutView_bubbleCornerRadius, DEFAULT_BUBBLE_CORNER_RADIUS)

        this.background = GradientDrawable().apply {
            this.shape = GradientDrawable.RECTANGLE
            this.color = ColorStateList.valueOf(bubbleColor)
            this.cornerRadius = bubbleCornerRadius
        }
    }

    fun attachOverlay(onAttach: DrawOverlay.() -> Unit) {
        if (parent !is BubbleLayoutContainer) {
            throw IllegalStateException("The parent of a BubbleLayout must be BubbleLayoutContainer")
        } else {
            overlay = parent as BubbleLayoutContainer
            overlay?.let(onAttach)
            overlay?.registerDrawTarget { canvas, renderData, invalidator ->
                bubble.onRender(canvas, renderData.paint, renderData.shapePath, renderData.shadowPath)
                if (dirty) {
                    invalidator()
                    dirty = false
                }
            }
        }
    }

    override val bubbleX: Float
        get() = this.translationX

    override val bubbleY: Float
        get() = this.translationY

    override val bubbleScaleX: Float
        get() = this.scaleX

    override val bubbleScaleY: Float
        get() = this.scaleY

    override val bubbleWidth: Float
        get() = this.measuredWidth.toFloat()

    override val bubbleHeight: Float
        get() = this.measuredHeight.toFloat()

    override val drawOverlay: DrawOverlay?
        get() = overlay

    override val boundsWidth: Float
        get() = overlay?.overlayWidth ?: MIN_OFFSET

    override val boundsHeight: Float
        get() = overlay?.overlayHeight ?: MIN_OFFSET

    override val bubbleElevation: Float
        get() = this.elevation

    override fun onLayout(delay: Long, action: Action) {
        postDelayed(action, delay)
    }

    override fun setPointerOffset(offset: Float) {
        this.bubble.pointerOffset = offset
    }

    private var adjustAlpha: Boolean = true

    private fun adjustBubble(scaleX: Float = this.scaleX, scaleY: Float = this.scaleY, setPointer: Boolean  = true) {
        if (setPointer) {
            this.bubble.pointerLength = bubblePointerLength * scaleY
        }
        this.bubble.alpha = alpha.clamp(MIN_OFFSET, MAX_OFFSET)

        val bounds = Bounds().apply {
            this.x = bubble.x
            this.y = bubbleY + ((bubbleHeight * 2) + (bubble.pointerLength / 2))
            this.width = bubbleWidth * scaleX
            this.height = bubbleHeight * scaleY
        }

        this.bubble.bounds = bounds
        this.dirty = true
        this.overlay?.reDraw()
    }

    override fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long) {
        this.pivotX = pivotX
        this.pivotY = pivotY
        this.scaleX = 0.90f
        this.scaleY = 0.90f
        this.animate()
            .setUpdateListener {
                this.adjustBubble(MAX_OFFSET, MAX_OFFSET)
            }
            .setInterpolator(OvershootInterpolator())
            .setDuration(duration)
            .scaleX(MAX_OFFSET)
            .scaleY(MAX_OFFSET)
            .start()
    }

    override fun reveal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        with(this.background as GradientDrawable) {
            this.alpha = MIN_COLOR
        }

        this.bubble.render = true
        this.adjustAlpha = true
        this.elevation = MIN_OFFSET
        this.alpha = MIN_OFFSET
        this.pivotX = pivot.x
        this.pivotY = pivot.y
        this.scaleX = 0.15f
        this.scaleY = 0.15f

        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }

        this.animate()
            .setUpdateListener {
                this.adjustBubble()
            }
            .withStartAction {
                this.adjustBubble()
            }
            .withEndAction(onDone)
            .setInterpolator(OvershootInterpolator())
            .setDuration(duration)
            .scaleX(MAX_OFFSET)
            .scaleY(MAX_OFFSET)
            .alpha(MAX_OFFSET)
            .start()
    }

    override fun conceal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        this.adjustAlpha = true
        this.bubble.alpha = MAX_OFFSET

        this.pivotX = pivot.x
        this.pivotY = pivot.y

        this.animate()
            .setUpdateListener {
                this.adjustBubble()
            }
            .withEndAction(onDone)
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(duration)
            .alpha(MIN_OFFSET)
            .scaleX(0.2f)
            .scaleY(0.3f)
            .start()
    }

    override fun onMove(x: Float, y: Float, bubbleX: Float, offsetX: Float, offsetY: Float) {
        this.x = x + offsetX
        this.y = y + offsetY

        this.bubble.x = bubbleX + offsetX
        this.bubble.y = bubbleY + ((bubbleHeight * 2) + (bubble.pointerLength / 2))

        this.dirty = true
        this.overlay?.reDraw()
    }

    override fun onDataIntercepted(data: Any) {
        dataListener?.invoke(data)
    }

    override fun setDataInteceptListener(dataListener: (Any) -> Unit) {
        this.dataListener = dataListener
    }

    companion object {
        val DEFAULT_BUBBLE_POINTER_LENGTH = 15.dp
        val DEFAULT_BUBBLE_CORNER_RADIUS = 6.dp
    }
}
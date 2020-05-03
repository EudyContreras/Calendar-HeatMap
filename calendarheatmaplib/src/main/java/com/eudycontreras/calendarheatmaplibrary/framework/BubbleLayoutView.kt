package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.view.doOnLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.R
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.DrawOverlay
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Bubble
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DropShadow
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
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
    internal var overlay: DrawOverlay? = null

    internal var dataListener: ((Any) -> Unit)? = null

    private var bubbleColor: Int = AndroidColor.TRANSPARENT
    private var bubblePointerLength: Float = 15.dp
    private var bubbleCornerRadius: Float = 6.dp

    private val bubble: Bubble by lazy {
        Bubble().also {
            it.pointerOffset = 0f
            it.cornerRadius = bubbleCornerRadius
            it.pointerLength = bubblePointerLength
            it.bounds = Bounds().apply {
                this.width = bubbleWidth
                this.height = bubbleHeight
            }
            it.color = MutableColor.fromColor(bubbleColor)
        }
    }

    private val shadow: DropShadow by lazy {
        DropShadow(bubbleElevation).also {
            it.bounds = Bounds(
                left = bubbleX,
                top = bubbleY,
                right = bubbleX + bubbleWidth,
                bottom = bubbleY + bubbleHeight
            )
            it.elevation = elevation
            it.render = false
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
            if (overlay == null) {
                attachOverlay {  }
            }
        }
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        this.bubbleColor = typedArray.getColor(R.styleable.BubbleLayoutView_bubbleColor, AndroidColor.TRANSPARENT)
        this.bubblePointerLength = typedArray.getDimension(R.styleable.BubbleLayoutView_bubblePointerLength, 15.dp)
        this.bubbleCornerRadius = typedArray.getDimension(R.styleable.BubbleLayoutView_bubbleCornerRadius, 4.dp)


    }

    fun attachOverlay(onAttach: DrawOverlay.() -> Unit) {
        if (parent !is BubbleLayoutContainer) {
            throw IllegalStateException("The parent of a BubbleLayout must be BubbleLayoutContainer")
        } else {
            overlay = parent as BubbleLayoutContainer
            overlay?.let(onAttach)
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

    override fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long) {
        this.pivotX = pivotX
        this.pivotY = pivotY
        this.scaleX = 0.75f
        this.scaleY = 0.75f
        this.animate()
            .setInterpolator(OvershootInterpolator())
            .setDuration(duration)
            .scaleX(1f)
            .scaleY(1f)
            .start()
    }

    override fun reveal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
        this.pivotX = pivot.x
        this.pivotY = pivot.y
        this.scaleX = 0.25f
        this.scaleY = 0.25f
        this.animate()
            .withEndAction(onDone)
            .setInterpolator(OvershootInterpolator())
            .setDuration(duration)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .start()
    }

    override fun conceal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        this.pivotX = pivot.x
        this.pivotY = pivot.y
        this.animate()
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(duration)
            .alpha(0f)
            .scaleX(0.4f)
            .scaleY(0.4f)
            .start()
    }

    override fun onMove(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun onDataIntercepted(data: Any) {
        dataListener?.invoke(data)
    }

    override fun setDataInteceptListener(dataListener: (Any) -> Unit) {
        this.dataListener = dataListener
    }

    companion object {

    }
}
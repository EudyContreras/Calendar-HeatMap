package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.R
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.DrawOverlay
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Bubble
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DropShadow
import com.eudycontreras.calendarheatmaplibrary.framework.data.WeekDay
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

class BubbleWrapperView: FrameLayout, BubbleLayout<WeekDay> {

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleWrapperView)
        try {
            setUpAttributes(typedArray)
        } finally {
            typedArray.recycle()
        }
    }

    private fun setUpAttributes(typedArray: TypedArray) {
        this.bubbleColor = typedArray.getColor(R.styleable.BubbleWrapperView_bubbleColor, AndroidColor.TRANSPARENT)
        this.bubblePointerLength = typedArray.getDimension(R.styleable.BubbleWrapperView_bubblePointerLength, 15.dp)
        this.bubbleCornerRadius = typedArray.getDimension(R.styleable.BubbleWrapperView_bubbleCornerRadius, 4.dp)

        this.setBackgroundColor(bubbleColor)

        this.background = GradientDrawable().apply {
            this.shape = GradientDrawable.RECTANGLE
            this.color = ColorStateList.valueOf(bubbleColor)
            this.cornerRadius = bubbleCornerRadius
        }
    }

    override val bubbleX: Float
        get() = super.getX()

    override val bubbleY: Float
        get() = super.getY()

    override val bubbleScaleX: Float
        get() = super.getScaleX()

    override val bubbleScaleY: Float
        get() = super.getScaleY()

    override val bubbleWidth: Float
        get() = super.getMeasuredWidth().toFloat()

    override val bubbleHeight: Float
        get() = super.getMeasuredHeight().toFloat()

    override val drawOverlay: DrawOverlay?
        get() = TODO("Not yet implemented")

    override val boundsWidth: Float
        get() = TODO("Not yet implemented")

    override val boundsHeight: Float
        get() = TODO("Not yet implemented")

    override val bubbleElevation: Float
        get() = TODO("Not yet implemented")

    override fun onLayout(delay: Long, action: Action) {
        TODO("Not yet implemented")
    }

    override fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long) {
        TODO("Not yet implemented")
    }

    override fun reveal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        TODO("Not yet implemented")
    }

    override fun conceal(offset: Float, pivot: Coordinate, duration: Long, onDone: Action?) {
        TODO("Not yet implemented")
    }

    override fun onMove(x: Float, y: Float) {
        TODO("Not yet implemented")
    }

    override fun onDataIntercepted(data: WeekDay) {
        TODO("Not yet implemented")
    }

    companion object {

    }
}
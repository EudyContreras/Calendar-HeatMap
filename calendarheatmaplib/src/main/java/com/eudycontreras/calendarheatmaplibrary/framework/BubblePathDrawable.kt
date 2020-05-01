package com.eudycontreras.calendarheatmaplibrary.framework

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.addRoundRect
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.toBounds
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Bubble
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.CornerRadii
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import com.eudycontreras.calendarheatmaplibrary.tryGet

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

class BubblePathDrawable : GradientDrawable() {

    private val shapePath: Path = Path()

    private val shadowPath: Path = Path()

    internal val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    init {
        this.shape = RECTANGLE
    }

    private val bubble: Bubble by lazy {
        Bubble().also {
            it.cornerRadius = 4.dp
            it.pointerLength = 4.dp
            it.pointerOffset = 0f
            it.strokeWidth = 0.dp
            it.elevation = 0f
            it.bounds = this.bounds.toBounds()
            it.color = MutableColor.fromHexString("#3C3C3C")
        }
    }

    private val self: GradientDrawable
        get() = this

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeProperties(drawable: Drawable) {
        when (drawable) {
            is GradientDrawable -> {
                (self.mutate() as GradientDrawable).apply {
                    val corners = tryGet { drawable.cornerRadii?.let { CornerRadii(it) } } ?: CornerRadii(drawable.cornerRadius)
                    this.shape = drawable.shape
                    this.alpha = drawable.alpha
                    this.color = drawable.color
                    this.colors = drawable.colors
                    this.cornerRadii = corners.corners
                    this.gradientRadius = drawable.gradientRadius
                    this.gradientType = drawable.gradientType
                    invalidateSelf()
                }
            }
        }
    }

    fun setPointer(x: Float, y: Float, minInX: Float = -1f, maxInX: Float = -1f) {
        bubble.x = x
        bubble.y = y
        bubble.pointerOffset = mapRange(x, minInX, maxInX, MIN_OFFSET, MAX_OFFSET)

        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        bubble.bounds = this.copyBounds().toBounds().copy(
            bottom = this.copyBounds().bottom - bubble.pointerLength
        )
        bubble.onRender(canvas, paint, shapePath, shadowPath)

        invalidateSelf()
    }

    @Synchronized
    override fun setAlpha(alpha: Int) { }

    @Synchronized
    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    @Synchronized
    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getOutline(outline: Outline) {
        when (shape) {
            RECTANGLE -> {
                outline.setConvexPath(shapePath)
            }
            OVAL -> {
                outline.setOval(bounds)
            }
            else -> {}
        }
    }
}
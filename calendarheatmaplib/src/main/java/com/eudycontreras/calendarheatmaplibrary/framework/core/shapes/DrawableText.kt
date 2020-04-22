package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.*
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.extensions.sp
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.data.Alignment
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class DrawableText(
    val text: String,
    val paint: Paint
) : DrawableShape() {

    private var boundsDirty: Boolean = true

    var textBounds: Rect = Rect()

    var textSize: Float = STANDARD_TEXT_SIZE.sp
        set(value) {
            field = value
            boundsDirty = true
        }

    var textColor: MutableColor
        get() = color
        set(value) {
            color = value
        }

    var typeFace: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            boundsDirty = true
        }

    var alignment: Alignment = Alignment.LEFT
        set(value) {
            field = value
            boundsDirty = true
        }

    fun build(): DrawableText {
        paint.recycle()
        paint.typeface = typeFace
        paint.textSize = textSize
        paint.color = textColor.toColor()
        paint.textAlign = alignment.getTextAlignment()
        paint.getTextBounds(text, 0, text.length, this.textBounds)
        width = textBounds.width().toFloat()
        height = textBounds.height().toFloat()
        return this
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (render) {
            if (boundsDirty) {
                build()
                boundsDirty = false
            }
            canvas.drawText(text, x, y, this.paint)
        }
    }

    fun copyStyle(other: DrawableText) {
        this.textSize = other.textSize
        this.typeFace = other.typeFace
        this.textColor = other.textColor
        this.alignment = other.alignment
    }

    private fun Alignment?.getTextAlignment(): Paint.Align {
        return when(this) {
            Alignment.RIGHT -> Paint.Align.RIGHT
            Alignment.LEFT -> Paint.Align.LEFT
            else -> Paint.Align.CENTER
        }
    }

    companion object {
        const val STANDARD_TEXT_SIZE: Float = 12f
    }
}
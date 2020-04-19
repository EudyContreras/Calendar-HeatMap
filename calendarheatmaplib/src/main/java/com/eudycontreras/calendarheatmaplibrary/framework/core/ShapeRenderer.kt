package com.eudycontreras.calendarheatmaplibrary.framework.core

import android.graphics.*
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

class ShapeRenderer {

    private val shapePath: Path = Path()

    private val shadowPath: Path = Path()

    private val shapes = ArrayList<DrawableShape>()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    var renderCapsule: ((Path, Paint, Canvas) -> Unit)? = null

    var renderShapes: Boolean = true

    var renderBounds: Bounds = Bounds()

    fun setBounds(bounds: Bounds) {
        this.renderBounds = bounds
    }

    fun renderShapes(canvas: Canvas) {
        for (shape in shapes) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }

        paint.style = Paint.Style.FILL
        paint.color = 0xff0000

        canvas.drawRoundRect(renderBounds.left, renderBounds.top, renderBounds.right, renderBounds.bottom, 0f, 0f,  paint)
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        for (shape in shapes) {
            if (shape is TouchableShape && shape.touchProcessor != null) {
                shape.onTouch(motionEvent, x, y)
            }
        }
    }

    fun delegateLongPressEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onLongPressed(motionEvent, x, y)
            }
        }
    }
}
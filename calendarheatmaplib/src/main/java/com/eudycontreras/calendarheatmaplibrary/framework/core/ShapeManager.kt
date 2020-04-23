package com.eudycontreras.calendarheatmaplibrary.framework.core

import android.graphics.*
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchConsumer
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class ShapeManager {

    private val shapePath: Path = Path()

    private val shadowPath: Path = Path()

    private val shapes = ArrayList<RenderTarget>()

    internal val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    var renderShapes: Boolean = true

    fun <T : RenderTarget> addShape(shape: T?) {
        shape?.let { shapes.add(it) }
    }

    fun <T : RenderTarget> addShape(shapes: Array<T>?) {
        shapes?.let { this.shapes.addAll(it) }
    }

    fun <T : RenderTarget> addShape(shapes: List<T>?) {
        shapes?.let { this.shapes.addAll(it) }
    }

    fun <T : RenderTarget> removeShape(shape: T?) {
        shape?.let { shapes.remove(it) }
    }

    fun <T : RenderTarget> removeShape(vararg shape: T) {
        shapes.removeAll(shape)
    }

    fun renderShapes(canvas: Canvas) {
        if (!renderShapes) return

        for (shape in shapes) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onTouch(motionEvent, x, y, this)
            }
        }
    }

    fun delegateTouchEvent(motionEvent: MotionEvent, bounds: Bounds, x: Float, y: Float, caller: TouchableShape) {
        for (shape in shapes) {
            if (shape is TouchConsumer && shape != caller) {
                shape.onTouch(motionEvent, bounds, x, y)
            }
        }
    }

    fun delegateLongPressEvent(motionEvent: MotionEvent, x: Float, y: Float) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onLongPressed(motionEvent, x, y, this)
            }
        }
    }
}
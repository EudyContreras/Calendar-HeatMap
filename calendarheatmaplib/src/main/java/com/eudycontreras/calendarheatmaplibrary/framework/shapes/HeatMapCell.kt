package com.eudycontreras.calendarheatmaplibrary.framework.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.extensions.addRoundRect
import com.eudycontreras.calendarheatmaplibrary.extensions.addShadowBounds
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.utilities.Shadow

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class HeatMapCell : DrawableShape(), TouchableShape {

    private var shadowFilter: BlurMaskFilter? = null

    override fun build() {
        shadowFilter = Shadow.getShadowFilter(elevation)
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float) {
        touchProcessor?.invoke(this, event, x, y)
    }

    override fun onLongPressed(event: MotionEvent, x: Float, y: Float) {}

    override fun onHovered(event: MotionEvent, x: Float, y: Float) {}

    override fun onRender(
        canvas: Canvas,
        paint: Paint,
        shapePath: Path,
        shadowPath: Path
    ) {
        if (!render) {
            return
        }

        if (drawShadows) {
            renderShadow(canvas, paint, shadowPath)
        }

        renderShape(canvas, paint, shapePath)

        if (showStroke) {
            strokeColor?.let {
                renderStroke(canvas, paint, shapePath, it)
            }
        }
    }


    private fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
        paint.recycle()
        paint.shader = null
        paint.maskFilter = shadowFilter
        paint.color = shadowColor?.toColor() ?: Shadow.DEFAULT_COLOR

        shadowPath.rewind()
        shadowPath.addShadowBounds(bounds, radii, elevation)

        canvas.drawPath(shadowPath, paint)
    }

    private fun renderShape(canvas: Canvas, paint: Paint, shapePath: Path) {
        paint.recycle()
        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        if (shader != null) {
            paint.shader = shader
        }

        shapePath.rewind()
        shapePath.addRoundRect(bounds, radii)

        canvas.drawPath(shapePath, paint)
    }

    private fun renderStroke(canvas: Canvas, paint: Paint, shapePath: Path, stroke: Color) {
        paint.recycle()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = stroke.toColor()

        shapePath.rewind()
        canvas.drawPath(shapePath, paint)
    }
}
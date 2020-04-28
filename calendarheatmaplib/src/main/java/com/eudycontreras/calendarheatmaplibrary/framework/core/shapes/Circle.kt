package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.addCircle
import com.eudycontreras.calendarheatmaplibrary.extensions.addShadowOval
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal open class Circle : DrawableShape() {

    var centerX: Float = MIN_OFFSET
        set(value) {
            field = value
            x = value - (radius / 2)
        }

    var centerY: Float = MIN_OFFSET
        set(value) {
            field = value
            y = value - (radius / 2)
        }

    var radius: Float = MIN_OFFSET
        get() = bounds.radius
        set(value) {
            field = value
            width = value
            height = value
        }

    private var shadowFilter: BlurMaskFilter? = null

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (!render) {
            return
        }

        if (drawShadows) {
            renderShadow(canvas, paint, shadowPath)
        }

        paint.recycle()
        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        if (shader != null) {
            paint.shader = shader
        }
        
        shapePath.rewind()
        shapePath.addCircle(centerX, centerY, radius)
        canvas.drawPath(shapePath, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.recycle()
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                shapePath.addCircle(centerX, centerY, radius)
                canvas.drawPath(shapePath, paint)
            }
        }
    }

    protected open fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
        if (shadowColor == null) {
            val color = ShadowUtility.COLOR
            this.shadowAlpha = color.alpha
            this.shadowColor = ShadowUtility.getShadowColor(color, elevation)
            this.shadowFilter = ShadowUtility.getShadowFilter(elevation)
        }
        paint.recycle()
        paint.shader = null
        paint.maskFilter = shadowFilter
        paint.color = shadowColor?.toColor() ?: ShadowUtility.DEFAULT_COLOR

        shadowPath.rewind()
        shadowPath.addShadowOval(centerX, centerY, radius, elevation)

        canvas.drawPath(shadowPath, paint)
    }
}
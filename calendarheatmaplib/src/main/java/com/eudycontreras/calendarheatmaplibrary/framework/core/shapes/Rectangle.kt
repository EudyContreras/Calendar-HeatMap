package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.extensions.addRoundRect
import com.eudycontreras.calendarheatmaplibrary.extensions.addShadowBounds
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal open class Rectangle : DrawableShape() {

    protected open var shadowFilter: BlurMaskFilter? = null

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

    protected open fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
        if (shadowColor == null) {
            val color = ShadowUtility.COLOR
            this.shadowAlpha = color.alpha
            this.shadowColor = ShadowUtility.getShadowColor(color, elevation)
            this.shadowFilter = ShadowUtility.getShadowFilter(elevation)
        }
        paint.recycle()
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.maskFilter = shadowFilter
        paint.color = shadowColor?.toColor() ?: ShadowUtility.DEFAULT_COLOR

        shadowPath.rewind()
        shadowPath.addShadowBounds(bounds, radii, elevation)

        canvas.drawPath(shadowPath, paint)
    }

    protected open fun renderShape(canvas: Canvas, paint: Paint, shapePath: Path) {
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

    protected open fun renderStroke(canvas: Canvas, paint: Paint, shapePath: Path, stroke: Color) {
        paint.recycle()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = stroke.toColor()

        canvas.drawPath(shapePath, paint)
    }
}
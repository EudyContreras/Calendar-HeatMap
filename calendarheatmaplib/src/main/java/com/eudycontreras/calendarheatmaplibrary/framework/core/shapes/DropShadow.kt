package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.addShadowBounds
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
internal open class DropShadow(
    override var elevation: Float
) : DrawableShape() {

    var scaleX: Float = MAX_OFFSET
    var scaleY: Float = MAX_OFFSET

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
    }

    protected open fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
        if (shadowColor == null) {
            val color = ShadowUtility.getShadowColor(ShadowUtility.COLOR, elevation)
            this.shadowAlpha = color?.alpha ?: ShadowUtility.COLOR.alpha
            this.shadowColor = color?.updateAlpha(this.shadowAlpha * 1.044f)
            this.shadowFilter = ShadowUtility.getShadowFilter(elevation)
        }
        paint.recycle()
        paint.shader = null
        paint.maskFilter = shadowFilter
        paint.style = Paint.Style.FILL
        paint.color = shadowColor?.toColor() ?: ShadowUtility.DEFAULT_COLOR

        shadowPath.rewind()

        val offsetX = if (scaleX >= MAX_OFFSET) {
            (bounds.right - bounds.left) * scaleX
        } else {
            (bounds.right - bounds.left) * scaleX
        }
        val offsetY = if (scaleY >= MAX_OFFSET) {
            (bounds.bottom - bounds.top) * scaleY
        } else {
            (bounds.bottom - bounds.top) * scaleY
        }

        shadowPath.addShadowBounds(bounds, radii, elevation,
            left = bounds.left + offsetX,
            right = bounds.right - offsetX,
            top = bounds.top + offsetY,
            bottom = bounds.bottom - offsetY
        )

        canvas.drawPath(shadowPath, paint)
    }
}
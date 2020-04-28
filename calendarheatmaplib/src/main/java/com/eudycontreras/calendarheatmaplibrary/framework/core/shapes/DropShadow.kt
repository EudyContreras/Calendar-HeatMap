package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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
internal open class DropShadow : DrawableShape() {

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
        shadowPath.addShadowBounds(bounds, radii, elevation)

        canvas.drawPath(shadowPath, paint)
    }
}
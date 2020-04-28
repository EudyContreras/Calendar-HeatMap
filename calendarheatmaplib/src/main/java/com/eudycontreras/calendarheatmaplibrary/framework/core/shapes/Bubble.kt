package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.PathPlot
import com.eudycontreras.calendarheatmaplibrary.properties.PathPoint
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Created by eudycontreras.
 */

internal class Bubble: DrawableShape() {

    var pointerOffset = 0.5f

    var pointerWidth = 10.dp

    var pointerLength = 10.dp

    var cornerRadius = 8.dp
        set(value) {
            field = value
            corners.apply(value, value, value, value)
        }

    var contentBounds: Bounds = Bounds()

    private val pathPlot: PathPlot = PathPlot(Path())

    private var shadowFilter: BlurMaskFilter? = null

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (!render) {
            return
        }

        val offsetLeft = 1f * mapRange(pointerOffset, 0f, 0.5f, 0f, 0.5f, 0f, 1f)
        val offsetRight = 1f * mapRange(pointerOffset, 0.5f, 1f, 0.5f, 0f, 0f, 1f)

        if (!pathPlot.pathCreated) {

            pathPlot.width = bounds.width - (cornerRadius * 2)
            pathPlot.height = bounds.height - (cornerRadius * 2)

            pathPlot.contentBounds.width = pathPlot.width + cornerRadius
            pathPlot.contentBounds.height = pathPlot.height + cornerRadius

            val shift = (pathPlot.width - pointerWidth)

            pathPlot.startX = bounds.x
            pathPlot.startY = bounds.y + cornerRadius

            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetLeft), -pointerLength))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -((pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, -cornerRadius, 0f, -cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, -pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, -cornerRadius, cornerRadius, -cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, pathPlot.width, 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, cornerRadius, 0f, cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, 0f, pathPlot.height))
            pathPlot.points.add(PathPoint(PathPlot.Type.QUAD, true, 0f, cornerRadius, -cornerRadius, cornerRadius))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(shift - (pointerOffset * shift)), 0f))
            pathPlot.points.add(PathPoint(PathPlot.Type.LINE, true, -(pointerWidth * offsetRight), pointerLength))

            pathPlot.build()
        }

        pathPlot.startX = bounds.x
        pathPlot.startY = bounds.y + cornerRadius

        val shift = (pathPlot.width - pointerWidth)

        pathPlot.points[0].startX = -(pointerWidth * offsetLeft)
        pathPlot.points[1].startX =  -((pointerOffset * shift))
        pathPlot.points[9].startX = -(shift-(pointerOffset * shift))
        pathPlot.points[10].startX = -(pointerWidth * offsetRight)

        pathPlot.contentBounds.x = bounds.x - ((pathPlot.width/2) + (cornerRadius/2))
        pathPlot.contentBounds.y = bounds.y - (pointerLength + (pathPlot.height + (cornerRadius / 2)))

        contentBounds.centerX = pathPlot.contentBounds.centerX -((pointerOffset * shift))
        contentBounds.centerY = pathPlot.contentBounds.centerY
        contentBounds.height = bounds.height

        pathPlot.build()

        if (drawShadows) {
            renderShadow(canvas, paint, pathPlot.path)
        }

        paint.recycle()
        paint.style = Paint.Style.FILL
        paint.color = color.toColor()

        canvas.drawPath(pathPlot.path, paint)

        if (showStroke) {

            strokeColor?.let {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeWidth
                paint.color = it.toColor()

                canvas.drawPath(pathPlot.path, paint)
            }
        }
    }

    private fun renderShadow(canvas: Canvas, paint: Paint, shadowPath: Path) {
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

        canvas.drawPath(shadowPath, paint)
    }
}
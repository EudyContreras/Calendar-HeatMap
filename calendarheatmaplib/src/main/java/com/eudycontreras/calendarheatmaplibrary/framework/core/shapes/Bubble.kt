package com.eudycontreras.calendarheatmaplibrary.framework.core.shapes

import android.graphics.*
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.recycle
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MAX_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MIN_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.PathCorner
import com.eudycontreras.calendarheatmaplibrary.properties.PathPlot
import com.eudycontreras.calendarheatmaplibrary.properties.PathPoint
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Created by eudycontreras.
 */

internal class Bubble: DrawableShape() {

    var dirty = false

    var pointerOffset = 0.5f

    var pointerWidth = 10.dp

    var pointerLength = 10.dp

    var minShadowRadius: Float = ShadowUtility.MIN_SHADOW_RADIUS
    var maxShadowRadius: Float = ShadowUtility.MAX_SHADOW_RADIUS

    var cornerRadius = 8.dp
        set(value) {
            field = value
            corners.apply(value, value, value, value)
        }

    var contentBounds: Bounds = Bounds()

    override var bounds: Bounds = Bounds()
        set(value) {
            field = value
            dirty = true
        }

    private val pathPlot: PathPlot = PathPlot(Path())

    private var shadowFilter: BlurMaskFilter? = null

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (!render) {
            return
        }

        val offsetLeft = MAX_OFFSET * mapRange(pointerOffset, MIN_OFFSET, 0.5f, MIN_OFFSET, 0.5f, MIN_OFFSET, MAX_OFFSET)
        val offsetRight = MAX_OFFSET * mapRange(pointerOffset, 0.5f, MAX_OFFSET, 0.5f, MIN_OFFSET, MIN_OFFSET, MAX_OFFSET)

        if (!pathPlot.pathCreated || dirty) {

            pathPlot.points.clear()
            pathPlot.path.reset()
            pathPlot.width = bounds.width - (cornerRadius * 2)
            pathPlot.height = bounds.height - (cornerRadius * 2)

            pathPlot.contentBounds.width = pathPlot.width + cornerRadius
            pathPlot.contentBounds.height = pathPlot.height + cornerRadius

            val shift = (pathPlot.width - pointerWidth)

            pathPlot.startX = bounds.x
            pathPlot.startY = (bounds.y + cornerRadius)

            pathPlot.points.add(PathPoint.Point(-(pointerWidth * offsetLeft), -pointerLength))
            pathPlot.points.add(PathPoint.Point(-((pointerOffset * shift)), MIN_OFFSET))

            pathPlot.points.add(PathPoint.Corner(PathCorner.BOTTOM_LEFT, cornerRadius))

            pathPlot.points.add(PathPoint.Point(MIN_OFFSET, -pathPlot.height))

            pathPlot.points.add(PathPoint.Corner(PathCorner.TOP_LEFT, cornerRadius))

            pathPlot.points.add(PathPoint.Point(pathPlot.width, MIN_OFFSET))

            pathPlot.points.add(PathPoint.Corner(PathCorner.TOP_RIGHT, cornerRadius))

            pathPlot.points.add(PathPoint.Point(MIN_OFFSET, pathPlot.height))

            pathPlot.points.add(PathPoint.Corner(PathCorner.BOTTOM_RIGHT, cornerRadius))

            pathPlot.points.add(PathPoint.Point(-(shift - (pointerOffset * shift)), MIN_OFFSET))
            pathPlot.points.add(PathPoint.Point(-(pointerWidth * offsetRight), pointerLength))

            dirty = false
            pathPlot.build()
        }

        pathPlot.startX = bounds.x
        pathPlot.startY = bounds.y + cornerRadius

        val shift = (pathPlot.width - pointerWidth)

        val pointerLeftStartX: PathPoint.Point =  pathPlot.points[0] as PathPoint.Point
        val pointerLeftEndX: PathPoint.Point =  pathPlot.points[1] as PathPoint.Point

        val pointerRightStartX: PathPoint.Point =  pathPlot.points[9] as PathPoint.Point
        val pointerRightEndX: PathPoint.Point =  pathPlot.points[10] as PathPoint.Point

        pointerLeftStartX.x = -(pointerWidth * offsetLeft)
        pointerLeftEndX.x = -((pointerOffset * shift))

        pointerRightStartX.x = -(shift-(pointerOffset * shift))
        pointerRightEndX.x = -(pointerWidth * offsetRight)

        pathPlot.contentBounds.x = bounds.x - ((pathPlot.width/2) + (cornerRadius/2))
        pathPlot.contentBounds.y = bounds.y - (pointerLength + (pathPlot.height + (cornerRadius / 2)))

        contentBounds.centerX = pathPlot.contentBounds.centerX -((pointerOffset * shift))
        contentBounds.centerY = pathPlot.contentBounds.centerY
        contentBounds.height = bounds.height

        pathPlot.build()

        if (drawShadows) {
            renderShadow(canvas, paint, pathPlot.path, shadowPath)
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

    private val shadowMatrix: Matrix by lazy {
        Matrix().apply {
            this.setTranslate(elevation * 0.1f, elevation * 0.2f)
        }
    }

    private fun renderShadow(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        if (shadowColor == null) {
            val color = ShadowUtility.getShadowColor(ShadowUtility.COLOR, elevation)
            this.shadowAlpha = color?.alpha ?: ShadowUtility.COLOR.alpha
            this.shadowColor = color?.updateAlpha((color.alpha * shadowAlphaOffset).toInt())
            this.shadowAlpha = shadowColor?.alpha ?: ShadowUtility.COLOR.alpha
            this.shadowFilter = ShadowUtility.getShadowFilter(elevation, minShadowRadius, maxShadowRadius)
        }

        paint.recycle()
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.maskFilter = shadowFilter
        paint.color = shadowColor?.toColor() ?: ShadowUtility.DEFAULT_COLOR
        paint.alpha = mapRange(opacity, MIN_COLOR, MAX_COLOR, MIN_COLOR, this.shadowColor?.alpha ?: MAX_COLOR)
        shadowPath.rewind()
        shadowPath.addPath(shapePath)
        shadowPath.transform(shadowMatrix)

        canvas.drawPath(shadowPath, paint)
    }
}
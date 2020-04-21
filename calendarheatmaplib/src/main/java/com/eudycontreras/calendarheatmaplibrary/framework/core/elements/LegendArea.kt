package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeRenderer
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableText
import com.eudycontreras.calendarheatmaplibrary.framework.data.Alignment
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class LegendArea(
    private val options: HeatMapOptions,
    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchableShape {

    private val levels: MutableList<DrawableShape> = mutableListOf()

    private val lessText = DrawableText(options.legendLessLabel)
    private val moreText = DrawableText(options.legendMoreLabel)

    private var sizeRatio: Float = DEFAULT_SIZE_RATIO

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    override var hovered: Boolean = false

    private val levelCount: Int = 4

    fun buildWith(offset: Float): LegendArea {
        when(options.legendAlignment) {
            Alignment.LEFT -> {
                withLeftAlignment(offset)
            }
            Alignment.RIGHT -> {
                withRightAlignment(offset)
            }
        }
        return this
    }

    private fun withLeftAlignment(offset: Float) {
        lessText.x = bounds.x + offset
        lessText.y = bounds.bottom - (offset * 2)
        lessText.alignment = DrawableText.Alignment.LEFT
        lessText.textSize = style.legendLabelStyle.textSize
        lessText.typeFace = style.legendLabelStyle.typeFace
        lessText.textColor = MutableColor(style.legendLabelStyle.textColor)
        lessText.build()

        val cellSize = bounds.height * sizeRatio
        var leftOffset = lessText.x + (offset * 2) + lessText.textBounds.width()

        for (level in 0..levelCount) {
            val shape = DrawableRectangle()
            shape.x = leftOffset
            shape.y = lessText.y - cellSize
            shape.width = cellSize
            shape.height = cellSize
            shape.color = getColor(level, 0f, levelCount.toFloat(), style)
            levels.add(shape)
            leftOffset += (cellSize + offset)
        }

        moreText.x = leftOffset + offset
        moreText.y = lessText.y
        moreText.alignment = DrawableText.Alignment.LEFT
        moreText.textSize = style.legendLabelStyle.textSize
        moreText.typeFace = style.legendLabelStyle.typeFace
        moreText.textColor = MutableColor(style.legendLabelStyle.textColor)
        moreText.build()
    }

    private fun withRightAlignment(offset: Float) {
        val cellSize = bounds.height * sizeRatio

        moreText.alignment = DrawableText.Alignment.RIGHT
        moreText.textSize = style.legendLabelStyle.textSize
        moreText.typeFace = style.legendLabelStyle.typeFace
        moreText.textColor = MutableColor(style.legendLabelStyle.textColor)
        moreText.build()
        moreText.x = bounds.right - offset
        moreText.y = bounds.bottom - (offset * 2)

        var rightOffset = moreText.x - (moreText.width + cellSize + (offset * 2))

        for (level in levelCount downTo 0) {
            val shape = DrawableRectangle()
            shape.x = rightOffset
            shape.y = moreText.y - cellSize
            shape.width = cellSize
            shape.height = cellSize
            shape.color = getColor(level, 0f, levelCount.toFloat(), style)
            levels.add(shape)
            rightOffset -= (cellSize + offset)
        }

        lessText.alignment = DrawableText.Alignment.RIGHT
        lessText.textSize = style.legendLabelStyle.textSize
        lessText.typeFace = style.legendLabelStyle.typeFace
        lessText.textColor = MutableColor(style.legendLabelStyle.textColor)
        lessText.build()
        lessText.bounds.x = rightOffset + (cellSize - (offset))
        lessText.bounds.y = moreText.y
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        lessText.onRender(canvas, paint, shapePath, shadowPath)
        moreText.onRender(canvas, paint, shapePath, shadowPath)
        for (shape in levels) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeRenderer: ShapeRenderer) {
        for (shape in levels) {
            if (shape is TouchableShape) {
                shape.onTouch(event, x, y, shapeRenderer)
            }
        }
    }

    override fun onLongPressed(
        event: MotionEvent,
        x: Float,
        y: Float,
        shapeRenderer: ShapeRenderer
    ) {
        for (shape in levels) {
            if (shape is TouchableShape) {
                shape.onLongPressed(event, x, y, shapeRenderer)
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun getColor(value: Int, min: Float, max: Float, style: HeatMapStyle): MutableColor {
        val range = mapRange(value.toFloat(), min, max, MIN_OFFSET, MAX_OFFSET)
        val colorMin = MutableColor.fromColor(style.minCellColor)
        val colorMax = MutableColor.fromColor(style.maxCellColor)
        return MutableColor.interpolateColor(colorMin, colorMax, range)
    }

    companion object {
        const val DEFAULT_SIZE_RATIO: Float = 0.55f
    }
}
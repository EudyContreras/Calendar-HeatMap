package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.*
import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.common.TouchableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.DrawableShape
import com.eudycontreras.calendarheatmaplibrary.framework.core.ShapeManager
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableText
import com.eudycontreras.calendarheatmaplibrary.framework.data.Alignment
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapOptions
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapStyle
import com.eudycontreras.calendarheatmaplibrary.framework.data.Measurements
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

    val style: HeatMapStyle,
    val bounds: Bounds
): RenderTarget, TouchableShape {

    private var sizeRatio: Float = DEFAULT_SIZE_RATIO

    private val spectrumLevels: Int = DEFAULT_SPECTRUM_LEVELS

    override var hovered: Boolean = false

    override var touchHandler: ((TouchableShape, MotionEvent, Float, Float) -> Unit)? = null

    private val shapes: MutableList<DrawableShape> = mutableListOf()

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    fun buildWith(measurements: Measurements, options: HeatMapOptions): LegendArea {
        val lessText = DrawableText(options.legendLessLabel, paint).apply {
            textSize = style.legendLabelStyle.textSize
            typeFace = style.legendLabelStyle.typeFace
            textColor = MutableColor(style.legendLabelStyle.textColor)
        }.build()

        val moreText = DrawableText(options.legendMoreLabel, paint).apply {
            textSize = style.legendLabelStyle.textSize
            typeFace = style.legendLabelStyle.typeFace
            textColor = MutableColor(style.legendLabelStyle.textColor)
        }.build()

        when(options.legendAlignment) {
            Alignment.LEFT -> {
                moreText.alignment = Alignment.LEFT
                lessText.alignment = Alignment.LEFT
                withLeftAlignment(measurements.cellGap, lessText, moreText)
            }
            Alignment.RIGHT -> {
                moreText.alignment = Alignment.RIGHT
                lessText.alignment = Alignment.RIGHT
                withRightAlignment(measurements.cellGap, lessText, moreText)
            }
        }

        shapes.add(lessText)
        shapes.add(moreText)

        return this
    }

    private fun withLeftAlignment(
        offset: Float,
        lessText: DrawableText,
        moreText: DrawableText
    ) {
        lessText.x = bounds.x + offset
        lessText.y = bounds.bottom - (offset * 2)

        val cellSize = bounds.height * sizeRatio
        var leftOffset = lessText.x + (offset * 2) + lessText.textBounds.width()

        for (level in 0..spectrumLevels) {
            val shape = DrawableRectangle()
            shape.x = leftOffset
            shape.y = lessText.y - cellSize
            shape.width = cellSize
            shape.height = cellSize
            shape.elevation = style.cellElevation
            shape.color = getColor(level, MIN_OFFSET, spectrumLevels.toFloat(), style)
            shapes.add(shape)
            leftOffset += (cellSize + offset)
        }

        moreText.x = leftOffset + offset
        moreText.y = lessText.y
    }

    private fun withRightAlignment(
        offset: Float,
        lessText: DrawableText,
        moreText: DrawableText
    ) {
        val cellSize = bounds.height * sizeRatio

        moreText.x = bounds.right - offset
        moreText.y = bounds.bottom - (offset * 2)

        var rightOffset = moreText.x - (moreText.width + cellSize + (offset * 2))

        for (level in spectrumLevels downTo 0) {
            val shape = DrawableRectangle()
            shape.x = rightOffset
            shape.y = moreText.y - cellSize
            shape.width = cellSize
            shape.height = cellSize
            shape.elevation = style.cellElevation
            shape.color = getColor(level, MIN_OFFSET, spectrumLevels.toFloat(), style)
            shapes.add(shape)
            rightOffset -= (cellSize + offset)
        }

        lessText.bounds.x = rightOffset + (cellSize - (offset))
        lessText.bounds.y = moreText.y
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {
        for (shape in shapes) {
            shape.onRender(canvas, paint, shapePath, shadowPath)
        }
    }

    override fun onTouch(event: MotionEvent, x: Float, y: Float, shapeManager: ShapeManager) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onTouch(event, x, y, shapeManager)
            }
        }
    }

    override fun onLongPressed(
        event: MotionEvent,
        x: Float,
        y: Float,
        shapeManager: ShapeManager
    ) {
        for (shape in shapes) {
            if (shape is TouchableShape) {
                shape.onLongPressed(event, x, y, shapeManager)
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
        const val DEFAULT_SPECTRUM_LEVELS: Int = 4
    }
}
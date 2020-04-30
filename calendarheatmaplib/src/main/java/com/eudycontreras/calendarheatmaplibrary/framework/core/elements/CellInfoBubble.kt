package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.Bubble
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DropShadow
import com.eudycontreras.calendarheatmaplibrary.framework.data.Measurements
import com.eudycontreras.calendarheatmaplibrary.framework.data.WeekDay
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MAX_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class CellInfoBubble(
    val bounds: Bounds,
    val topOffset: Float,
    val sideOffset: Float,
    val measurements: Measurements,
    val bubbleLayout: BubbleLayout<WeekDay>
): RenderTarget {
    var offsetX: Float = MIN_OFFSET
    var offsetY: Float = MIN_OFFSET

    var isRevealed: Boolean = false
    var isRevealing: Boolean = false

    private val bubble: Bubble by lazy {
        Bubble().also {
            it.cornerRadius = 0f
            it.pointerLength = 15.dp
            it.pointerOffset = 0f
            it.strokeWidth = 1.dp
            it.elevation = 4.dp
            it.parentBounds = this.bounds
            it.color = MutableColor.rgb(MAX_COLOR)
        }
    }
    private val shadow: DropShadow by lazy {
        DropShadow(bubbleLayout.elevation).also {
            it.bounds = Bounds(
                left = bubbleLayout.x,
                top = bubbleLayout.y,
                right = bubbleLayout.x + bubbleLayout.width,
                bottom = bubbleLayout.y + bubbleLayout.height
            )
            it.elevation = bubbleLayout.elevation
            it.render = false
        }
    }

    init {
        bubbleLayout.drawOverlay?.registerDrawTarget { canvas, renderData, invalidator ->
            shadow.onRender(canvas, renderData.paint, renderData.shapePath, renderData.shadowPath)
            bubble.onRender(canvas, renderData.paint, renderData.shapePath, renderData.shadowPath)
            invalidator()
        }
    }

    fun setInterceptedData(weekDay: WeekDay) {
        bubbleLayout.onDataIntercepted(weekDay)
    }

    fun onInterception(x: Float, y: Float, minInX: Float, maxInX: Float): Float {
        var positionY = y - (bubbleLayout.height + topOffset) + offsetY

        if (positionY < (bounds.top - offsetY)) {
            positionY = (bounds.top - offsetY)
        }

        val minX = bounds.left

        val maxX = (((bounds.left + bubbleLayout.boundsWidth) - bubbleLayout.width))

        bubble.x = x
        bubble.y = ((positionY + bubble.pointerLength) + bubbleLayout.height)

        bubble.pointerOffset = mapRange(x, minInX, maxInX, MIN_OFFSET, MAX_OFFSET)

        val interpolatedX = mapRange(bubble.pointerOffset, MIN_OFFSET, MAX_OFFSET, minInX + offsetX, maxX)
        bubbleLayout.onMove(interpolatedX, bubble.contentBounds.y)

        shadow.scaleX = bubbleLayout.scaleX
        shadow.scaleY = bubbleLayout.scaleY

        shadow.bounds.x = bubble.contentBounds.x
        shadow.bounds.y = bubble.contentBounds.y

        shadow.width = bubbleLayout.width
        shadow.height = bubbleLayout.height

        return positionY
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {

    }

    fun bringToFront(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val offset = MAX_OFFSET
        //bubbleLayout.toFront(offset, bubbleLayout.width * 0.5f, bubbleLayout.height,250)

//        val positionY = y - (bubbleLayout.height + topOffset)
//
//        val left = x + sideOffset
//        val top = ((positionY + bubble.pointerLength) + bubbleLayout.height)
//
//        bubble.bounds = Bounds(
//            left = left,
//            top = top,
//            right = (left + bubbleLayout.width) - sideOffset,
//            bottom = top + bubbleLayout.height
//        )
//        isRevealing = false
    }

    fun revealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        val pivotY = (bubbleLayout.height + topOffset)
        val positionY = y - (bubbleLayout.height + topOffset)
        val pivot = Coordinate(pivotX, pivotY)

        val left = x + sideOffset
        val top = ((positionY + bubble.pointerLength) + bubbleLayout.height)

        isRevealing = true
        val offset = MAX_OFFSET
        shadow.render = true
        bubbleLayout.reveal(offset, pivot,250)
        isRevealed = true
        isRevealing = false

        bubble.bounds = Bounds(
            left = left,
            top = top,
            right = (left + bubbleLayout.width),
            bottom = top + bubbleLayout.height
        )
    }

    fun concealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        val pivotY = (bubbleLayout.height + topOffset)

        val pivot = Coordinate(pivotX, pivotY)

        isRevealed = false
        val offset = MIN_OFFSET
        shadow.render = false
        bubbleLayout.conceal(offset, pivot,250)
        isRevealing = false
    }
}
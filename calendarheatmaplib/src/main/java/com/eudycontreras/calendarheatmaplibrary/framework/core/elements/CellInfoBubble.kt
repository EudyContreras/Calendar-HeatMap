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
    val measuremendts: Measurements,
    val bubbleLayout: BubbleLayout<WeekDay>
): RenderTarget {
    var offsetX: Float = MIN_OFFSET
    var offsetY: Float = MIN_OFFSET

    var isRevealed: Boolean = false
    var isRevealing: Boolean = false

    var elevation: Float = 8.dp

    private val bubble: Bubble by lazy {
        Bubble().also {
            it.cornerRadius = 4.dp
            it.pointerLength = 15.dp
            it.pointerOffset = 0f
            it.strokeWidth = 0f
            it.elevation = 0f
            it.parentBounds = this.bounds
            it.bounds = Bounds().apply {
                this.width = bubbleLayout.width
                this.height = bubbleLayout.height
            }
            it.color = MutableColor.fromHexString("#3C3C3C")
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
            it.elevation = elevation
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
        var positionY = y - (bubbleLayout.height)

        if (positionY < (bounds.top - offsetY) + topOffset) {
            positionY = (bounds.top - offsetY) + topOffset
        }

        val minX = (bounds.left + offsetX)
        val maxX = (((bounds.left + bubbleLayout.boundsWidth) - bubbleLayout.width)) + sideOffset

        bubble.pointerOffset = mapRange(x, minInX, maxInX, MIN_OFFSET, MAX_OFFSET)

        val interpolatedX = mapRange(bubble.pointerOffset, MIN_OFFSET, MAX_OFFSET, (minInX - measuremendts.cellGap) + offsetX, maxX)
        val interpolatedY = positionY - (bubble.height) + offsetY

        bubbleLayout.onMove(interpolatedX, interpolatedY)

        bubble.x = x + offsetX
        bubble.y = bubbleLayout.y + (bubbleLayout.height + bubble.height + (bubble.pointerLength / 2))

        shadow.bounds.x = interpolatedX
        shadow.bounds.y = interpolatedY

        shadow.width = bubbleLayout.width
        shadow.height = bubble.height

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

        val pivot = Coordinate(pivotX, pivotY)

        isRevealing = true

        bubbleLayout.onLayout(100) {
            val bounds = Bounds().apply {
                this.x =  x + offsetX
                this.y = bubbleLayout.y + (bubbleLayout.height + bubble.height + (bubble.pointerLength / 2))
                this.width = bubbleLayout.width
                this.height = bubbleLayout.height
            }
            bubble.bounds = bounds
            bubbleLayout.reveal(MAX_OFFSET, pivot,250)

            shadow.render = true
            bubble.render = true

            isRevealed = true
            isRevealing = false
        }
    }

    fun concealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        val pivotY = (bubbleLayout.height + topOffset)

        val pivot = Coordinate(pivotX, pivotY)

        shadow.render = false
        bubble.render = false

        bubbleLayout.conceal(MIN_OFFSET, pivot,250)

        isRevealing = false
        isRevealed = false

    }
}
package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.common.RenderTarget
import com.eudycontreras.calendarheatmaplibrary.framework.data.WeekDay
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate

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
    val bubbleLayout: BubbleLayout
): RenderTarget {
    var offsetX: Float = MIN_OFFSET
    var offsetY: Float = MIN_OFFSET

    var isRevealed: Boolean = false
    var isRevealing: Boolean = false

    fun setInterceptedData(weekDay: WeekDay) {
        bubbleLayout.onDataIntercepted(weekDay)
    }

    fun onInterception(x: Float, y: Float, minInX: Float, maxInX: Float) {
        var positionY = y - (bubbleLayout.bubbleHeight)

        if (positionY < (bounds.top - offsetY) + topOffset) {
            positionY = (bounds.top - offsetY) + topOffset
        }

        val pointerOffset = mapRange(x, minInX, maxInX, MIN_OFFSET, MAX_OFFSET)

        val interpolatedX = mapRange(pointerOffset, MIN_OFFSET, MAX_OFFSET, minInX - sideOffset, maxInX - bubbleLayout.bubbleWidth + sideOffset)
        val interpolatedY = positionY - (bubbleLayout.bubbleHeight)

        bubbleLayout.setPointerOffset(pointerOffset)
        bubbleLayout.onMove(interpolatedX, interpolatedY, x, offsetX, offsetY + sideOffset)
    }

    override fun onRender(canvas: Canvas, paint: Paint, shapePath: Path, shadowPath: Path) {}

    fun bringToFront() {
        bubbleLayout.toFront(offsetX, bubbleLayout.bubbleWidth * 0.5f, bubbleLayout.bubbleHeight,200)
        isRevealing = false
    }

    fun revealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {

        isRevealing = true
        isRevealed = true

        bubbleLayout.onLayout(20) {
            val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.bubbleWidth)
            val pivotY = (bubbleLayout.bubbleHeight + bubbleLayout.bubblePointerLength)

            onInterception(x, y, minInX, maxInX)

            bubbleLayout.reveal(MIN_OFFSET, Coordinate(pivotX, pivotY),200)

            isRevealing = false
        }
    }

    fun concealInfoBubble(x: Float, y: Float, minInX: Float, maxInX: Float) {
        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.bubbleWidth)
        val pivotY = (bubbleLayout.bubbleHeight + bubbleLayout.bubblePointerLength)

        bubbleLayout.conceal(MIN_OFFSET, Coordinate(pivotX, pivotY),150)

        isRevealing = false
        isRevealed = false
    }
}
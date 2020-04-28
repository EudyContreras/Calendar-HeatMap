package com.eudycontreras.calendarheatmaplibrary.framework.core.elements

import android.view.MotionEvent
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.BubbleLayout
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.framework.data.WeekDay
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal class CellInfoBubble(
    val bounds: Bounds,
    val sideOffset: Float,
    val bubbleLayout: BubbleLayout<WeekDay>
) {
    var offsetX: Float = MIN_OFFSET
    var offsetY: Float = MIN_OFFSET

    var isRevealed: Boolean = false
    var isRevealing: Boolean = false

    fun setInterceptedData(weekDay: WeekDay) {
        bubbleLayout.onDataIntercepted(weekDay)
    }

    fun onInterception(x: Float, y: Float, minInX: Float, maxInX: Float, eventAction: Int) {
        val topOffset = 20.dp

        var positionX = (x + offsetX) - (bubbleLayout.width / 2)
        var positionY = (y - offsetY) - (bubbleLayout.height + topOffset)

        val minX = bounds.left + sideOffset
        val maxX = ((bounds.left + bubbleLayout.boundsWidth) - bubbleLayout.width) - sideOffset

        if (positionY < bounds.top) {
            positionY = bounds.top
        }

        val interpolateX = mapRange(x, minInX, maxInX, minX, maxX)

        bubbleLayout.onMove(interpolateX, positionY)

        val pivotX = mapRange(x, minInX, maxInX, MIN_OFFSET, bubbleLayout.width)
        onInterceptorEvent(eventAction, pivotX, bubbleLayout.height + topOffset)
    }

    private fun onInterceptorEvent(eventAction: Int, pivotX: Float, pivotY: Float) {
        when(eventAction) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_BUTTON_RELEASE, MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL -> {
                if (isRevealed && !isRevealing) {
                    concealInfoBubble(pivotX = pivotX, pivotY = pivotY)
                }
            }
        }
    }

    fun bringToFront(pivotX: Float = MIN_OFFSET, pivotY: Float = MIN_OFFSET) {
        isRevealing = true
        val offset = MAX_OFFSET
        bubbleLayout.toFront(offset, bubbleLayout.width / 2f, bubbleLayout.height,250)
        isRevealed = true
        isRevealing = false
    }

    fun revealInfoBubble(pivotX: Float = MIN_OFFSET, pivotY: Float = MIN_OFFSET) {
        isRevealing = true
        val offset = MAX_OFFSET
        bubbleLayout.reveal(offset, bubbleLayout.width / 2f, bubbleLayout.height,250)
        isRevealed = true
        isRevealing = false
    }

    fun concealInfoBubble(pivotX: Float, pivotY: Float) {
        isRevealed = false
        val offset = MIN_OFFSET
        bubbleLayout.conceal(offset, pivotX, pivotY,250)
        isRevealing = false
    }
}
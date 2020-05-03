package com.eudycontreras.calendarheatmaplibrary.common

import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal interface BubbleLayout {
    val bubbleX: Float
    val bubbleY: Float
    val bubbleScaleX: Float
    val bubbleScaleY: Float
    val bubbleWidth: Float
    val bubbleHeight: Float
    val boundsWidth: Float
    val boundsHeight: Float
    val bubbleElevation: Float
    val drawOverlay: DrawOverlay?
    fun onLayout(delay: Long = 0, action: Action)
    fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun reveal(offset: Float, pivot: Coordinate, duration: Long = 0, onDone: Action? = null)
    fun conceal(offset: Float, pivot: Coordinate, duration: Long = 0, onDone: Action? = null)
    fun onMove(x: Float, y: Float)
    fun onDataIntercepted(data: Any)
    fun setDataInteceptListener(dataListener: (Any) -> Unit)
}
package com.eudycontreras.calendarheatmaplibrary.common

import com.eudycontreras.calendarheatmaplibrary.Action
import com.eudycontreras.calendarheatmaplibrary.framework.BubblePathDrawable
import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal interface BubbleLayout<T> {
    val x: Float
    val y: Float
    val scaleX: Float
    val scaleY: Float
    val width: Float
    val height: Float
    val drawOverlay: DrawOverlay?
    val boundsWidth: Float
    val boundsHeight: Float
    val elevation: Float
    fun onLayout(delay: Long, action: Action)
    fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun reveal(offset: Float, pivot: Coordinate, duration: Long = 0, onDone: Action? = null)
    fun conceal(offset: Float, pivot: Coordinate, duration: Long = 0, onDone: Action? = null)
    fun onMove(x: Float, y: Float)
    fun onDataIntercepted(data: T)
}
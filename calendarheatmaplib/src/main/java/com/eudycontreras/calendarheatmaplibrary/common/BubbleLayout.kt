package com.eudycontreras.calendarheatmaplibrary.common

import android.graphics.Canvas
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
    fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun reveal(offset: Float, pivot: Coordinate, duration: Long = 0)
    fun conceal(offset: Float, pivot: Coordinate, duration: Long = 0)
    fun onMove(x: Float, y: Float)
    fun onRender(canvas: Canvas)
    fun onDataIntercepted(data: T)
}
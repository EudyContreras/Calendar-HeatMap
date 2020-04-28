package com.eudycontreras.calendarheatmaplibrary.common

import android.graphics.Canvas

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
interface BubbleLayout<T> {
    val x: Float
    val y: Float
    val width: Float
    val height: Float
    val boundsWidth: Float
    val boundsHeight: Float
    val elevation: Float
    fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun reveal(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun conceal(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun onMove(x: Float, y: Float)
    fun onRender(canvas: Canvas)
    fun onDataIntercepted(data: T)
}
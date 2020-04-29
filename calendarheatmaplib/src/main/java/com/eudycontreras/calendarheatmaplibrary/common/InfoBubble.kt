package com.eudycontreras.calendarheatmaplibrary.common

import com.eudycontreras.calendarheatmaplibrary.properties.Coordinate

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
internal interface InfoBubble<T> {
    fun toFront(offset: Float, pivotX: Float, pivotY: Float, duration: Long = 0)
    fun onReveal(offset: Float, pivot: Coordinate, duration: Long = 0)
    fun onConcealed(offset: Float, pivot: Coordinate, duration: Long = 0)
    fun onMove(x: Float, y: Float)
}
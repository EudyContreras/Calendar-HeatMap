package com.eudycontreras.calendarheatmaplibrary.extensions

import android.graphics.Paint

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

fun Paint.recycle(){
    shader = null
    maskFilter = null
    pathEffect = null
    typeface = null
    textSize = 0f
    clearShadowLayer()
}
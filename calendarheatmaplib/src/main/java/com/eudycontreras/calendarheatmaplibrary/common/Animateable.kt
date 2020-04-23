package com.eudycontreras.calendarheatmaplibrary.common

interface Animateable {
    fun onPreAnimation()
    fun onPostAnimation()
    fun onAnimate(delta: Float)
}
package com.eudycontreras.calendarheatmaplibrary.common

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

interface UpdateTarget {
    fun onUpdate(currentPlayTime: Long, delta: Float)
}

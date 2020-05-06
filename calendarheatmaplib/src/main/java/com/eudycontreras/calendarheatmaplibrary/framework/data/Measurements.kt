package com.eudycontreras.calendarheatmaplibrary.framework.data

import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class Measurements(
    val cellGap: Float = MIN_OFFSET,
    val cellSize: Float = MIN_OFFSET,
    val matrixWidth: Float = MIN_OFFSET,
    val matrixHeight: Float = MIN_OFFSET,
    val legendAreaHeight: Float = MIN_OFFSET,
    val dayLabelAreaWidth: Float = MIN_OFFSET,
    val monthLabelAreaHeight: Float = MIN_OFFSET,
    val viewportWidth: Float = MIN_OFFSET,
    val viewportHeight: Float = MIN_OFFSET
)
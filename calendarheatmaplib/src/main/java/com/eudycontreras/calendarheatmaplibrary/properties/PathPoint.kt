package com.eudycontreras.calendarheatmaplibrary.properties

import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class PathPoint(
    var type: PathPlot.Type,
    var relative: Boolean = true,
    var startX: Float,
    var startY: Float,
    var endX: Float = MIN_OFFSET,
    var endY: Float = MIN_OFFSET
)
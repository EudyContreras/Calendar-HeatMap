package com.eudycontreras.calendarheatmaplibrary.properties

import android.graphics.Paint
import android.graphics.Path

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class RenderData(
    val paint: Paint,
    val shapePath: Path,
    val shadowPath: Path
)
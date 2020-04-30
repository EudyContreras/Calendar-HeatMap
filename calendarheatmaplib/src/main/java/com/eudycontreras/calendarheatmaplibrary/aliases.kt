package com.eudycontreras.calendarheatmaplibrary

import android.graphics.Canvas
import android.graphics.Color
import com.eudycontreras.calendarheatmaplibrary.properties.RenderData

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal typealias Action = () -> Unit

internal typealias AndroidColor = Color

internal typealias DrawTarget = (canvas: Canvas, renderData: RenderData, invalidator: () -> Unit) -> Unit
package com.eudycontreras.calendarheatmaplibrary.framework.data

import android.graphics.Typeface
import androidx.annotation.ColorInt
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.extensions.sp

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class HeatMapStyle(
    var cellTextColor: Int? = null,
    var cellElevation: Float = MIN_OFFSET,
    val cellTypeFace: Typeface = Typeface.DEFAULT_BOLD,
    var minCellColor: Int = AndroidColor.TRANSPARENT,
    var maxCellColor: Int = AndroidColor.TRANSPARENT,
    var emptyCellColor: Int = AndroidColor.TRANSPARENT,
    var interceptorLinesColor: Int = AndroidColor.WHITE,
    var interceptorCenterColor: Int = AndroidColor.WHITE,
    var interceptorElevation: Float = 4.dp,
    var interceptorLineThickness: Float = 1.5f.dp,
    var dayLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY),
    var monthLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY),
    var legendLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT_BOLD, 12.sp, AndroidColor.DKGRAY)
)

data class TextStyle(
    var typeFace: Typeface,
    var textSize: Float,
    @ColorInt var textColor: Int
)
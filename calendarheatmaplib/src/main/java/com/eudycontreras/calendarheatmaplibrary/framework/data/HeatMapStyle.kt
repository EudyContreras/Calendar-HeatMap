package com.eudycontreras.calendarheatmaplibrary.framework.data

import android.graphics.Typeface
import androidx.annotation.ColorInt
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.extensions.sp

data class HeatMapStyle(
    var minCellColor: Int = AndroidColor.TRANSPARENT,
    var maxCellColor: Int = AndroidColor.TRANSPARENT,
    var emptyCellColor: Int = AndroidColor.TRANSPARENT,
    var dayLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT, 12.sp, AndroidColor.DKGRAY),
    var monthLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT, 12.sp, AndroidColor.DKGRAY),
    var legendLabelStyle: TextStyle = TextStyle(Typeface.DEFAULT, 12.sp, AndroidColor.DKGRAY)
)

data class TextStyle(
    var typeFace: Typeface,
    var textSize: Float,
    @ColorInt var textColor: Int
)
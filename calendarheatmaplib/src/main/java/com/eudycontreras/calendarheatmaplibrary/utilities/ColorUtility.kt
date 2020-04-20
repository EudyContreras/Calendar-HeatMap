package com.eudycontreras.calendarheatmaplibrary.utilities

import androidx.annotation.ColorInt
import com.eudycontreras.calendarheatmaplibrary.AndroidColor
import com.eudycontreras.calendarheatmaplibrary.properties.Color
import com.eudycontreras.calendarheatmaplibrary.properties.Color.Companion.MAX_COLOR
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import kotlin.math.roundToInt

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since March 2020
 */

object ColorUtility {

    fun colorDecToHex(r: Int, g: Int, b: Int): Int {
        return AndroidColor.parseColor(colorDecToHexString(r, g, b))
    }

    fun colorDecToHex(a: Int, r: Int, g: Int, b: Int): Int {
        return AndroidColor.parseColor(colorDecToHexString(a, r, g, b))
    }

    fun colorDecToHexString(r: Int, g: Int, b: Int): String {
        return colorDecToHexString(MAX_COLOR, r, g, b)
    }

    fun colorDecToHexString(a: Int, r: Int, g: Int, b: Int): String {
        var red = Integer.toHexString(r)
        var green = Integer.toHexString(g)
        var blue = Integer.toHexString(b)
        var alpha = Integer.toHexString(a)

        if (red.length == 1) {
            red = "0$red"
        }
        if (green.length == 1) {
            green = "0$green"
        }
        if (blue.length == 1) {
            blue = "0$blue"
        }
        if (alpha.length == 1) {
            alpha = "0$alpha"
        }

        return "#$alpha$red$green$blue"
    }

    fun interpolateColor(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff

        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff

        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                (startB + (fraction * (endB - startB))).toInt()
    }

    fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (AndroidColor.alpha(color) * factor).roundToInt()
        val red = AndroidColor.red(color)
        val green = AndroidColor.green(color)
        val blue = AndroidColor.blue(color)
        return AndroidColor.argb(alpha, red, green, blue)
    }

    fun adjustAlpha(color: Color, factor: Float) {
        color.alpha = (color.alpha * factor).roundToInt()
    }

    fun toSoulColor(@ColorInt color: Int): Color {
        val alpha = AndroidColor.alpha(color)
        val red = AndroidColor.red(color)
        val green = AndroidColor.green(color)
        val blue = AndroidColor.blue(color)
        return MutableColor(alpha, red, green, blue)
    }
}

package com.eudycontreras.calendarheatmaplibrary.utilities

import android.graphics.BlurMaskFilter
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.extensions.dp
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.MutableColor
import kotlin.math.*

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since March 2020
 */
internal object ShadowUtility {
    internal const val DEFAULT_COLOR = 0x30000000

    private const val SHADOW_RADIUS_MULTIPLIER = -0.25f
    private const val SHADOW_OFFSET_MULTIPLIER = 0.35f

    private const val SHADOW_TOP_OFFSET = -0.34f
    private const val SHADOW_LEFT_OFFSET = 0.1f
    private const val SHADOW_RIGHT_OFFSET = -0.1f
    private const val SHADOW_BOTTOM_OFFSET = 0.5f

    private const val MIN_SHADOW_ALPHA = 15f
    private const val MAX_SHADOW_ALPHA = 60f

    internal const val MIN_SHADOW_RADIUS = 1f
    internal const val MAX_SHADOW_RADIUS = 55f

    private val MIN_ELEVATION = 0.dp
    private val MAX_ELEVATION = 24.dp

    internal val COLOR: MutableColor
        get() = MutableColor(DEFAULT_COLOR)


    fun getShadowFilter(elevation: Float, minShadowRadius: Float = MIN_SHADOW_RADIUS, maxShadowRadius: Float = MAX_SHADOW_RADIUS): BlurMaskFilter? {
        if (elevation <= 0) return null

        val radius = mapRange(elevation, MIN_ELEVATION, MAX_ELEVATION, minShadowRadius, maxShadowRadius)

        return BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
    }

    fun getShadowColor(color: MutableColor, elevation: Float): MutableColor? {
        if (elevation <= 0) return null

        val delta = mapRange(elevation, MIN_ELEVATION, MAX_ELEVATION, MIN_OFFSET, MAX_OFFSET)

        val alpha = (MAX_SHADOW_ALPHA - abs(MAX_SHADOW_ALPHA * delta) + (MIN_SHADOW_ALPHA * delta))

        return color.clone().updateAlpha(alpha.roundToInt())
    }

    private fun getShadowOffsetX(elevation: Float, translationZ: Float = MIN_OFFSET, shadowCompatRotation: Double = 0.0): Int {
        val depth = elevation + translationZ
        val shadowCompatOffset = ceil(depth * SHADOW_OFFSET_MULTIPLIER).toInt()
        return (shadowCompatOffset * sin(Math.toRadians(shadowCompatRotation))).toInt()
    }

    private fun getShadowOffsetY(elevation: Float, translationZ: Float = MIN_OFFSET, shadowCompatRotation: Double = 0.0): Int {
        val depth = elevation + translationZ
        val shadowCompatOffset = ceil(depth * SHADOW_OFFSET_MULTIPLIER).toInt()
        return (shadowCompatOffset * cos(Math.toRadians(shadowCompatRotation))).toInt()
    }

    fun getTopOffset(elevation: Float): Float {
        return (elevation * SHADOW_TOP_OFFSET)
    }

    fun getRadiusTopOffset(elevation: Float): Float {
        return (elevation * SHADOW_OFFSET_MULTIPLIER)
    }

    fun getBottomOffset(elevation: Float): Float {
        return (elevation * SHADOW_BOTTOM_OFFSET)
    }

    fun getLeftOffset(elevation: Float): Float {
        val shift = elevation - (elevation * SHADOW_LEFT_OFFSET)
        return (shift * SHADOW_LEFT_OFFSET)
    }

    fun getRightOffset(elevation: Float): Float {
        val shift = elevation - (elevation * SHADOW_RIGHT_OFFSET)
        return (shift * SHADOW_RIGHT_OFFSET)
    }

    fun getRadiusMultiplier(elevation: Float): Float {
        return (elevation * SHADOW_RADIUS_MULTIPLIER)
    }
}

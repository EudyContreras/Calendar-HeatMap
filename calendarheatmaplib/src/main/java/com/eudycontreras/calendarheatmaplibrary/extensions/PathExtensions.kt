package com.eudycontreras.calendarheatmaplibrary.extensions

import android.graphics.Path
import com.eudycontreras.calendarheatmaplibrary.properties.Bounds
import com.eudycontreras.calendarheatmaplibrary.utilities.ShadowUtility

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

fun Path.addShadowBounds(bounds: Bounds, radii: FloatArray, elevation: Float) {
    val left = bounds.left - ShadowUtility.getLeftOffset(elevation)
    val top = bounds.top - ShadowUtility.getTopOffset(elevation)
    val right = bounds.right + ShadowUtility.getRightOffset(elevation)
    val bottom = bounds.bottom + ShadowUtility.getBottomOffset(elevation)

    addRoundRect(left, top, right, bottom, radii, Path.Direction.CCW)
}

fun Path.addShadowBounds(
    bounds: Bounds,
    radii: FloatArray,
    elevation: Float,
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    val bLeft = (left ?: bounds.left) - ShadowUtility.getLeftOffset(elevation)
    val bTop = (top ?: bounds.top) - ShadowUtility.getTopOffset(elevation)
    val bRight = (right ?: bounds.right) + ShadowUtility.getRightOffset(elevation)
    val bBottom = (bottom ?: bounds.bottom) + ShadowUtility.getBottomOffset(elevation)
    addRoundRect(bLeft, bTop, bRight, bBottom, radii, Path.Direction.CCW)
}

fun Path.addShadowOval(centerX: Float, centerY: Float, radius: Float, elevation: Float) {
    val shadowRadius = radius + ShadowUtility.getRadiusMultiplier(elevation)
    addCircle(
        centerX + ShadowUtility.getLeftOffset(elevation),
        centerY + ShadowUtility.getRadiusTopOffset(elevation),
        shadowRadius,
        Path.Direction.CCW
    )
}

fun Path.addCircle(centerX: Float, centerY: Float, radius: Float) {
    addCircle(centerX, centerY, radius, Path.Direction.CCW)
}

fun Path.addRoundRect(bounds: Bounds, radii: FloatArray) {
    addRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, radii, Path.Direction.CCW)
}

fun Path.addRoundRect(left: Float, top: Float, right: Float, bottom: Float, radii: FloatArray) {
    addRoundRect(left, top, right, bottom, radii, Path.Direction.CCW)
}

fun Path.addRoundRect(
    bounds: Bounds,
    radii: FloatArray,
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    addRoundRect(
        left ?: bounds.left,
        top ?: bounds.top,
        right ?: bounds.right,
        bottom ?: bounds.bottom,
        radii,
        Path.Direction.CCW
    )
}
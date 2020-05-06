package com.eudycontreras.calendarheatmaplibrary.properties

import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET

/**
 * Copyright (C) 2019 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since January 2019
 */
data class Dimension(
    var width: Float = MIN_OFFSET,
    var height: Float = MIN_OFFSET
) {
    fun subtractWidth(dp: Float): Dimension {
        val newWidth = width - (dp * 2)
        return Dimension(newWidth, this.height)
    }

    fun addWidth(dp: Float): Dimension {
        val newWidth = width - (dp * 2)
        return Dimension(newWidth, this.height)
    }

    fun subtractHeight(dp: Float): Dimension {
        val newHeight = height - (dp * 2)
        return Dimension(this.width, newHeight)
    }

    fun addHeight(dp: Float): Dimension {
        val newHeight = height - (dp * 2)
        copy()
        return Dimension(this.width, newHeight)
    }

    operator fun plusAssign(other: Dimension) {
        this.width += other.width
        this.height += other.height
    }

    operator fun plus(other: Dimension): Dimension {
        return this.copy(
            width = width + other.width,
            height = height + other.height
        )
    }

    operator fun minusAssign(other: Dimension) {
        this.width -= other.width
        this.height -= other.height
    }

    operator fun minus(other: Dimension): Dimension {
        return this.copy(
            width = width - other.width,
            height = height - other.height
        )
    }
}

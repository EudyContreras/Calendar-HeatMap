package com.eudycontreras.calendarheatmaplibrary.properties

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

data class Padding(
    val paddingStart: Int,
    val paddingEnd: Int,
    val paddingTop: Int,
    val paddingBottom: Int
) {
    val verticalPadding: Int
        get() = paddingTop + paddingBottom

    val horizontalPadding: Int
        get() = paddingStart + paddingEnd
}
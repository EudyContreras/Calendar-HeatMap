package com.eudycontreras.calendarheatmaplibrary.properties

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
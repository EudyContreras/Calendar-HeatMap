package com.eudycontreras.calendarheatmaplibrary.properties

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
data class Property<T>(private var value: T) {

    fun setValue(value: T) {
        this.value = value
    }

    fun getValue() : T {
        return this.value
    }
}
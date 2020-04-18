package com.eudycontreras.calendarheatmaplibrary.properties

data class Property<T>(private var value: T) {

    fun setValue(value: T) {
        this.value = value
    }

    fun getValue() : T {
        return this.value
    }
}
package com.eudycontreras.calendarheatmaplibrary.properties

import kotlinx.serialization.Serializable

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
@Serializable
data class Index(val row: Int, val col: Int, val weight: Int = 0)
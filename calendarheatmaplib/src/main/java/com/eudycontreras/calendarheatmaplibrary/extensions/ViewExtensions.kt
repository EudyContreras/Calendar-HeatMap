package com.eudycontreras.calendarheatmaplibrary.extensions

import android.view.View
import android.view.ViewGroup

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal tailrec fun View.findMaster(): ViewGroup? {
    val parent: ViewGroup = this.parent as? ViewGroup? ?: return this as ViewGroup

    return parent.findMaster()
}

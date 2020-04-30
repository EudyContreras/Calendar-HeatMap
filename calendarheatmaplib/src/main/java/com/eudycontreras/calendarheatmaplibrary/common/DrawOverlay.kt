package com.eudycontreras.calendarheatmaplibrary.common

import com.eudycontreras.calendarheatmaplibrary.DrawTarget
import com.eudycontreras.calendarheatmaplibrary.properties.RenderData

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */
interface DrawOverlay {
    fun getRenderData(): RenderData
    fun setRenderData(renderData: RenderData)
    fun registerDrawTarget(drawTarget: DrawTarget)
    fun unregisterDrawTarget(drawTarget: DrawTarget)
}
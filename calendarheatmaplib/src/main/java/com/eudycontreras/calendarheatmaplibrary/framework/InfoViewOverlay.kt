package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.MainThread
import com.eudycontreras.calendarheatmaplibrary.DrawTarget
import com.eudycontreras.calendarheatmaplibrary.common.DrawOverlay
import com.eudycontreras.calendarheatmaplibrary.properties.RenderData

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

@MainThread
class InfoViewOverlay : View,
    DrawOverlay {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var renderData: RenderData

    private val drawTargets: MutableList<DrawTarget> = mutableListOf()

    private val invalidator: () -> Unit = {
        invalidate()
    }

    override fun getRenderData(): RenderData {
        return renderData
    }

    override fun setRenderData(renderData: RenderData) {
        this.renderData = renderData
    }

    override fun registerDrawTarget(drawTarget: DrawTarget) {
        drawTargets.add(drawTarget)
    }

    override fun unregisterDrawTarget(drawTarget: DrawTarget) {
        drawTargets.remove(drawTarget)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(target in drawTargets) {
            target(canvas, renderData, invalidator)
        }
    }
}
package com.eudycontreras.calendarheatmaplibrary.framework

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
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
class InfoViewOverlay : FrameLayout, DrawOverlay {
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

    override fun reDraw() {
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

    override fun dispatchDraw(canvas: Canvas) {
        for(target in drawTargets) {
            target(canvas, renderData, invalidator)
        }
        super.dispatchDraw(canvas)
    }
}
package com.eudycontreras.calendarheatmaplibrary.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.Interpolator
import androidx.core.math.MathUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.framework.core.shapes.DrawableRectangle
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Index

/**
 * Created by eudycontreras.
 */
internal class CellGridAnimation : HeatMapAnimation<Array<Array<DrawableRectangle>>> {

    override var interpolator: Interpolator = FastOutSlowInInterpolator()

    override var duration: Long = 0
    override var delay: Long = 0

    override var onEnd: (() -> Unit)? = null
    override var onStart: (() -> Unit)? = null

    var fromIndex: Index = Index(0, 0)
    var stagger: Long = 0

    var type: AnimationType = AnimationType.LEFT_TO_RIGHT

    enum class AnimationType {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        EDGE_TO_CENTER,
        CENTER_TO_EDGE
    }

    override fun animate(heatMap: CalHeatMap, animateable: Array<Array<DrawableRectangle>>) {
        performSequentialAnimation(heatMap, animateable)
    }

    private fun performSequentialAnimation(
        view: CalHeatMap,
        animateable: Array<Array<DrawableRectangle>>
    ) {
        val indexes = createOrder(animateable.size,  animateable[0].size, fromIndex)

        val valueAnimator = ValueAnimator.ofFloat(MIN_OFFSET, MAX_OFFSET)

        val startPoints = Array(animateable.size) {
            Array(animateable[it].size) { Pair(MIN_OFFSET, MIN_OFFSET) }
        }

        val values: Array<Array<Pair<Float, Float>>> = Array(animateable.size) {
            Array(animateable[it].size) { Pair(MIN_OFFSET, MIN_OFFSET) }
        }

        var start = 0L
        var end = start + duration
        var totalDuration = duration

        startPoints[indexes[0].row][indexes[0].col] = (start.toFloat() to end.toFloat())

        for (index in indexes) {
            values[index.row][index.col] = (MIN_OFFSET to MAX_OFFSET)
            start += stagger
            end = start + duration
            totalDuration += stagger
            startPoints[index.row][index.col] = (start.toFloat() to end.toFloat())
        }
        valueAnimator.startDelay = delay
        valueAnimator.duration = totalDuration
        valueAnimator.interpolator = interpolator
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                for (row in animateable) {
                    for (col in row) {
                        col.onPreAnimation()
                    }
                }
                onStart?.invoke()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                onEnd?.invoke()
            }
        })

        valueAnimator.addUpdateListener {
            for (index in indexes) {
                val rowIndex = index.row
                val colIndex = index.col

                val currentTime = it.currentPlayTime.toFloat()
                val animate = MathUtils.clamp(
                    mapRange(
                        currentTime,
                        startPoints[rowIndex][colIndex].first,
                        startPoints[rowIndex][colIndex].second,
                        values[rowIndex][colIndex].first,
                        values[rowIndex][colIndex].second
                    ),
                    values[rowIndex][colIndex].first,
                    values[rowIndex][colIndex].second
                )
                animateable[rowIndex][colIndex].onAnimate(it.interpolator.getInterpolation(animate))
            }

            view.update()
        }
        valueAnimator.start()
    }

    private fun createOrder(itemRowCount: Int, itemColCount: Int, fromIndex: Index): ArrayList<Index> {
        val indexes: ArrayList<Index> = ArrayList()

        var rowIndex = fromIndex.row
        var colIndex = fromIndex.col
        var shiftDown = 0
        var shiftUp = 0

        var increase = 0
        for (row in 0 until itemRowCount) {
            for (col in 0 until itemColCount) {
                indexes.add(Index(row, col))
            }
        }
        indexes.sortByDescending { it.col + it.row }
        indexes.reverse()

        return indexes
    }
}

private fun Int.clamp(max: Int): Int {
    if (this >= max) return max
    return this
}
package com.eudycontreras.calendarheatmaplibrary.animations

import android.view.animation.Interpolator
import androidx.core.math.MathUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.eudycontreras.calendarheatmaplibrary.MAX_OFFSET
import com.eudycontreras.calendarheatmaplibrary.MIN_OFFSET
import com.eudycontreras.calendarheatmaplibrary.common.Animateable
import com.eudycontreras.calendarheatmaplibrary.framework.CalHeatMap
import com.eudycontreras.calendarheatmaplibrary.manDistance
import com.eudycontreras.calendarheatmaplibrary.mapRange
import com.eudycontreras.calendarheatmaplibrary.properties.Index

/**
 * Copyright (C) 2020 Project X
 *
 * @Project ProjectX
 * @author Eudy Contreras.
 * @since April 2020
 */

internal class MatrixRevealAnimation<T: Animateable> : HeatMapAnimation<Array<Array<T>>> {

    override var interpolator: Interpolator = FastOutSlowInInterpolator()

    override var duration: Long = 0
    override var delay: Long = 0

    override var onEnd: (() -> Unit)? = null
    override var onStart: (() -> Unit)? = null

    var fromIndex: Index = Index(0, 0)
    var stagger: Long = 0

    override fun animate(heatMap: CalHeatMap, animateable: Array<Array<T>>) {
        performSequentialAnimation(heatMap, animateable)
    }

    private fun performSequentialAnimation(
        heatMap: CalHeatMap,
        animateable: Array<Array<T>>
    ) {
        val indexMaps = createOrder(animateable.size,  animateable[0].size, fromIndex)

        val startPoints = Array(animateable.size) {
            Array(animateable[it].size) { Pair(MIN_OFFSET, MIN_OFFSET) }
        }

        val values: Array<Array<Pair<Float, Float>>> = Array(animateable.size) {
            Array(animateable[it].size) { Pair(MIN_OFFSET, MIN_OFFSET) }
        }

        var start = 0L
        var end = start + duration
        var totalDuration = duration

        startPoints[0][0] = (start.toFloat() to end.toFloat())

        for (entries in indexMaps) {
            start += stagger
            end = start + duration
            totalDuration += stagger
            for (index in entries.value) {
                values[index.row][index.col] = (MIN_OFFSET to MAX_OFFSET)
                startPoints[index.row][index.col] = (start.toFloat() to end.toFloat())
            }
        }

        val indexes = indexMaps.values.flatten()

        val animation = AnimationEvent(
            duration = totalDuration,
            startDelay = delay,
            onEnd = onEnd,
            onStart = {
                for (row in animateable) {
                    for (col in row) {
                        col.onPreAnimation()
                    }
                }
                onStart?.invoke()
            },
            updateListener = { _, currentPlayTime, _ ->
                for (index in indexes) {
                    val rowIndex = index.row
                    val colIndex = index.col

                    val currentTime = currentPlayTime.toFloat()
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
                    animateable[rowIndex][colIndex].onAnimate(interpolator.getInterpolation(animate))
                }
            }
        )
        heatMap.addAnimation(animation)
    }

    private fun createOrder(itemRowCount: Int, itemColCount: Int, fromIndex: Index): Map<Int, List<Index>> {
        val indexes: ArrayList<Index> = ArrayList()

        for (row in 0 until itemRowCount) {
            for (col in 0 until itemColCount) {
                indexes.add(Index(
                    row = row,
                    col = col,
                    weight = manDistance(fromIndex.row, fromIndex.col, row, col)
                ))
            }
        }

        indexes.sortBy { it.weight }

        return indexes.groupBy { it.weight }
    }
}
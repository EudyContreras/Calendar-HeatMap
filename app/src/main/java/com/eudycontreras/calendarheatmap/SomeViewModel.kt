package com.eudycontreras.calendarheatmap

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.framework.data.Date
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*
import kotlin.random.Random

internal class SomeViewModel : ViewModel() {

    private val monthLabels = arrayOf("Jan", "Feb", "Mar", "Apr", "Maj", "Jun", "Jul", "Agu", "Sep", "Oct", "Nov", "Dec")
    private val weekdays = arrayOf("Sun", "Mon", "Tus", "Wed", "Thu", "Fri", "Sat")

    val demoData: LiveData<HeatMapData> = MutableLiveData(generateData())

    @SuppressLint("NewApi")
    private fun generateData(): HeatMapData {
        val dateTo = LocalDate.now()
        val origin = dateTo.minusDays(365)
        var dateFrom = dateTo.minusDays(365)

        val weeks: MutableList<Week> = mutableListOf()

        val daysInWeek = 7

        val weeksInYear = ChronoUnit.WEEKS.between(dateFrom, dateTo)

        for (index in 0L..weeksInYear) {
            val days: MutableList<WeekDay> = mutableListOf()

            for (day in 0 until daysInWeek) {
                dateFrom = dateFrom.plusDays(1)
                days.add(
                    WeekDay(
                        index = day,
                        label = weekdays[day],
                        date = dateFrom.toDate(),
                        activeLabel = daysInWeek % 2 != 0,
                        frequencyData = Frequency(
                            count = if (day > 0 && day < daysInWeek - 1) {
                                Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE)
                            } else if (Random.nextBoolean()) { Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE / 2) } else 0,
                            data = null
                        )
                    )
                )
            }

            weeks.add(Week(weekNumber = (index + 1L).toInt(), weekDays = days))
        }

        return HeatMapData(
            timeSpan = TimeSpan(
                dateMin = origin.toDate(),
                dateMax = dateTo.toDate(),
                weeks = weeks
            )
        )
    }

    @SuppressLint("NewApi")
    private fun LocalDate.toDate(): Date {
        return Date(dayOfMonth,  Month(monthValue -1, monthLabels[monthValue - 1]), year)
    }
}

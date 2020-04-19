package com.eudycontreras.calendarheatmap

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
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
        val dateFrom = dateTo.minusYears(1)

        val months: MutableList<Month> = mutableListOf()

        val monthsInYear = 12L
        val daysInWeek = 7

        val weekFields: WeekFields = WeekFields.of(Locale.getDefault())

        for (index in 0L..monthsInYear) {
            val current = if (index > 0) {
                dateFrom.plusMonths(index).withDayOfMonth(1)
            } else {
                dateFrom.plusMonths(index)
            }
            val nextMonth = if (index < monthsInYear) {
                dateFrom.plusMonths(index + 1).withDayOfMonth(1)
            } else {
                dateTo
            }

            val weeksInMonth = ChronoUnit.WEEKS.between(current, nextMonth)

            val weekdays: List<WeekDay> = List(daysInWeek, init = {
                WeekDay(
                    index = it,
                    label = weekdays[it],
                    activeLabel = daysInWeek % 2 != 0,
                    frequencyData = Frequency(
                        count = Random.nextInt(0, 12),
                        data = null
                    )
                )
            })

            val weeksData: List<Week> = List(weeksInMonth.toInt(), init = {
                val weekNumber: Int = current.plusWeeks(it.toLong()).get(weekFields.weekOfWeekBasedYear())
                Week(
                    weekNumber = weekNumber,
                    weekDays = weekdays
                )
            })

            months.add(Month(
                index = current.monthValue - 1,
                year = current.year,
                label = monthLabels[current.monthValue - 1],
                weeks = weeksData
            ))
        }

        return HeatMapData(
            timeSpan = TimeSpan(months)
        )
    }
}

package com.eudycontreras.calendarheatmap

import android.os.Build
import androidx.annotation.RequiresApi
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

    val demoData: LiveData<HeatMapData> = MutableLiveData(getSafeData())

    private fun getSafeData(): HeatMapData {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generateData()
        } else {
            return HeatMapData(
                options = HeatMapOptions(),
                timeSpan = TimeSpan(
                    dateMin = Date(0, Month(0, ""), 0),
                    dateMax = Date(0, Month(0, ""), 0),
                    weeks = emptyList()
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateData(): HeatMapData {
        val daysInWeek = 7
        val daysInYear = 365L

        val dateTo = LocalDate.now()
        val origin = dateTo.minusDays(daysInYear)
        var dateFrom = dateTo.minusDays(daysInYear)

        val weeks: MutableList<Week> = mutableListOf()

        val monthLabels = HeatMapOptions.STANDARD_MONTH_LABELS.map { it.text }

        val weeksInYear = ChronoUnit.WEEKS.between(dateFrom, dateTo)

        weeks@ for (index in 0L..weeksInYear) {
            val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
            val weekNumber = dateFrom.get(weekFields.weekOfWeekBasedYear())

            val days: MutableList<WeekDay> = mutableListOf()

            days@for (day in 0 until daysInWeek) {
                if (dateFrom > dateTo) {
                    break@days
                }
                days.add(
                    WeekDay(
                        index = day,
                        date = dateFrom.toDate(monthLabels),
                        frequencyData = Frequency(
                            count = if (day > 0 && day < daysInWeek - 1) {
                                Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE)
                            } else if (Random.nextBoolean()) { Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE / 2) } else 0,
                            data = null
                        )
                    )
                )
                dateFrom = dateFrom.plusDays(1)
            }

            weeks.add(Week(weekNumber = weekNumber, weekDays = days))
        }

        return HeatMapData(
            options = HeatMapOptions(),
            timeSpan = TimeSpan(
                dateMin = origin.toDate(monthLabels),
                dateMax = dateTo.toDate(monthLabels),
                weeks = weeks
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun LocalDate.toDate(monthLabels: List<String>): Date {
        return Date(dayOfMonth,  Month(monthValue -1, monthLabels[monthValue - 1]), year)
    }
}

package com.eudycontreras.calendarheatmap

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.framework.data.Date
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*
import kotlin.random.Random

internal class SomeViewModel : ViewModel() {

    private val holidays: HashSet<Date> = hashSetOf(
        Date(24, 11),
        Date(25, 11),
        Date(31, 11),
        Date(1, 0)
    )

    private val vacation: HashSet<Date> = (1..17).map { Date(it, 6) }.toHashSet()

    val demoData1: HeatMapData
        get() = getSafeData()

    val demoData2: HeatMapData
        get() = getSafeData()

    val demoData3: HeatMapData
        get() = getSafeData()

    val demoData4: HeatMapData
        get() = getSafeData()

    val demoData5: HeatMapData
        get() = getSafeData()

    val demoData6: HeatMapData
        get() = getSafeData()

    private fun getSafeData(): HeatMapData {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generateData()
        } else {
            return HeatMapData(
                options = HeatMapOptions(),
                timeSpan = TimeSpan(
                    dateMin = Date(0, 0),
                    dateMax = Date(0, 0),
                    weeks = emptyList()
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateData(): HeatMapData {
        val daysInWeek = 7

        val dateTo = LocalDate.now()
        val origin = dateTo.minusYears(1)
        var dateFrom = dateTo.minusYears(1)

        val weeks: MutableList<Week> = mutableListOf()

        val monthLabels = HeatMapOptions.STANDARD_MONTH_LABELS.map { it.text }

        val weeksInYear = ChronoUnit.WEEKS.between(dateFrom, dateTo)

        weeks@ for (index in 0L..weeksInYear) {
            val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
            val weekNumber = dateFrom.get(weekFields.weekOfWeekBasedYear())

            val days: MutableList<WeekDay> = mutableListOf()

            days@ for (day in 0 until daysInWeek) {
                if (dateFrom > dateTo) {
                    break@days
                }
                val date = dateFrom.toDate(monthLabels)

                val frequency = if (holidays.contains(date) || vacation.contains(date)) {
                    0
                } else if (day > 0 && day < daysInWeek - 1) {
                    Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE)
                } else if (Random.nextBoolean()) {
                    Random.nextInt(Frequency.MIN_VALUE, Frequency.MAX_VALUE / 2)
                } else 0

                days.add(
                    WeekDay(
                        index = day,
                        date = date,
                        frequencyData = Frequency(count = frequency, data = null)
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
        return Date(dayOfMonth,monthValue - 1, year)
    }
}

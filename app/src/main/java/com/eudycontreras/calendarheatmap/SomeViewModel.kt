package com.eudycontreras.calendarheatmap

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.eudycontreras.calendarheatmaplibrary.framework.data.*
import com.eudycontreras.calendarheatmaplibrary.framework.data.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private fun generateData(weekOffset: Long = 0): HeatMapData {
        val dateTo = LocalDate.now()

        val origin = dateTo.minusYears(1).with(WeekFields.SUNDAY_START.dayOfWeek(), 1L).minusWeeks(weekOffset)
        var dateFrom = dateTo.minusYears(1).with(WeekFields.SUNDAY_START.dayOfWeek(), 1L).minusWeeks(weekOffset)

        val weeks: MutableList<Week> = mutableListOf()

        val weeksInYear = ChronoUnit.WEEKS.between(dateFrom, dateTo)

        if (weeksInYear > WEEKS_IN_YEAR) {
            return generateData(WEEKS_IN_YEAR - weeksInYear)
        }
        var maxFrequencyValue = 0

        val dateFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)

        weeks@ for (index in 0L..weeksInYear) {
            val weekFields: WeekFields = WeekFields.of(Locale.getDefault())
            val weekNumber = dateFrom.get(weekFields.weekOfWeekBasedYear())

            val days: MutableList<WeekDay> = mutableListOf()

            days@ for (day in 0 until DAYS_IN_WEEK) {
                if (dateFrom > dateTo) {
                    break@days
                }
                val date = dateFrom.toDate()

                val frequency = if (holidays.contains(date) || vacation.contains(date)) {
                    0
                } else if (day > 0 && day < DAYS_IN_WEEK - 1) {
                    (Frequency.MIN_VALUE .. Frequency.MAX_VALUE).random()
                    Random.nextInt(Frequency.MAX_VALUE + 1)
                } else if (Random.nextBoolean()) {
                    (Frequency.MIN_VALUE until Frequency.MAX_VALUE.div(2)).random()
                } else 0

                if (frequency > maxFrequencyValue) {
                    maxFrequencyValue = frequency
                }
                days.add(
                    WeekDay(
                        index = day,
                        date = date,
                        dateString = dateFrom.format(dateFormatter),
                        frequencyData = Frequency(count = frequency, data = null)
                    )
                )
                dateFrom = dateFrom.plusDays(1)
            }
            weeks.add(Week(weekNumber = weekNumber, weekDays = days))
        }

        return HeatMapData(
            options = HeatMapOptions(
                maxFrequencyValue = maxFrequencyValue
            ),
            timeSpan = TimeSpan(
                dateMin = origin.toDate(),
                dateMax = dateTo.toDate(),
                weeks = weeks
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun LocalDate.toDate() : Date {
        return Date(dayOfMonth, monthValue - 1, year)
    }

    private companion object {
        const val DAYS_IN_WEEK = 7
        const val WEEKS_IN_YEAR = 52

        const val DEFAULT_DATE_FORMAT = "MMM dd, YYYY"
    }
}

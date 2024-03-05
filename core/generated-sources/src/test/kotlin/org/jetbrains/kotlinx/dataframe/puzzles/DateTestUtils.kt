package org.jetbrains.kotlinx.dataframe.puzzles

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus

class DateRangeIterator(first: LocalDate, last: LocalDate, val step: Int) : Iterator<LocalDate> {
    private val finalElement: LocalDate = last
    private var hasNext: Boolean = if (step > 0) first <= last else first >= last
    private var next: LocalDate = if (hasNext) first else finalElement

    override fun hasNext(): Boolean = hasNext

    override fun next(): LocalDate {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        } else {
            next = next.plus(step, DateTimeUnit.DayBased(1))
        }
        return value
    }
}

operator fun ClosedRange<LocalDate>.iterator() = DateRangeIterator(this.start, this.endInclusive, 1)

fun ClosedRange<LocalDate>.toList(): List<LocalDate> {
    return when (val size = this.start.daysUntil(this.endInclusive)) {
        0 -> emptyList()
        1 -> listOf(iterator().next())
        else -> {
            val dest = ArrayList<LocalDate>(size)
            for (item in this) {
                dest.add(item)
            }
            dest
        }
    }
}

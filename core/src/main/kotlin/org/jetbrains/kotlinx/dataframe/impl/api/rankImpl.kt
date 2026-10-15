package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.RankMethod
import org.jetbrains.kotlinx.dataframe.api.RankNAOption
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.api.name
import kotlin.reflect.typeOf

/** Sorts indices, scans comparison groups once, then restores original row positions. */
internal fun <T : Comparable<T>> DataColumn<T?>.rankImpl(
    method: RankMethod,
    ascending: Boolean,
    naOption: RankNAOption,
    pct: Boolean,
): DataColumn<Double?> {
    val values = toList()
    val missing = BooleanArray(values.size) { values[it] == null || values[it].isNaN }

    fun compareValues(left: Int, right: Int): Int =
        when {
            missing[left] && missing[right] -> 0

            missing[left] -> if (naOption == RankNAOption.Top) -1 else 1

            missing[right] -> if (naOption == RankNAOption.Top) 1 else -1

            ascending -> values[left]!!.compareTo(values[right]!!)

            // Swap operands instead of negating: a comparator may legitimately return Int.MIN_VALUE.
            else -> values[right]!!.compareTo(values[left]!!)
        }

    val ordered = values.indices
        .filter { naOption != RankNAOption.Keep || !missing[it] }
        .sortedWith { left, right ->
            val comparison = compareValues(left, right)
            if (comparison == 0) left.compareTo(right) else comparison
        }
    val ranks = MutableList<Double?>(values.size) { null }
    var start = 0
    var groups = 0

    while (start < ordered.size) {
        var end = start + 1
        while (end < ordered.size && compareValues(ordered[start], ordered[end]) == 0) end++
        groups++
        val groupRank = when (method) {
            RankMethod.Average -> (start.toDouble() + 1.0 + end.toDouble()) / 2.0
            RankMethod.Min -> start.toDouble() + 1.0
            RankMethod.Max -> end.toDouble()
            RankMethod.Dense -> groups.toDouble()
            RankMethod.First -> 0.0 // Each row in the tie receives its own position below.
        }
        for (position in start until end) {
            ranks[ordered[position]] = if (method == RankMethod.First) position.toDouble() + 1.0 else groupRank
        }
        start = end
    }

    if (pct && ordered.isNotEmpty()) {
        val denominator = if (method == RankMethod.Dense) groups.toDouble() else ordered.size.toDouble()
        for (index in ranks.indices) ranks[index] = ranks[index]?.div(denominator)
    }

    return DataColumn.createValueColumn(name(), ranks, typeOf<Double?>())
}

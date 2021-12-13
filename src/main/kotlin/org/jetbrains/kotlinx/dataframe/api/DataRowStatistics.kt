package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.median
import org.jetbrains.kotlinx.dataframe.math.std

public fun AnyRow.rowMinOrNull(): Any? = values().filterIsInstance<Comparable<*>>().minWithOrNull(compareBy { it })
public fun AnyRow.rowMin(): Any = rowMinOrNull().suggestIfNull("rowMin")

public fun AnyRow.rowMaxOrNull(): Any? = values().filterIsInstance<Comparable<*>>().maxWithOrNull(compareBy { it })
public fun AnyRow.rowMax(): Any = rowMaxOrNull().suggestIfNull("rowMax")

public fun AnyRow.rowMean(skipNA: Boolean = defaultSkipNA): Double = values().filterIsInstance<Number>().map { it.toDouble() }.mean(skipNA)

public fun AnyRow.rowStd(skipNA: Boolean = defaultSkipNA): Double = values().filterIsInstance<Number>().map { it.toDouble() }.std(skipNA)

public fun AnyRow.rowSum(): Number = Aggregators.sum.aggregateMixed(values().filterIsInstance<Number>()) ?: 0

public fun AnyRow.rowMedianOrNull(): Any? = Aggregators.median.aggregateMixed(values().filterIsInstance<Comparable<Any?>>().asIterable())
public fun AnyRow.rowMedian(): Any = rowMedianOrNull().suggestIfNull("rowMedian")

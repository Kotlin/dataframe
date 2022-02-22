package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.mean
import org.jetbrains.kotlinx.dataframe.math.medianOrNull
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.typeOf

public fun AnyRow.count(): Int = columnsCount()
public fun AnyRow.count(predicate: Predicate<Any?>): Int = values().count(predicate)

public fun AnyRow.rowMinOrNull(): Any? = values().filterIsInstance<Comparable<*>>().minWithOrNull(compareBy { it })
public fun AnyRow.rowMin(): Any = rowMinOrNull().suggestIfNull("rowMin")
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOfOrNull(): T? = values().filterIsInstance<T>().minOrNull()
public inline fun <reified T : Comparable<T>> AnyRow.rowMinOf(): T = rowMinOfOrNull<T>().suggestIfNull("rowMinOf")

public fun AnyRow.rowMaxOrNull(): Any? = values().filterIsInstance<Comparable<*>>().maxWithOrNull(compareBy { it })
public fun AnyRow.rowMax(): Any = rowMaxOrNull().suggestIfNull("rowMax")
public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOfOrNull(): T? = values().filterIsInstance<T>().maxOrNull()
public inline fun <reified T : Comparable<T>> AnyRow.rowMaxOf(): T = rowMaxOfOrNull<T>().suggestIfNull("rowMaxOf")

public fun AnyRow.rowMean(skipNA: Boolean = skipNA_default): Double = values().filterIsInstance<Number>().map { it.toDouble() }.mean(skipNA)
public inline fun <reified T : Number> AnyRow.rowMeanOf(): Double = values().filterIsInstance<T>().mean(typeOf<T>())

public fun AnyRow.rowStd(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): Double = values().filterIsInstance<Number>().map { it.toDouble() }.std(skipNA, ddof)
public inline fun <reified T : Number> AnyRow.rowStdOf(ddof: Int = ddof_default): Double = values().filterIsInstance<T>().std(typeOf<T>(), ddof = ddof)

public fun AnyRow.rowSum(): Number = Aggregators.sum.aggregateMixed(values().filterIsInstance<Number>()) ?: 0
public inline fun <reified T : Number> AnyRow.rowSumOf(): T = values().filterIsInstance<T>().sum(typeOf<T>())

public fun AnyRow.rowMedianOrNull(): Any? = Aggregators.median.aggregateMixed(values().filterIsInstance<Comparable<Any?>>().asIterable())
public fun AnyRow.rowMedian(): Any = rowMedianOrNull().suggestIfNull("rowMedian")
public inline fun <reified T : Comparable<T>> AnyRow.rowMedianOfOrNull(): T? = valuesOf<T>().medianOrNull()
public inline fun <reified T : Comparable<T>> AnyRow.rowMedianOf(): T = rowMedianOfOrNull<T>().suggestIfNull("rowMedianOf")

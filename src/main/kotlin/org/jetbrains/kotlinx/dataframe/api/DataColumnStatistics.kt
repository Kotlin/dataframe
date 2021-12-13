package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.impl.suggestIfNull
import org.jetbrains.kotlinx.dataframe.math.sum
import kotlin.reflect.full.withNullability

// region

public fun <T> DataColumn<T>.count(predicate: Predicate<T>? = null): Int = if (predicate == null) size() else values().count(predicate)

// endregion

// region min

public fun <T : Comparable<T>> DataColumn<T?>.min(): T = minOrNull().suggestIfNull("min")
public fun <T : Comparable<T>> DataColumn<T?>.minOrNull(): T? = asSequence().filterNotNull().minOrNull()

public fun <T, R : Comparable<R>> DataColumn<T>.minBy(selector: (T) -> R): T = minByOrNull(selector).suggestIfNull("minBy")
public fun <T, R : Comparable<R>> DataColumn<T>.minByOrNull(selector: (T) -> R): T? = values.minByOrNull(selector)

public fun <T, R : Comparable<R>> DataColumn<T>.minOf(selector: (T) -> R): R = minOfOrNull(selector).suggestIfNull("minOf")
public fun <T, R : Comparable<R>> DataColumn<T>.minOfOrNull(selector: (T) -> R): R? = values.minOfOrNull(selector)

// endregion

// region max

public fun <T : Comparable<T>> DataColumn<T?>.max(): T = maxOrNull().suggestIfNull("max")
public fun <T : Comparable<T>> DataColumn<T?>.maxOrNull(): T? = asSequence().filterNotNull().maxOrNull()

public fun <T, R : Comparable<R>> DataColumn<T>.maxBy(selector: (T) -> R): T = maxByOrNull(selector).suggestIfNull("maxBy")
public fun <T, R : Comparable<R>> DataColumn<T>.maxByOrNull(selector: (T) -> R): T? = values.maxByOrNull(selector)

public fun <T, R : Comparable<R>> DataColumn<T>.maxOf(selector: (T) -> R): R = maxOfOrNull(selector).suggestIfNull("maxOf")
public fun <T, R : Comparable<R>> DataColumn<T>.maxOfOrNull(selector: (T) -> R): R? = values.maxOfOrNull(selector)

// endregion

// region sum

@JvmName("sumT")
public fun <T : Number> DataColumn<T>.sum(): T = values.sum(type())

@JvmName("sumT?")
public fun <T : Number> DataColumn<T?>.sum(): T = values.sum(type())

public inline fun <T, reified R : Number> DataColumn<T>.sumOf(crossinline expression: (T) -> R): R? =
    (Aggregators.sum as Aggregator<*, *>).cast<R>().of(this, expression)

// endregion

// region mean

public fun <T : Number> DataColumn<T?>.mean(skipNA: Boolean = defaultSkipNA): Double = meanOrNull(skipNA).suggestIfNull("mean")
public fun <T : Number> DataColumn<T?>.meanOrNull(skipNA: Boolean = defaultSkipNA): Double? = Aggregators.mean(skipNA).aggregate(this)

public inline fun <T, reified R : Number> DataColumn<T>.meanOf(
    skipNA: Boolean = defaultSkipNA,
    noinline expression: (T) -> R?
): Double = Aggregators.mean(skipNA).cast2<R?, Double>().aggregateOf(this, expression) ?: Double.NaN

// endregion

// region median

public fun <T : Comparable<T>> DataColumn<T?>.median(): T = medianOrNull().suggestIfNull("median")
public fun <T : Comparable<T>> DataColumn<T?>.medianOrNull(): T? = Aggregators.median.cast<T>().aggregate(this)

public inline fun <T, reified R : Comparable<R>> DataColumn<T>.medianOfOrNull(noinline expression: (T) -> R?): R? = Aggregators.median.cast<R?>().aggregateOf(this, expression)
public inline fun <T, reified R : Comparable<R>> DataColumn<T>.medianOf(noinline expression: (T) -> R?): R = medianOfOrNull(expression).suggestIfNull("medianOf")

// endregion

// region std

public fun <T : Number> DataColumn<T?>.std(): Double = Aggregators.std.aggregate(this) ?: .0

public inline fun <T, reified R : Number> DataColumn<T>.stdOf(noinline expression: (T) -> R?): Double = Aggregators.std.aggregateOf(this, expression) ?: .0

// endregion

// region valueCounts

@DataSchema
public interface ValueCount {
    public val count: Int
}

internal val defaultCountColumnName: String = ValueCount::count.name

public fun <T> DataColumn<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<ValueCount> {
    var grouped = toList().groupBy { it }.map { it.key to it.value.size }
    if (sort) {
        grouped = if (ascending) grouped.sortedBy { it.second }
        else grouped.sortedByDescending { it.second }
    }
    if (dropNA) grouped = grouped.filter { !it.first.isNA }
    val nulls = if (dropNA) false else hasNulls()
    val values = DataColumn.create(name(), grouped.map { it.first }, type().withNullability(nulls))
    val countName = if (resultColumn == name()) resultColumn + "1" else resultColumn
    val counts = DataColumn.create(countName, grouped.map { it.second }, getType<Int>())
    return dataFrameOf(values, counts).cast()
}

// endregion

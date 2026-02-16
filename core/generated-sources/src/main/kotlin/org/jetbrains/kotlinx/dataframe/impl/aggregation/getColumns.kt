package org.jetbrains.kotlinx.dataframe.impl.aggregation

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.NamedValue
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.isNumber
import org.jetbrains.kotlinx.dataframe.api.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.api.valuesAreComparable
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType

internal inline fun <T> Aggregatable<T>.remainingColumns(
    crossinline predicate: (AnyCol) -> Boolean,
): ColumnsSelector<T, Any?> = remainingColumnsSelector().filter { predicate(it.data) }

/**
 * Emulates selecting all columns whose values are mutually comparable to each other.
 * These are columns of type `R` where `R : Comparable<R>`.
 *
 * There is no way to denote this generically in types, however,
 * hence the _fake_ type `Comparable<Any>` is used.
 * (`Comparable<Nothing>` would be more correct, but then the compiler complains)
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> Aggregatable<T>.intraComparableColumns(): ColumnsSelector<T, Comparable<Any>?> =
    remainingColumns { it.valuesAreComparable() } as ColumnsSelector<T, Comparable<Any>?>

@Suppress("UNCHECKED_CAST")
internal fun <T> Aggregatable<T>.numberColumns(): ColumnsSelector<T, Number?> =
    remainingColumns { it.isNumber() } as ColumnsSelector<T, Number?>

@Suppress("UNCHECKED_CAST")
internal fun <T> Aggregatable<T>.primitiveOrMixedNumberColumns(): ColumnsSelector<T, Number?> =
    remainingColumns { it.isPrimitiveOrMixedNumber() } as ColumnsSelector<T, Number?>

internal fun <T> DataRow<T>.primitiveOrMixedNumberColumns(): ColumnsSelector<T, Number?> =
    { cols { it.isPrimitiveOrMixedNumber() }.cast() }

internal fun NamedValue.toColumnWithPath() =
    path to createColumnGuessingType(
        name = path.last(),
        values = listOf(value),
        suggestedType = TypeSuggestion.create(type, guessType),
        defaultValue = default,
    )

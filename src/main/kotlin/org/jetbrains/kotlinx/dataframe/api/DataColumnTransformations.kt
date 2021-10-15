package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.StringCol
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.impl.between
import kotlin.reflect.KClass

// region between

public fun <T : Comparable<T>> DataColumn<T>.between(left: T, right: T, includeBoundaries: Boolean = true): DataColumn<Boolean> = map { it.between(left, right, includeBoundaries) }

// endregion

// region digitize

public fun DataColumn<Double>.digitize(vararg bins: Int, right: Boolean = false): DataColumn<Int> = digitize(bins.toList(), Double::class, right)

public fun <T : Comparable<T>> DataColumn<T>.digitize(vararg bins: T, right: Boolean = false): DataColumn<Int> = digitize(bins.toList(), right)

public fun <T : Comparable<T>> DataColumn<T>.digitize(bins: List<Int>, kclass: KClass<T>, right: Boolean = false): DataColumn<Int> = digitize(
    bins.toList().map { org.jetbrains.kotlinx.dataframe.impl.convert(it, kclass) },
    right
)

public fun <T : Comparable<T>> DataColumn<T>.digitize(bins: List<T>, right: Boolean = false): DataColumn<Int> {
    // TODO: use binary search
    // TODO: support descending order of bins
    val predicate: (T, T) -> Boolean = if (right) { value, bin -> value <= bin } else { value, bin -> value < bin }

    return map { value ->
        val index = bins.indexOfFirst { predicate(value, it) }
        if (index == -1) bins.size
        else index
    }
}

// endregion
// TODO: change return type to BooleanColumn
public infix fun <T> DataColumn<T>.isMatching(predicate: Predicate<T>): BooleanArray = BooleanArray(size) {
    predicate(this[it])
}

// region StringColumn Api

public fun StringCol.length(): DataColumn<Int?> = map { it?.length }
public fun StringCol.lowercase(): StringCol = map { it?.lowercase() }
public fun StringCol.uppercase(): StringCol = map { it?.uppercase() }

// endregion

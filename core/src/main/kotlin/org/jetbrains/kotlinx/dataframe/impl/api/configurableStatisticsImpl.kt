package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.getOrNull
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.math.IndexedComparable
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.jetbrains.kotlinx.dataframe.math.quantile
import org.jetbrains.kotlinx.dataframe.math.quantileOrNull
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/** Selects the existing engine without converting selecting inputs to Double. */
internal fun Sequence<Comparable<Any>>.percentileWithMethodOrNull(
    percentile: Double,
    type: KType,
    skipNaN: Boolean,
    method: QuantileEstimationMethod<*, *>,
): Any? {
    require(percentile in 0.0..100.0) { "Percentile must be in range [0, 100]" }
    return when (method) {
        is QuantileEstimationMethod.Selecting -> quantileOrNull(percentile / 100.0, type, skipNaN, method)
        is QuantileEstimationMethod.Interpolating -> quantileOrNull(percentile / 100.0, type, skipNaN, method)
    }
}

/** The declared schema must match the explicit method, including selecting primitive values. */
internal fun quantileMethodConversion(method: QuantileEstimationMethod<*, *>): CalculateReturnType =
    { type, isEmpty ->
        val resultType = when (method) {
            is QuantileEstimationMethod.Selecting -> {
                require(type == nothingType || type.isIntraComparable()) {
                    "Cannot select a percentile for ${renderType(type)}"
                }
                type
            }

            is QuantileEstimationMethod.Interpolating -> {
                require(type == nothingType || type.isPrimitiveNumber()) {
                    "Percentile interpolation requires primitive numbers, but was ${renderType(type)}"
                }
                typeOf<Double>()
            }
        }
        resultType.withNullability(isEmpty)
    }

/** Filters using the original values, before wrapping them, so NaN detection retains its type and index. */
internal fun <T : Comparable<T & Any>?> Sequence<T>.indexOfPercentileWithMethod(
    percentile: Double,
    type: KType,
    skipNaN: Boolean,
    method: QuantileEstimationMethod.Selecting,
): Int {
    require(percentile in 0.0..100.0) { "Percentile must be in range [0, 100]" }
    val nonNullType = type.withNullability(false)
    if (nonNullType == nothingType) return -1
    require(nonNullType.isIntraComparable()) {
        "Cannot select a percentile for ${renderType(type)}"
    }
    val indexed = ArrayList<IndexedComparable<T & Any>>()
    for ((index, value) in withIndex()) {
        if (value == null) continue
        if (value.isNaN) {
            if (!skipNaN) return index
            continue
        }
        indexed.add(IndexedComparable(index, value))
    }
    if (indexed.isEmpty()) return -1
    return method.quantile(percentile / 100.0, indexed).index
}

/** The string API obtains the actual column type instead of erasing it to Comparable. */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> DataFrame<T>.percentileByWithMethodOrNullImpl(
    percentile: Double,
    method: QuantileEstimationMethod.Selecting,
    column: String,
    skipNaN: Boolean,
): DataRow<T>? {
    val values = this[column]
    val index = (values.values().asSequence() as Sequence<Comparable<Any>?>).indexOfPercentileWithMethod(
        percentile,
        values.type(),
        skipNaN,
        method,
    )
    return getOrNull(index)
}

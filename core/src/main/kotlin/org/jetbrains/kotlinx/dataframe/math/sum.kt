package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnTypeOrNull
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.sequences.filterNotNull

internal fun Iterable<Number?>.sum(type: KType): Number = asSequence().sum(type)

@Suppress("UNCHECKED_CAST")
@JvmName("sumNullableT")
@PublishedApi
internal fun Sequence<Number?>.sum(type: KType): Number {
    if (type.isMarkedNullable) {
        return filterNotNull().sum(type.withNullability(false))
    }
    return when (type.withNullability(false)) {
        typeOf<Double>() -> (this as Sequence<Double>).sum()

        typeOf<Float>() -> (this as Sequence<Float>).sum()

        typeOf<Int>() -> (this as Sequence<Int>).sum()

        // Note: returns Int
        typeOf<Short>() -> (this as Sequence<Short>).sum()

        // Note: returns Int
        typeOf<Byte>() -> (this as Sequence<Byte>).sum()

        typeOf<Long>() -> (this as Sequence<Long>).sum()

        typeOf<Number>() ->
            error("Encountered non-specific Number type in sum function. This should not occur.")

        nothingType -> 0.0

        else -> throw IllegalArgumentException(
            "Unable to compute the sum for ${renderType(type)}, Only primitive numbers are supported.",
        )
    }
}

/** T: Number? -> T */
internal val sumTypeConversion: CalculateReturnTypeOrNull = { type, _ ->
    when (type.withNullability(false)) {
        typeOf<Int>(),
        typeOf<Short>(),
        typeOf<Byte>(),
        -> typeOf<Int>()

        typeOf<Long>() -> typeOf<Long>()

        typeOf<Double>() -> typeOf<Double>()

        typeOf<Float>() -> typeOf<Float>()

        else -> typeOf<Double>()
    }
}

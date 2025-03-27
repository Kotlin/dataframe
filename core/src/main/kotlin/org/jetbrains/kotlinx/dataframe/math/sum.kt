package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnTypeOrNull
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
@JvmName("sumNullableT")
@PublishedApi
internal fun Sequence<Number?>.sum(type: KType, skipNaN: Boolean): Number {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in sum function. This should not occur.")
    }
    return when (type) {
        typeOf<Double>() -> (this as Sequence<Double>).filterNot { skipNaN && it.isNaN() }.sum()

        typeOf<Float>() -> (this as Sequence<Float>).filterNot { skipNaN && it.isNaN() }.sum()

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
    when (val type = type.withNullability(false)) {
        // type changes to Int
        typeOf<Short>(), typeOf<Byte>() -> typeOf<Int>()

        // type remains the same
        typeOf<Int>(), typeOf<Long>(), typeOf<Double>(), typeOf<Float>(), typeOf<Number>() -> type

        nothingType -> typeOf<Double>()

        else ->
            error("Unable to compute the sum for ${renderType(type)}, Only primitive numbers are supported.")
    }
}

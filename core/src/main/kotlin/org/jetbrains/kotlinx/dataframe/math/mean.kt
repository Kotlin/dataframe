package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.skipNaNDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> Sequence<T>.mean(type: KType, skipNaN: Boolean): Double {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in mean function. This should not occur.")
    }
    return when (type) {
        typeOf<Double>() -> (this as Sequence<Double>).mean(skipNaN)

        typeOf<Float>() -> (this as Sequence<Float>).map { it.toDouble() }.mean(skipNaN)

        typeOf<Int>() -> (this as Sequence<Int>).map { it.toDouble() }.mean(false)

        typeOf<Short>() -> (this as Sequence<Short>).map { it.toDouble() }.mean(false)

        typeOf<Byte>() -> (this as Sequence<Byte>).map { it.toDouble() }.mean(false)

        typeOf<Long>() -> {
            (this as Sequence<Long>).map { it.toDouble() }.mean(false)
        }

        typeOf<BigInteger>(), typeOf<BigDecimal>() ->
            throw IllegalArgumentException(
                "Cannot calculate the mean for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        typeOf<Number>() ->
            error("Encountered non-specific Number type in mean function. This should not occur.")

        // this means the sequence is empty
        nothingType -> Double.NaN

        else -> throw IllegalArgumentException(
            "Unable to compute the mean for ${renderType(type)}. Only primitive numbers are supported.",
        )
    }
}

/** T: Number? -> Double */
internal val meanTypeConversion: CalculateReturnType = { _, _ ->
    typeOf<Double>()
}

internal fun Sequence<Double>.mean(skipNaN: Boolean = skipNaNDefault): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNaN) {
                continue
            } else {
                return Double.NaN
            }
        }
        sum += element
        count++
    }
    return if (count > 0) sum / count else Double.NaN
}

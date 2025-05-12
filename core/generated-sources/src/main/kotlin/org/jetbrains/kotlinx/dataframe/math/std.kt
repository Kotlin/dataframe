package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.ddofDefault
import org.jetbrains.kotlinx.dataframe.api.skipNaNDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.sqrt
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Calculates the standard deviation from [this] with optional delta degrees of freedom.
 *
 * @param ddof delta degrees of freedom, the bias-correction of std.
 *   Default is [ddofDefault], so `ddof = 1`, the "unbiased sample standard deviation", but alternatively,
 *   the "population standard deviation", so `ddof = 0`, can be used.
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Number> Sequence<T?>.std(type: KType, skipNaN: Boolean, ddof: Int): Double {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in std function. This should not occur.")
    }
    return when (type) {
        typeOf<Double>() -> (this as Sequence<Double>).std(skipNaN, ddof)

        typeOf<Float>() -> (this as Sequence<Float>).map { it.toDouble() }.std(skipNaN, ddof)

        typeOf<Int>() -> (this as Sequence<Int>).map { it.toDouble() }.std(false, ddof)

        typeOf<Short>() -> (this as Sequence<Short>).map { it.toDouble() }.std(false, ddof)

        typeOf<Byte>() -> (this as Sequence<Byte>).map { it.toDouble() }.std(false, ddof)

        typeOf<Long>() -> {
            (this as Sequence<Long>).map { it.toDouble() }.std(false, ddof)
        }

        typeOf<BigInteger>(), typeOf<BigDecimal>() ->
            throw IllegalArgumentException(
                "Cannot calculate the std for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        typeOf<Number>() ->
            error("Encountered non-specific Number type in std function. This should not occur.")

        // this means the sequence is empty
        nothingType -> Double.NaN

        else -> throw IllegalArgumentException(
            "Unable to compute the std for type ${renderType(type)}. Only primitive numbers are supported",
        )
    }
}

/** T: Number? -> Double */
internal val stdTypeConversion: CalculateReturnType = { _, _ ->
    typeOf<Double>()
}

@JvmName("doubleStd")
internal fun Sequence<Double>.std(skipNaN: Boolean = skipNaNDefault, ddof: Int = ddofDefault): Double =
    calculateBasicStatsOrNull(skipNaN)?.std(ddof) ?: Double.NaN

/**
 * Calculates the standard deviation from a [BasicStats] with optional delta degrees of freedom.
 *
 * @param ddof delta degrees of freedom, the bias-correction of std.
 *   Default is [ddofDefault], so `ddof = 1`, the "unbiased sample standard deviation", but alternatively,
 *   the "population standard deviation", so `ddof = 0`, can be used.
 */
internal fun BasicStats.std(ddof: Int): Double =
    if (count <= ddof) {
        Double.NaN
    } else {
        sqrt(variance / (count - ddof))
    }

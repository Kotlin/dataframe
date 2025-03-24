package org.jetbrains.kotlinx.dataframe.math

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.skipNA_default
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnTypeOrNull
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

@Suppress("UNCHECKED_CAST")
internal fun <T : Number> Sequence<T>.mean(type: KType, skipNA: Boolean = skipNA_default): Double {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in mean function. This should not occur.")
    }
    return when (type) {
        typeOf<Double>() -> (this as Sequence<Double>).mean(skipNA)

        typeOf<Float>() -> (this as Sequence<Float>).map { it.toDouble() }.mean(skipNA)

        typeOf<Int>() -> (this as Sequence<Int>).map { it.toDouble() }.mean(false)

        typeOf<Short>() -> (this as Sequence<Short>).map { it.toDouble() }.mean(false)

        typeOf<Byte>() -> (this as Sequence<Byte>).map { it.toDouble() }.mean(false)

        typeOf<Long>() -> {
            logger.warn { "Converting Longs to Doubles to calculate the mean, loss of precision may occur." }
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
            "Unable to compute the mean for ${renderType(type)}, Only primitive numbers are supported.",
        )
    }
}

/** T: Number? -> Double */
internal val meanTypeConversion: CalculateReturnTypeOrNull = { _, _ ->
    typeOf<Double>()
}

internal fun Sequence<Double>.mean(skipNA: Boolean = skipNA_default): Double {
    var count = 0
    var sum: Double = 0.toDouble()
    for (element in this) {
        if (element.isNaN()) {
            if (skipNA) {
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

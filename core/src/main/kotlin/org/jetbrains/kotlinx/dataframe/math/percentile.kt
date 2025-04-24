package org.jetbrains.kotlinx.dataframe.math

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.math.quantileOrNull
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

internal fun <T : Comparable<T>> Sequence<T>.percentileOrNull(percentile: Double, type: KType, skipNaN: Boolean): Any? {
    when {
        percentile !in 0.0..100.0 -> error("Percentile must be in range [0, 100]")

        type.isMarkedNullable ->
            error("Encountered nullable type ${renderType(type)} in percentile function. This should not occur.")

        // this means the sequence is empty
        type == nothingType -> return null

        !type.isIntraComparable() ->
            error(
                "Unable to compute the percentile for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        type == typeOf<BigDecimal>() || type == typeOf<BigInteger>() ->
            throw IllegalArgumentException(
                "Cannot calculate the percentile for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        type == typeOf<Long>() ->
            logger.warn { "Converting Longs to Doubles to calculate the percentile, loss of precision may occur." }
    }

    // percentile of 25.0 means the 25th 100-quantile, so 25 / 100 = 0.25
    val p = percentile / 100.0

    // TODO make configurable
    val (values, method) =
        when {
            type.isPrimitiveNumber() -> this.map { (it as Number).toDouble() } to QuantileEstimationMethod.R8
            else -> this to QuantileEstimationMethod.R3
        }

    // fake Comparable types to satisfy the compiler
    values as Sequence<Comparable<Any>>
    method as QuantileEstimationMethod<Comparable<Any>>

    return values.quantileOrNull(
        p = p,
        type = type,
        skipNaN = skipNaN,
        method = method,
        name = "percentile",
    )
}

internal val percentileConversion: CalculateReturnType = { type, isEmpty ->
    when {
        // uses linear interpolation, R8 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isPrimitiveNumber() -> typeOf<Double>()

        // closest rank method, preferring lower middle,
        // number R3 of Hyndman and Fan "Sample quantiles in statistical packages"
        type.isIntraComparable() -> type

        else -> error("Can not calculate percentile for type ${renderType(type)}")
    }.withNullability(isEmpty)
}

internal fun <T : Comparable<T & Any>?> Sequence<T>.indexOfPercentile(
    percentile: Double,
    type: KType,
    skipNaN: Boolean,
): Int {
    val nonNullType = type.withNullability(false)
    when {
        percentile !in 0.0..100.0 -> error("Percentile must be in range [0, 100]")

        // this means the sequence is empty
        nonNullType == nothingType -> return -1

        !nonNullType.isIntraComparable() ->
            error(
                "Unable to compute the percentile for ${
                    renderType(type)
                }. Only primitive numbers or self-comparables are supported.",
            )

        nonNullType == typeOf<BigDecimal>() || nonNullType == typeOf<BigInteger>() ->
            throw IllegalArgumentException(
                "Cannot calculate the percentile for big numbers in DataFrame. Only primitive numbers are supported.",
            )
    }

    // TODO make configurable
    val method = QuantileEstimationMethod.R3

    // percentile of 25.0 means the 25th 100-quantile, so 25 / 100 = 0.25
    val p = percentile / 100.0
    return this.indexOfQuantile(
        p = p,
        type = type,
        skipNaN = skipNaN,
        method = method as QuantileEstimationMethod<T>,
        name = "percentile",
    ).let {
        method.roundIndex(it)
    }.toInt()
}

@PublishedApi
internal fun <T : Comparable<T>> Iterable<T?>.percentile(percentile: Double, type: KType): T? {
    require(percentile in 0.0..100.0) { "Percentile must be in range [0, 100]" }

    @Suppress("UNCHECKED_CAST")
    val list = if (type.isMarkedNullable) filterNotNull() else (this as Iterable<T>).asList()
    val size = list.size
    if (size == 0) return null

    val index = (percentile / 100.0 * (size - 1)).toInt()
    val fraction = (percentile / 100.0 * (size - 1)) - index

    // median handle for even sized list (legacy logic)
    if (percentile == 50.0 && size % 2 == 0) {
        val lower = list.quickSelect(index)
        val upper = list.quickSelect(index + 1)

        return when (type) {
            Double::class -> ((lower as Double + upper as Double) / 2.0) as T
            Float::class -> ((lower as Float + upper as Float) / 2.0f) as T
            Int::class -> ((lower as Int + upper as Int) / 2) as T
            Short::class -> ((lower as Short + upper as Short) / 2).toShort() as T
            Long::class -> ((lower as Long + upper as Long) / 2L) as T
            Byte::class -> ((lower as Byte + upper as Byte) / 2).toByte() as T
            BigDecimal::class -> ((lower as BigDecimal + upper as BigDecimal) / 2.toBigDecimal()) as T
            BigInteger::class -> ((lower as BigInteger + upper as BigInteger) / 2.toBigInteger()) as T
            else -> lower
        }
    }

    if (fraction == 0.0) {
        return list.quickSelect(index)
    }

    val lower = list.quickSelect(index)
    val upper = list.quickSelect(index + 1)

    return when (type.classifier) {
        Double::class -> ((lower as Double) + (upper as Double - lower as Double) * fraction) as T

        Float::class -> ((lower as Float) + (upper as Float - lower as Float) * fraction) as T

        Int::class -> ((lower as Int) + (upper as Int - lower as Int) * fraction).toInt() as T

        Short::class -> ((lower as Short) + (upper as Short - lower as Short) * fraction).toInt().toShort() as T

        Long::class -> ((lower as Long) + (upper as Long - lower as Long) * fraction).toLong() as T

        Byte::class -> ((lower as Byte) + (upper as Byte - lower as Byte) * fraction).toInt().toByte() as T

        BigDecimal::class -> (
            (lower as BigDecimal) +
                (upper as BigDecimal - lower as BigDecimal) * fraction.toBigDecimal()
        ) as T

        BigInteger::class -> (
            (lower as BigInteger) +
                (
                    (upper as BigInteger - lower as BigInteger) * fraction.toBigDecimal()
                        .toBigInteger()
                )
        ) as T

        else -> lower
    }
}

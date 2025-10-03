package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.isNA
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberTypeOrNull
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * @param skipNA when true, ignores NA values (null or NaN)
 *   When false, all values after first NA will be NaN (for Double and Float columns)
 *   or null (for integer columns).
 */
internal fun DataColumn<Number?>.cumSumImpl(type: KType, skipNA: Boolean): DataColumn<Number?> =
    when (type) {
        typeOf<Double>() -> cast<Double>().cumSumImpl(skipNA)

        typeOf<Double?>() -> cast<Double?>().cumSumImpl(skipNA)

        typeOf<Float>() -> cast<Float>().cumSumImpl(skipNA)

        typeOf<Float?>() -> cast<Float?>().cumSumImpl(skipNA)

        typeOf<Int>() -> cast<Int>().cumSumImpl()

        // Note: returns Int
        typeOf<Byte>() -> cast<Byte>().cumSumImpl()

        // Note: returns Int
        typeOf<Short>() -> cast<Short>().cumSumImpl()

        typeOf<Int?>() -> cast<Int?>().cumSumImpl(skipNA)

        // Note: returns Int
        typeOf<Byte?>() -> cast<Byte?>().cumSumImpl(skipNA)

        // Note: returns Int
        typeOf<Short?>() -> cast<Short?>().cumSumImpl(skipNA)

        typeOf<Long>() -> cast<Long>().cumSumImpl()

        typeOf<Long?>() -> cast<Long?>().cumSumImpl(skipNA)

        typeOf<BigInteger>(), typeOf<BigInteger?>(), typeOf<BigDecimal>(), typeOf<BigDecimal?>() ->
            throw IllegalArgumentException(
                "Cannot calculate the cumSum for big numbers in DataFrame. Only primitive numbers are supported.",
            )

        // accepts mixed primitive numbers by unifying them
        typeOf<Number?>(), typeOf<Number>() -> {
            val types = this.asIterable().types().toSet()
            val unifiedType = types.unifiedNumberTypeOrNull(UnifiedNumberTypeOptions.PRIMITIVES_ONLY)
                ?: error(
                    "Couldn't unify the numbers of types ${
                        types.joinToString { renderType(it) }
                    } of column ${name()} in cumSum. Please manually convert the numbers in column ${name()} to the same primitive number type before using cumSum.",
                )

            this.asIterable()
                .convertToUnifiedNumberType(UnifiedNumberTypeOptions.PRIMITIVES_ONLY, unifiedType)
                .toColumn(this.name())
                .cumSumImpl(unifiedType, skipNA)
        }

        // CumSum for empty column or column with just null is itself
        nothingType, nullableNothingType -> this

        else -> error("CumSum for type ${type()} is not supported")
    }

@JvmName("doubleCumsum")
internal fun DataColumn<Double>.cumSumImpl(skipNA: Boolean): DataColumn<Double> {
    var sum = .0
    var fillNaN = false
    return map {
        when {
            it.isNaN() -> {
                if (!skipNA) fillNaN = true
                Double.NaN
            }

            fillNaN -> Double.NaN

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("cumsumDoubleNullable")
internal fun DataColumn<Double?>.cumSumImpl(skipNA: Boolean): DataColumn<Double> {
    var sum = .0
    var fillNaN = false
    return map {
        when {
            it.isNA() -> {
                if (!skipNA) fillNaN = true
                Double.NaN
            }

            fillNaN -> Double.NaN

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("floatCumsum")
internal fun DataColumn<Float>.cumSumImpl(skipNA: Boolean): DataColumn<Float> {
    var sum = .0f
    var fillNaN = false
    return map {
        when {
            it.isNaN() -> {
                if (!skipNA) fillNaN = true
                Float.NaN
            }

            fillNaN -> Float.NaN

            else -> {
                sum += it
                sum
            }
        }
    }
}

internal fun DataColumn<Float?>.cumSumImpl(skipNA: Boolean): DataColumn<Float> {
    var sum = .0f
    var fillNaN = false
    return map {
        when {
            it.isNA() -> {
                if (!skipNA) fillNaN = true
                Float.NaN
            }

            fillNaN -> Float.NaN

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("intCumsum")
internal fun DataColumn<Int>.cumSumImpl(): DataColumn<Int> {
    var sum = 0
    return map {
        sum += it
        sum
    }
}

@JvmName("intCumsum")
internal fun DataColumn<Int?>.cumSumImpl(skipNA: Boolean): DataColumn<Int?> {
    var sum = 0
    var fillNull = false
    return map {
        when {
            it == null -> {
                if (!skipNA) fillNull = true
                null
            }

            fillNull -> null

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("byteCumsum")
internal fun DataColumn<Byte>.cumSumImpl(): DataColumn<Int> {
    var sum = 0
    return map {
        sum += it
        sum
    }
}

@JvmName("cumsumByteNullable")
internal fun DataColumn<Byte?>.cumSumImpl(skipNA: Boolean): DataColumn<Int?> {
    var sum = 0
    var fillNull = false
    return map {
        when {
            it == null -> {
                if (!skipNA) fillNull = true
                null
            }

            fillNull -> null

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("shortCumsum")
internal fun DataColumn<Short>.cumSumImpl(): DataColumn<Int> {
    var sum = 0
    return map {
        sum += it
        sum
    }
}

@JvmName("cumsumShortNullable")
internal fun DataColumn<Short?>.cumSumImpl(skipNA: Boolean): DataColumn<Int?> {
    var sum = 0
    var fillNull = false
    return map {
        when {
            it == null -> {
                if (!skipNA) fillNull = true
                null
            }

            fillNull -> null

            else -> {
                sum += it
                sum
            }
        }
    }
}

@JvmName("longCumsum")
internal fun DataColumn<Long>.cumSumImpl(): DataColumn<Long> {
    var sum = 0L
    return map {
        sum += it
        sum
    }
}

@JvmName("cumsumLongNullable")
internal fun DataColumn<Long?>.cumSumImpl(skipNA: Boolean): DataColumn<Long?> {
    var sum = 0L
    var fillNull = false
    return map {
        when {
            it == null -> {
                if (!skipNA) fillNull = true
                null
            }

            fillNull -> null

            else -> {
                sum += it
                sum
            }
        }
    }
}

/**
 * Double(?) -> Double
 * Float(?) -> Float
 * Short(?), Byte(?) -> Int(?)
 * T : Number(?) -> T(?)
 */
public val cumSumTypeConversion: CalculateReturnType = { type, _ ->
    when (type.withNullability(false)) {
        // type changes to Int, carrying nullability
        typeOf<Short>(), typeOf<Byte>() -> typeOf<Int>().withNullability(type.isMarkedNullable)

        // type remains the same, carrying nullability
        typeOf<Int>(), typeOf<Long>(), typeOf<Number>() -> type

        // type remains the same, but nulls are turned into NaN, so no more nullability
        typeOf<Double>(), typeOf<Float>() -> type.withNullability(false)

        // CumSum for empty column or column with just nulls remains unchanged
        nothingType -> type

        else ->
            error("Unable to compute the cumSum for ${renderType(type)}, Only primitive numbers are supported.")
    }
}

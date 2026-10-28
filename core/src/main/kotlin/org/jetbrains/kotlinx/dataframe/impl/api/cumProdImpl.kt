package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/** Promotes byte input and default values to Int while retaining declared nullability. */
internal fun DataColumn<Byte?>.cumProdByteImpl(skipNA: Boolean): DataColumn<Int?> =
    cumProdImpl(skipNA, typeOf<Int>().withNullability(type().isMarkedNullable), { it.toInt() }, { a, b -> a * b })

/** Promotes short input and default values to Int while retaining declared nullability. */
internal fun DataColumn<Short?>.cumProdShortImpl(skipNA: Boolean): DataColumn<Int?> =
    cumProdImpl(skipNA, typeOf<Int>().withNullability(type().isMarkedNullable), { it.toInt() }, { a, b -> a * b })

/** Computes Int products with ordinary wrapping arithmetic. */
internal fun DataColumn<Int?>.cumProdIntImpl(skipNA: Boolean): DataColumn<Int?> =
    cumProdImpl(skipNA, type(), { it }, { a, b -> a * b })

/** Computes Long products with ordinary wrapping arithmetic. */
internal fun DataColumn<Long?>.cumProdLongImpl(skipNA: Boolean): DataColumn<Long?> =
    cumProdImpl(skipNA, type(), { it }, { a, b -> a * b })

/** Computes Float products with IEEE 754 arithmetic, retaining nullable input representation. */
internal fun DataColumn<Float?>.cumProdFloatImpl(skipNA: Boolean): DataColumn<Float?> =
    cumProdImpl(skipNA, type(), { it }, { a, b -> a * b })

/** Computes Double products with IEEE 754 arithmetic, retaining nullable input representation. */
internal fun DataColumn<Double?>.cumProdDoubleImpl(skipNA: Boolean): DataColumn<Double?> =
    cumProdImpl(skipNA, type(), { it }, { a, b -> a * b })

/** Scans once; only missing input cells are skipped, not NaN produced by arithmetic. */
private inline fun <T : Number, R : Number> DataColumn<T?>.cumProdImpl(
    skipNA: Boolean,
    resultType: KType,
    convertValue: (T) -> R,
    multiply: (R, R) -> R,
): DataColumn<R?> {
    var product: R? = null
    var missing: R? = null
    var propagateMissing = false
    val values = ArrayList<R?>(size())

    for (value in this) {
        values.add(
            when {
                propagateMissing -> missing

                value == null || value.isNaN -> {
                    val converted = value?.let(convertValue)
                    if (!skipNA) {
                        missing = converted
                        propagateMissing = true
                    }
                    converted
                }

                else -> {
                    val converted = convertValue(value)
                    val previous = product
                    product = if (previous == null) converted else multiply(previous, converted)
                    product
                }
            },
        )
    }

    return DataColumn.createValueColumn(name(), values, resultType, defaultValue = defaultValue()?.let(convertValue))
}

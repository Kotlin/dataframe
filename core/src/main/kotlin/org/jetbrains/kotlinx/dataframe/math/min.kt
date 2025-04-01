package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.preserveReturnTypeNullIfEmpty
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNaBy
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.text.compareTo

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.minOrNull(type: KType, skipNaN: Boolean): T? {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in min function. This should not occur.")
    }
    if (!type.isIntraComparable()) {
        error(
            "Encountered non-comparable type ${
                renderType(type)
            } in min function. Only self-comparable types are supported.",
        )
    }

    return when {
        // filter out NaNs if requested
        skipNaN && type.canBeNaN -> this.filterNot { it.isNaN }.minOrNull()

        // make sure that NaN is returned if it's in the sequence
        type == typeOf<Float>() -> (this as Sequence<Float>).minOrNull() as T?

        // make sure that NaN is returned if it's in the sequence
        type == typeOf<Double>() -> (this as Sequence<Double>).minOrNull() as T?

        else -> this.minOrNull()
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <C : Comparable<C>> Sequence<C?>.indexOfMin(type: KType, skipNaN: Boolean): Int =
    when {
        // filter out NaNs if requested
        skipNaN && type.canBeNaN -> indexOfBestNotNaBy { this < it }

        // make sure the index of the first NaN is returned if it's in the sequence
        type.canBeNaN -> indexOfBestNotNullBy { this.isNaN || (!it.isNaN && this < it) }

        else -> indexOfBestNotNullBy { this < it }
    }

/** T: Comparable<T> -> T(?) */
internal val minTypeConversion = preserveReturnTypeNullIfEmpty

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.IsBetterThan
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.CalculateReturnType
import org.jetbrains.kotlinx.dataframe.impl.bestByOrNull
import org.jetbrains.kotlinx.dataframe.impl.bestNotNaByOrNull
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNaBy
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import org.jetbrains.kotlinx.dataframe.impl.isIntraComparable
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

// region min

@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.minOrNull(type: KType, skipNaN: Boolean): T? =
    bestOrNull(type, skipNaN, "min") { this < it }

@Suppress("UNCHECKED_CAST")
internal fun <C : Comparable<C>> Sequence<C?>.indexOfMin(type: KType, skipNaN: Boolean): Int =
    indexOfBest(type, skipNaN) { this < it }

/** T: Comparable<T> -> T(?) */
internal val minTypeConversion: CalculateReturnType = { type, isEmpty ->
    val type = type.withNullability(false)
    when {
        type.isIntraComparable() -> type.withNullability(isEmpty)
        else -> error("min can not be calculated for type ${renderType(type)}")
    }
}

// endregion

// region max

@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.maxOrNull(type: KType, skipNaN: Boolean): T? =
    bestOrNull(type, skipNaN, "max") { this > it }

@Suppress("UNCHECKED_CAST")
internal fun <C : Comparable<C>> Sequence<C?>.indexOfMax(type: KType, skipNaN: Boolean): Int =
    indexOfBest(type, skipNaN) { this > it }

/** T: Comparable<T> -> T(?) */
internal val maxTypeConversion: CalculateReturnType = { type, isEmpty ->
    val type = type.withNullability(false)
    when {
        type.isIntraComparable() -> type.withNullability(isEmpty)
        else -> error("max can not be calculated for type ${renderType(type)}")
    }
}

// endregion

// region common

@Suppress("UNCHECKED_CAST")
private fun <T : Comparable<T>> Sequence<T>.bestOrNull(
    type: KType,
    skipNaN: Boolean,
    name: String,
    isBetterThan: IsBetterThan<T>,
): T? {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in $name function. This should not occur.")
    }
    if (!type.isIntraComparable()) {
        error(
            "Encountered non-comparable type ${
                renderType(type)
            } in $name function. Try converting the values to the same type `T : Comparable<T>`.",
        )
    }

    return when {
        // filter out NaNs if requested
        skipNaN && type.canBeNaN -> bestNotNaByOrNull(isBetterThan)

        // make sure that NaN is returned if it's in the sequence
        type.canBeNaN -> bestByOrNull { this.isNaN || (!it.isNaN && this.isBetterThan(it)) }

        else -> bestByOrNull(isBetterThan)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <C : Comparable<C>> Sequence<C?>.indexOfBest(
    type: KType,
    skipNaN: Boolean,
    isBetterThan: IsBetterThan<C>,
): Int =
    when {
        // filter out NaNs if requested
        skipNaN && type.canBeNaN -> indexOfBestNotNaBy(isBetterThan)

        // make sure the index of the first NaN is returned if it's in the sequence
        type.canBeNaN -> indexOfBestNotNullBy { this.isNaN || (!it.isNaN && this.isBetterThan(it)) }

        else -> indexOfBestNotNullBy(isBetterThan)
    }

// endregion

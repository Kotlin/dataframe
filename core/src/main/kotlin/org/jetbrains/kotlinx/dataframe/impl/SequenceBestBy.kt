package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.api.isNA
import org.jetbrains.kotlinx.dataframe.documentation.NA

internal typealias IsBetterThan<C> = C.(other: C) -> Boolean

// region indexOfBestBy

/**
 * Returns the index of the first element in this sequence that is not null and is better than all previous elements.
 *
 * Returns -1 if there are no elements non-`null` elements in [this].
 *
 * @param isBetterThan A function defining what it means for a value to be "better" than another.
 */
internal inline fun <C> Sequence<C>.indexOfBestNotNullBy(isBetterThan: IsBetterThan<C & Any>): Int {
    val bestIndex = indexOfBestBy { other -> this != null && (other == null || this.isBetterThan(other)) }
    // catch case where all values are null
    return if (bestIndex == 0 && first() == null) -1 else bestIndex
}

/**
 * Returns the index of the first element in this sequence
 * that is not [NA (null or NaN)][NA] and is better than all previous elements.
 *
 * Returns -1 if there are no elements non-`NA` elements in [this].
 *
 * @param isBetterThan A function defining what it means for a value to be "better" than another.
 */
internal inline fun <C> Sequence<C>.indexOfBestNotNaBy(isBetterThan: IsBetterThan<C & Any>): Int {
    val bestIndex = indexOfBestBy { other -> !this.isNA() && (other.isNA() || this.isBetterThan(other)) }
    // catch case where all values are NA (null or NaN)
    return if (bestIndex == 0 && first().isNA()) -1 else bestIndex
}

/**
 * Returns the index of the first element in this sequence that is better than all previous elements.
 *
 * Returns -1 if there are no elements in [this].
 *
 * Considers the first element in [this] the best if [isBetterThan] only returns `false`.
 *
 * @param isBetterThan A function defining what it means for a value to be "better" than another.
 */
internal inline fun <C : Any?> Sequence<C>.indexOfBestBy(isBetterThan: IsBetterThan<C>): Int =
    when {
        none() -> -1

        else -> {
            var bestIndex = 0
            reduceIndexed { index, bestFound, next ->
                if (next.isBetterThan(bestFound)) {
                    bestIndex = index
                    next
                } else {
                    bestFound
                }
            }
            bestIndex
        }
    }

// endregion

// bestNotNaBy

internal inline fun <C : Any> Sequence<C?>.bestNotNaBy(isBetterThan: IsBetterThan<C>): C =
    bestNotNaByOrElse(isBetterThan) { throw NoSuchElementException("Sequence is empty") }

internal inline fun <C : Any> Sequence<C?>.bestNotNaByOrNull(isBetterThan: IsBetterThan<C>): C? =
    bestNotNaByOrElse(isBetterThan) { null }

@Suppress("UNCHECKED_CAST")
internal inline fun <C : R, R : Any?> Sequence<C?>.bestNotNaByOrElse(
    isBetterThan: IsBetterThan<C & Any>,
    ifEmptyOrAllNa: () -> R,
): R =
    bestByOrElse(
        isBetterThan = { other -> !this.isNA() && (other.isNA() || this.isBetterThan(other)) },
        ifEmpty = ifEmptyOrAllNa,
    ) ?: ifEmptyOrAllNa()

// endregion

// bestNotNullBy

internal inline fun <C : Any> Sequence<C?>.bestNotNullBy(isBetterThan: IsBetterThan<C>): C =
    bestNotNullByOrElse(isBetterThan) { throw NoSuchElementException("Sequence is empty") }

internal inline fun <C : Any> Sequence<C?>.bestNotNullByOrNull(isBetterThan: IsBetterThan<C>): C? =
    bestNotNullByOrElse(isBetterThan) { null }

@Suppress("UNCHECKED_CAST")
internal inline fun <C : R, R : Any?> Sequence<C?>.bestNotNullByOrElse(
    isBetterThan: IsBetterThan<C & Any>,
    ifEmptyOrAllNull: () -> R,
): R =
    bestByOrElse(
        isBetterThan = { other -> this != null && (other == null || this.isBetterThan(other)) },
        ifEmpty = ifEmptyOrAllNull,
    ) ?: ifEmptyOrAllNull()

// endregion

// region bestBy

internal inline fun <C : Any?> Sequence<C>.bestBy(isBetterThan: IsBetterThan<C>): C =
    bestByOrElse(isBetterThan) { throw NoSuchElementException("Sequence is empty") }

internal inline fun <C : Any?> Sequence<C>.bestByOrNull(isBetterThan: IsBetterThan<C>): C? =
    bestByOrElse(isBetterThan) { null }

internal inline fun <C : R, R : Any?> Sequence<C>.bestByOrElse(isBetterThan: IsBetterThan<C>, ifEmpty: () -> R): R =
    when {
        none() -> ifEmpty()

        else -> reduce { bestFound, next ->
            if (next.isBetterThan(bestFound)) next else bestFound
        }
    }

// endregion

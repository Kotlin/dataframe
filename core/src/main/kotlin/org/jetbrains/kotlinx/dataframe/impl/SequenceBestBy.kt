package org.jetbrains.kotlinx.dataframe.impl

internal typealias IsBetterThan<C> = C.(other: C) -> Boolean

// TODO
internal inline fun <C : Any> pickingNonNull(crossinline isBetterThan: (IsBetterThan<C>)): IsBetterThan<C?> =
    { other: C? -> this != null && other != null && this.isBetterThan(other) }

// region indexOfBestBy

/**
 * Returns the index of the first element in this sequence that is not null and is better than all previous elements.
 *
 * Returns -1 if there are no elements non-`null` elements in [this].
 *
 * @param isBetterThan A function defining what it means for a value to be "better" than another.
 */
internal inline fun <C : Any> Sequence<C?>.indexOfBestNotNullBy(crossinline isBetterThan: IsBetterThan<C>): Int {
    val bestIndex = indexOfBestBy(pickingNonNull(isBetterThan))
    // catch case where all values are null
    return if (bestIndex == 0 && first() == null) -1 else bestIndex
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

// bestNotNullBy

internal inline fun <C : Any> Sequence<C?>.bestNotNullBy(crossinline isBetterThan: IsBetterThan<C>): C =
    bestNotNullByOrElse(isBetterThan) { throw NoSuchElementException("Sequence is empty") }

internal inline fun <C : Any> Sequence<C?>.bestNotNullByOrNull(crossinline isBetterThan: IsBetterThan<C>): C? =
    bestNotNullByOrElse(isBetterThan) { null }

@Suppress("UNCHECKED_CAST")
internal inline fun <C : R, R : Any?> Sequence<C?>.bestNotNullByOrElse(
    crossinline isBetterThan: IsBetterThan<C & Any>,
    ifEmptyOrAllNull: () -> R,
): R =
    bestByOrElse(
        isBetterThan = pickingNonNull(isBetterThan),
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

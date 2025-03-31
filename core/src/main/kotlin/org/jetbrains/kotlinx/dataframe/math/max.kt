package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.preserveReturnTypeNullIfEmpty
import org.jetbrains.kotlinx.dataframe.impl.canBeNaN
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNaBy
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.maxOrNull(type: KType, skipNaN: Boolean): T? {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in max function. This should not occur.")
    }
    return if (skipNaN && type.canBeNaN) {
        filterNot { it.isNaN }
    } else {
        this
    }.maxOrNull()
}

internal fun <C : Comparable<C>> Sequence<C?>.indexOfMax(type: KType, skipNaN: Boolean): Int =
    if (skipNaN && type.canBeNaN) {
        indexOfBestNotNaBy { this > it }
    } else {
        indexOfBestNotNullBy { this > it }
    }

/** T: Comparable<T> -> T(?) */
internal val maxTypeConversion = preserveReturnTypeNullIfEmpty

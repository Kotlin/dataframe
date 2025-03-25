package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.preserveReturnTypeNullIfEmpty
import org.jetbrains.kotlinx.dataframe.impl.indexOfBestNotNullBy
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.maxOrNull(type: KType): T? {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in max function. This should not occur.")
    }
    return this.maxOrNull()
}

internal fun <C : Comparable<C>> Sequence<C?>.indexOfMax(): Int = indexOfBestNotNullBy { this > it }

/** T: Comparable<T> -> T(?) */
internal val maxTypeConversion = preserveReturnTypeNullIfEmpty

package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.preserveReturnTypeNullIfEmpty
import org.jetbrains.kotlinx.dataframe.impl.renderType
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T>.minOrNull(type: KType): T? {
    if (type.isMarkedNullable) {
        error("Encountered nullable type ${renderType(type)} in min function. This should not occur.")
    }
    return this.minOrNull()
}

/** T: Comparable<T> -> T(?) */
internal val minTypeConversion = preserveReturnTypeNullIfEmpty

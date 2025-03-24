package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.preserveReturnTypeNullIfEmpty
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T : Comparable<T>> Sequence<T?>.minOrNull(type: KType): T? {
    if (type.isMarkedNullable) {
        return filterNotNull().minOrNull(type.withNullability(false))
    }
    return (this as Sequence<T>).minOrNull()
}

/** T: Comparable<T> -> T(?) */
internal val minTypeConversion = preserveReturnTypeNullIfEmpty

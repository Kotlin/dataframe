package org.jetbrains.dataframe

import java.math.BigDecimal
import kotlin.reflect.KType

public inline fun <reified T : Comparable<T>> Iterable<T?>.median(type: KType): T? {
    val sorted = if (type.isMarkedNullable) filterNotNull().sorted() else (this as Iterable<T>).sorted()
    val size = sorted.size
    if (size == 0) return null
    val index = size / 2
    if (index == 0 || size % 2 == 1) return sorted[index]
    return when (type.classifier) {
        Double::class -> ((sorted[index - 1] as Double + sorted[index] as Double) / 2.0) as T
        Int::class -> ((sorted[index - 1] as Int + sorted[index] as Int) / 2) as T
        Long::class -> ((sorted[index - 1] as Long + sorted[index] as Long) / 2L) as T
        Byte::class -> ((sorted[index - 1] as Byte + sorted[index] as Byte) / 2).toByte() as T
        BigDecimal::class -> ((sorted[index - 1] as BigDecimal + sorted[index] as BigDecimal) / BigDecimal(2)) as T
        else -> sorted[index - 1]
    }
}

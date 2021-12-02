package org.jetbrains.kotlinx.dataframe.impl

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.io.defaultPrecision
import org.jetbrains.kotlinx.dataframe.typeClass
import java.math.BigDecimal

internal fun <T : Number> DataColumn<T?>.precision(): Int {
    if (size() == 0) return 0
    return when (typeClass) {
        Double::class -> values().maxOf { (it as? Double)?.scale() ?: 1 }
        Float::class -> values().maxOf { (it as? Float)?.scale() ?: 1 }
        BigDecimal::class -> values().maxOf { (it as? BigDecimal)?.scale() ?: 1 }
        Number::class -> values().maxOf { (it as? Number)?.scale() ?: 0 }
        else -> 0
    }.coerceAtMost(defaultPrecision)
}

internal fun Double.scale() = if (isFinite()) toBigDecimal().scale() else 0
internal fun Float.scale() = if (isFinite()) toBigDecimal().scale() else 0

internal fun Number.scale(): Int = when (this) {
    is Double -> scale()
    is Float -> scale()
    is Int, is Long -> 0
    is BigDecimal -> scale()
    else -> 0
}

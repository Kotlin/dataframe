package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import kotlin.reflect.KType

/**
 * @param [kType] The target type of the values.
 * @param [needsFullConversion] Whether explicit conversion to [kType] is needed
 *   for the values to become the correct value type. If `false`, the values are already the right type,
 *   or a simple cast will suffice.
 */
internal data class ValueType(val kType: KType, val needsFullConversion: Boolean = false)

internal fun KType.toValueType(needsFullConversion: Boolean = false): ValueType = ValueType(this, needsFullConversion)

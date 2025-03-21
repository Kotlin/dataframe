package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import kotlin.reflect.KType

internal interface CommonAggregator<in Value, out Return> : Aggregator<Value, Return> {

    override fun calculateValueType(valueTypes: Set<KType>): KType = valueTypes.commonType(false)

    // heavy
    override fun calculateValueType(values: Iterable<Value?>): KType {
        var hasNulls = false
        val classes = values.mapNotNull {
            if (it == null) {
                hasNulls = true
                null
            } else {
                it.javaClass.kotlin
            }
        }
        return if (classes.isEmpty()) {
            nothingType(hasNulls)
        } else {
            classes.commonType(hasNulls)
        }
    }
}

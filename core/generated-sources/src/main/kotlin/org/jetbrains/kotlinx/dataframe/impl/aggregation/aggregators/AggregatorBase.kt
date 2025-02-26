package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.commonType
import kotlin.reflect.KType

/**
 * Base class for [aggregators][Aggregator].
 *
 * Aggregators are used to compute a single value from an [Iterable] of values, a single [DataColumn],
 * or multiple [DataColumns][DataColumn].
 *
 * @param name The name of this aggregator.
 * @param aggregator Functional argument for the [aggregate] function.
 */
internal abstract class AggregatorBase<Value, Return>(
    override val name: String,
    protected val aggregator: (values: Iterable<Value>, type: KType) -> Return?,
) : Aggregator<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [type] into account, and computes a single resulting value.
     * Uses [aggregator] to compute the result.
     */
    override fun aggregate(values: Iterable<Value>, type: KType): Return? = aggregator(values, type)

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     * Nulls are filtered out before calling the aggregation function with [Iterable] and [KType].
     */
    override fun aggregate(column: DataColumn<Value?>): Return? =
        if (column.hasNulls()) {
            aggregate(column.asSequence().filterNotNull().asIterable(), column.type())
        } else {
            aggregate(column.asIterable() as Iterable<Value>, column.type())
        }

    /**
     * Special case of [aggregate] with [Iterable] that calculates the common type of the values at runtime.
     * This is a heavy operation and should be avoided when possible.
     */
    fun aggregateCalculatingType(values: Iterable<Value>): Return? {
        var hasNulls = false
        val classes = values.mapNotNull {
            if (it == null) {
                hasNulls = true
                null
            } else {
                it.javaClass.kotlin
            }
        }
        return aggregate(values, classes.commonType(hasNulls))
    }

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * Must be overridden to use.
     */
    abstract override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return?
}

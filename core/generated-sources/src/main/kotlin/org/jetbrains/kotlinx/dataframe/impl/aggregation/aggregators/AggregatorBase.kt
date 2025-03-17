package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asIterable
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

/**
 * Abstract base class for [aggregators][Aggregator].
 *
 * Aggregators are used to compute a single value from an [Iterable] of values, a single [DataColumn],
 * or multiple [DataColumns][DataColumn].
 *
 * @param name The name of this aggregator.
 * @param aggregator Functional argument for the [aggregate] function. Nulls are filtered out before this is called.
 */
internal abstract class AggregatorBase<in Value, out Return>(
    override val name: String,
    protected val getReturnTypeOrNull: CalculateReturnTypeOrNull,
    protected val aggregator: Aggregate<Value, Return>,
) : Aggregator<Value, Return> {

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [type] into account,
     * filtering nulls (only if [type.isMarkedNullable][KType.isMarkedNullable]),
     * and computes a single resulting value.
     *
     * When using [AggregatorBase], this can be supplied by the [AggregatorBase.aggregator] argument.
     *
     * When the exact [type] is unknown, use [aggregateCalculatingType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregate(values: Iterable<Value?>, type: KType): Return =
        aggregator(
            // values =
            if (type.isMarkedNullable) {
                values.asSequence().filterNotNull().asIterable()
            } else {
                values as Iterable<Value & Any>
            },
            // type =
            type.withNullability(false),
        )

    /**
     * Function that can give the return type of [aggregate] as [KType], given the type of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * Uses [getReturnTypeOrNull] to calculate the return type.
     *
     * @param type The type of the input values.
     * @param emptyInput If `true`, the input values are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    override fun calculateReturnTypeOrNull(type: KType, emptyInput: Boolean): KType? =
        getReturnTypeOrNull(type.withNullability(false), emptyInput)

    /**
     * Aggregates the data in the given column and computes a single resulting value.
     *
     * Nulls are filtered out by default, then [aggregate] (with [Iterable] and [KType]) is called.
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregate(column: DataColumn<Value?>): Return =
        aggregate(
            values = column.asIterable(),
            type = column.type(),
        )

    /** Special case of [aggregate][org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator.aggregate] with [Iterable] that calculates the common type of the values at runtime.
     * Without [valueTypes], this is a heavy operation and should be avoided when possible.
     *
     * @param values The values to be aggregated.
     * @param valueTypes The types of the values.
     *   If provided, this can be used to avoid calculating the types of [values][org.jetbrains.kotlinx.dataframe.values] at runtime with reflection.
     *   It should contain all types of [values][org.jetbrains.kotlinx.dataframe.values].
     * If `null` or empty, the types of [values][org.jetbrains.kotlinx.dataframe.values] will be calculated at runtime (heavy!). */
    override fun aggregateCalculatingType(values: Iterable<Value?>, valueTypes: Set<KType>?): Return {
        val commonType = if (valueTypes != null && valueTypes.isNotEmpty()) {
            valueTypes.commonType(false)
        } else {
            var hasNulls = false
            val classes = values.mapNotNull {
                if (it == null) {
                    hasNulls = true
                    null
                } else {
                    it.javaClass.kotlin
                }
            }
            if (classes.isEmpty()) {
                nothingType(hasNulls)
            } else {
                classes.commonType(hasNulls)
            }
        }
        return aggregate(values, commonType)
    }

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     * Must be overridden to use.
     */
    abstract override fun aggregate(columns: Iterable<DataColumn<Value?>>): Return

    /**
     * Function that can give the return type of [aggregate] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    abstract override fun calculateReturnTypeOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType?
}

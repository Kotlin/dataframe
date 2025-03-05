package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * [Aggregator] made specifically for number calculations.
 *
 * Nulls are filtered from columns.
 *
 * When called on multiple columns (with potentially different [Number] types),
 * this [Aggregator] works in two steps:
 *
 * First, it aggregates within a [DataColumn]/[Iterable] with their (given) [Number] type,
 * and then between different columns
 * using the results of the first and the newly calculated [unified number][UnifyingNumbers] type of those results.
 *
 * ```
 * Iterable<Column<Number?>>
 *     -> Iterable<Iterable<Number>> // nulls filtered out
 *     -> aggregator(Iterable<Number>, colType) // called on each iterable
 *     -> Iterable<Return> // nulls filtered out
 *     -> aggregator(Iterable<Return>, unified number type of common valueType)
 *     -> Return?
 * ```
 *
 * @param name The name of this aggregator.
 * @param aggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
 *   While it takes a [Number] argument, you can assume that all values are of the same specific type, however,
 *   this type can be different for different calls to [aggregator].
 */
internal class TwoStepNumbersAggregator<out Return : Number>(
    name: String,
    getReturnTypeOrNull: (type: KType, emptyInput: Boolean) -> KType?,
    aggregator: (values: Iterable<Number>, numberType: KType) -> Return?,
) : AggregatorBase<Number, Return>(name, getReturnTypeOrNull, aggregator) {

    override fun aggregate(values: Iterable<Number>, type: KType): Return? {
        require(type.isSubtypeOf(typeOf<Number?>())) {
            "${TwoStepNumbersAggregator::class.simpleName}: Type $type is not a subtype of Number?"
        }
        return super.aggregate(values, type)
    }

    override fun aggregate(columns: Iterable<DataColumn<Number?>>): Return? {
        val (values, types) = columns.mapNotNull { col ->
            val value = aggregate(col) ?: return@mapNotNull null
            val type = calculateReturnTypeOrNull(
                type = col.type().withNullability(false),
                emptyInput = col.size() == 0,
            ) ?: value::class.starProjectedType // heavy fallback type calculation

            value to type
        }.unzip()

        return aggregateCalculatingType(
            values = values,
            valueTypes = types.toSet(),
        )
    }

    /**
     * Special case of [aggregate] with [Iterable] that calculates the [unified number type][UnifyingNumbers]
     * of the values at runtime.
     * This is a heavy operation and should be avoided when possible.
     * If provided, [valueTypes] can be used to avoid calculating the types of [values] at runtime.
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateCalculatingType(values: Iterable<Number>, valueTypes: Set<KType>?): Return? {
        val commonType = (valueTypes ?: values.types()).unifiedNumberType().withNullability(false)
        return aggregate(
            values = values.convertToUnifiedNumberType(commonType),
            type = commonType,
        )
    }

    override val preservesType = false

    class Factory<out Return : Number>(
        private val getReturnTypeOrNull: (type: KType, emptyInput: Boolean) -> KType?,
        private val aggregate: Iterable<Number>.(numberType: KType) -> Return?,
    ) : AggregatorProvider<TwoStepNumbersAggregator<Return>> by AggregatorProvider({ name ->
            TwoStepNumbersAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregator = aggregate,
            )
        })
}

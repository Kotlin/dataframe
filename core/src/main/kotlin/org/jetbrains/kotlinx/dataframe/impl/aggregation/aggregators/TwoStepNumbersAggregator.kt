package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.isEmpty
import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.Companion.PRIMITIVES_ONLY
import org.jetbrains.kotlinx.dataframe.impl.anyNull
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.isNothing
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberTypeOrNull
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

/**
 * [Aggregator] made specifically for number calculations.
 * Mixed number types are [unified][UnifyingNumbers] to [primitives][PRIMITIVES_ONLY].
 *
 * Nulls are filtered out.
 *
 * When called on multiple columns (with potentially mixed [Number] types),
 * this [Aggregator] works in two steps:
 *
 * First, it aggregates within a [DataColumn]/[Iterable] with their (given) [Number] type
 * (potentially unifying the types), and then between different columns
 * using the results of the first and the newly calculated [unified number][UnifyingNumbers] type of those results.
 *
 * ```
 * Iterable<Column<Number?>>
 *     -> Iterable<Iterable<Number>> // nulls filtered out
 *     -> aggregator(Iterable<specific Number>, unified number type of common colType) // called on each iterable
 *     -> Iterable<Return> // nulls filtered out
 *     -> aggregator(Iterable<specific Return>, unified number type of common valueType)
 *     -> Return
 * ```
 *
 * @param name The name of this aggregator.
 * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
 * @param aggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
 *   While it takes a [Number] argument, you can assume that all values are of the same specific type, however,
 *   this type can be different for different calls to [aggregator].
 */
internal class TwoStepNumbersAggregator<out Return : Number?>(
    name: String,
    getReturnTypeOrNull: CalculateReturnTypeOrNull,
    aggregator: Aggregate<Number, Return>,
) : AggregatorBase<Number, Return>(name, getReturnTypeOrNull, aggregator) {

    /**
     * Aggregates the data in the multiple given columns and computes a single resulting value.
     *
     * This function calls [aggregator] on each column and then again on the results.
     *
     * After the first aggregation, the number types are found by [calculateReturnTypeOrNull] and then
     * unified using [aggregateCalculatingType].
     */
    override fun aggregate(columns: Iterable<DataColumn<Number?>>): Return {
        val (values, types) = columns.mapNotNull { col ->
            val value = aggregate(col) ?: return@mapNotNull null
            val type = calculateReturnTypeOrNull(
                type = col.type().withNullability(false),
                emptyInput = col.isEmpty,
            ) ?: value::class.starProjectedType // heavy fallback type calculation

            value to type
        }.unzip()

        return aggregateCalculatingType(
            values = values,
            valueTypes = types.toSet(),
        )
    }

    /**
     * Function that can give the return type of [aggregate] with columns as [KType],
     * given the multiple types of the input.
     * This allows aggregators to avoid runtime type calculations.
     *
     * @param colTypes The types of the input columns.
     * @param colsEmpty If `true`, all the input columns are considered empty. This often affects the return type.
     * @return The return type of [aggregate] as [KType].
     */
    @Suppress("UNCHECKED_CAST")
    override fun calculateReturnTypeOrNull(colTypes: Set<KType>, colsEmpty: Boolean): KType? {
        val typesAfterStepOne = colTypes.map { type ->
            calculateReturnTypeOrNull(type = type.withNullability(false), emptyInput = colsEmpty)
        }
        if (typesAfterStepOne.anyNull()) return null
        val typeSet = (typesAfterStepOne as List<KType>).toSet()
        val unifiedType = typeSet.unifiedNumberTypeOrNull(PRIMITIVES_ONLY)
            ?.withNullability(false)
            ?: throw IllegalArgumentException(
                "Cannot calculate the $name of the number types: ${typeSet.joinToString { renderType(it) }}. " +
                    "Note, only primitive number types are supported in statistics.",
            )

        return unifiedType
    }

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [type] into account, and computes a single resulting value.
     *
     * Nulls are filtered out (only if [type.isMarkedNullable][KType.isMarkedNullable]).
     *
     * Uses [aggregator] to compute the result.
     *
     * This function is modified to call [aggregateCalculatingType] when it encounters mixed number types.
     * This is not optimal and should be avoided by calling [aggregateCalculatingType] with known number types directly.
     *
     * When the exact [type] is unknown, use [aggregateCalculatingType].
     */
    override fun aggregate(values: Iterable<Number?>, type: KType): Return {
        require(type.isSubtypeOf(typeOf<Number?>())) {
            "${TwoStepNumbersAggregator::class.simpleName}: Type $type is not a subtype of Number?"
        }
        return when (type.withNullability(false)) {
            // If the type is not a specific number, but rather a mixed Number, we unify the types first.
            // This is heavy and could be avoided by calling aggregate with a specific number type
            // or calling aggregateCalculatingType with all known number types
            typeOf<Number>() -> aggregateCalculatingType(values)

            // Nothing can occur when values are empty
            nothingType -> super.aggregate(values, type)

            !in primitiveNumberTypes -> throw IllegalArgumentException(
                "Cannot calculate $name of ${renderType(type)}, only primitive numbers are supported.",
            )

            else -> super.aggregate(values, type)
        }
    }

    /**
     * Special case of [aggregate] with [Iterable] that calculates the [unified number type][UnifyingNumbers]
     * of the values at runtime and converts all numbers to this type before aggregating.
     * Without [valueTypes], this is a heavy operation and should be avoided when possible.
     *
     * @param values The numbers to be aggregated.
     * @param valueTypes The types of the numbers.
     *   If provided, this can be used to avoid calculating the types of [values] at runtime with reflection.
     *   It should contain all types of [values].
     *   If `null` or empty, the types of [values] will be calculated at runtime (heavy!).
     */
    @Suppress("UNCHECKED_CAST")
    override fun aggregateCalculatingType(values: Iterable<Number?>, valueTypes: Set<KType>?): Return {
        val valueTypes = valueTypes?.takeUnless { it.isEmpty() } ?: values.types()
        val unifiedType = valueTypes.unifiedNumberTypeOrNull(PRIMITIVES_ONLY)
            ?: throw IllegalArgumentException(
                "Cannot calculate the $name of the number types: ${valueTypes.joinToString { renderType(it) }}. " +
                    "Note, only primitive number types are supported in statistics.",
            )

        if (unifiedType.isSubtypeOf(typeOf<Double?>()) &&
            (typeOf<ULong>() in valueTypes || typeOf<Long>() in valueTypes)
        ) {
            logger.warn {
                "Number unification of Long -> Double happened during aggregation. Loss of precision may have occurred."
            }
        }
        if (unifiedType.withNullability(false) !in primitiveNumberTypes && !unifiedType.isNothing) {
            throw IllegalArgumentException(
                "Cannot calculate $name of ${renderType(unifiedType)}, only primitive numbers are supported.",
            )
        }

        return super.aggregate(
            values = values.convertToUnifiedNumberType(commonNumberType = unifiedType),
            type = unifiedType,
        )
    }

    /**
     * Creates [TwoStepNumbersAggregator].
     *
     * @param getReturnTypeOrNull Functional argument for the [calculateReturnTypeOrNull] function.
     * @param aggregator Functional argument for the [aggregate] function, used within a [DataColumn] or [Iterable].
     */
    class Factory<out Return : Number?>(
        private val getReturnTypeOrNull: CalculateReturnTypeOrNull,
        private val aggregate: Aggregate<Number, Return>,
    ) : AggregatorProvider<TwoStepNumbersAggregator<Return>> by AggregatorProvider({ name ->
            TwoStepNumbersAggregator(
                name = name,
                getReturnTypeOrNull = getReturnTypeOrNull,
                aggregator = aggregate,
            )
        })
}

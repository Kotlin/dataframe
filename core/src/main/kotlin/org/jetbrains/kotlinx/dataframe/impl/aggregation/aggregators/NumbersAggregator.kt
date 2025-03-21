package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.Companion.PRIMITIVES_ONLY
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorBase
import org.jetbrains.kotlinx.dataframe.impl.isNothing
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberTypeOrNull
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

private val logger = KotlinLogging.logger { }

internal interface NumbersAggregator<out Return : Number?> : Aggregator<Number, Return> {

    // copy this to the implementation with super<AggregatorBase>.ref
//    override fun aggregateSingleIterable(values: Iterable<Number?>, valueType: KType): Return =
//        aggregateSingleIterableOfNumbers(values, valueType, super.ref)

    /**
     * Base function of [Aggregator].
     *
     * Aggregates the given values, taking [valueType] into account, and computes a single resulting value.
     *
     * Nulls are filtered out (only if [type.isMarkedNullable][KType.isMarkedNullable]).
     *
     * Uses [aggregator] to compute the result.
     *
     * This function is modified to call [aggregateCalculatingType] when it encounters mixed number types.
     * This is not optimal and should be avoided by calling [aggregateCalculatingType] with known number types directly.
     *
     * When the exact [valueType] is unknown, use [aggregateCalculatingType].
     */
    fun aggregateSingleIterableOfNumbers(
        values: Iterable<Number?>,
        valueType: KType,
        superAggregateSingleIterable: (Iterable<Number?>, KType) -> @UnsafeVariance Return,
    ): Return {
        require(valueType.isSubtypeOf(typeOf<Number?>())) {
            "${TwoStepNumbersAggregator::class.simpleName}: Type $valueType is not a subtype of Number?"
        }
        return when (valueType.withNullability(false)) {
            // If the type is not a specific number, but rather a mixed Number, we unify the types first.
            // This is heavy and could be avoided by calling aggregate with a specific number type
            // or calling aggregateCalculatingType with all known number types
            typeOf<Number>() -> superAggregateSingleIterable(values, calculateValueType(values))

            // Nothing can occur when values are empty
            nothingType -> superAggregateSingleIterable(values, valueType)

            !in primitiveNumberTypes -> throw IllegalArgumentException(
                "Cannot calculate $name of ${renderType(valueType)}, only primitive numbers are supported.",
            )

            else -> superAggregateSingleIterable(values, valueType)
        }
    }

    override fun calculateValueType(values: Iterable<Number?>): KType = calculateValueType(values.types().toSet())

    override fun calculateValueType(valueTypes: Set<KType>): KType {
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
        return unifiedType
    }
}

package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.inputHandlers

import org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.AggregatorInputHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.ValueType
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.aggregate
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.toValueType
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
import org.jetbrains.kotlinx.dataframe.impl.isMixedNumber
import org.jetbrains.kotlinx.dataframe.impl.isNothing
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveOrMixedNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.primitiveNumberTypes
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.impl.types
import org.jetbrains.kotlinx.dataframe.impl.unifiedNumberTypeOrNull
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 * Input handler for aggregators that can handle any (mixed) primitive [Number] type of input.
 *
 * When calculating the value type,
 * it will try to find the common type in terms of [number unification][UnifyingNumbers].
 * Preprocessing will handle the conversion of the values in the input to this unified number type.
 */
internal class NumberInputHandler<out Return : Any?> : AggregatorInputHandler<Number, Return> {

    /**
     * Preprocesses the input values before aggregation.
     *
     * - If [ValueType.kType] is a primitive number type and [ValueType.needsFullConversion],
     * then the values are converted to [ValueType.kType].
     *
     * - If [ValueType.kType] is given as [Number]`(?)`, then we first calculate the unified type and convert to that.
     * This is heavy and should be avoided if possible.
     *
     * @throws IllegalArgumentException if the input type is not [Number]`(?)` or a primitive number type.
     */
    override fun preprocessAggregation(
        values: Sequence<Number?>,
        valueType: ValueType,
    ): Pair<Sequence<Number?>, KType> {
        require(valueType.kType.isSubtypeOf(typeOf<Number?>())) {
            "Type $valueType is not a subtype of Number?, only primitive numbers are supported in ${aggregator!!.name}."
        }
        return when (valueType.kType.withNullability(false)) {
            // If the type is not a specific number, but rather a mixed Number, we unify the types first.
            // This is heavy and could be avoided by calling aggregate with a specific number type
            // or calling aggregateCalculatingType with all known number types
            typeOf<Number>() -> {
                val unifiedType = calculateValueType(values).kType

                // If calculateValueType returns Number(?),
                // it means the values cannot be unified to a primitive number type
                require(!unifiedType.isMixedNumber()) {
                    "Types ${
                        values.asIterable().types().toSet()
                    } are not all primitive numbers, only those are supported in ${aggregator!!.name}."
                }

                val unifiedValues = values.convertToUnifiedNumberType(
                    UnifiedNumberTypeOptions.PRIMITIVES_ONLY,
                    unifiedType,
                )
                Pair(unifiedValues, unifiedType)
            }

            // Nothing can occur when values are empty
            nothingType -> Pair(values, valueType.kType)

            !in primitiveNumberTypes -> throw IllegalArgumentException(
                "Cannot calculate ${aggregator!!.name} of ${
                    renderType(valueType.kType)
                }, only primitive numbers are supported in statistics.",
            )

            else -> {
                val unifiedValues = if (valueType.needsFullConversion) {
                    values.convertToUnifiedNumberType(
                        UnifiedNumberTypeOptions.PRIMITIVES_ONLY,
                        valueType.kType,
                    )
                } else {
                    values
                }
                Pair(unifiedValues, valueType.kType)
            }
        }
    }

    /**
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it in terms of [number unification][UnifyingNumbers]
     *
     * @throws IllegalArgumentException if the input type is not [Number]`(?)` or a primitive number type.
     * @return The (primitive) unified number type of the input values.
     *   If no valid unification can be found or the input is solely [Number]`(?)`, the type [Number]`(?)` is returned.
     */
    override fun calculateValueType(valueTypes: Set<KType>): ValueType {
        val unifiedType = valueTypes.unifiedNumberTypeOrNull(UnifiedNumberTypeOptions.PRIMITIVES_ONLY)
            ?: typeOf<Number>().withNullability(valueTypes.any { it.isMarkedNullable })
        if (!unifiedType.isPrimitiveOrMixedNumber() && !unifiedType.isNothing) {
            throw IllegalArgumentException(
                "Cannot calculate ${aggregator!!.name} of ${
                    renderType(unifiedType)
                }, only primitive numbers are supported.",
            )
        }

        return unifiedType.toValueType(needsFullConversion = valueTypes.singleOrNull() != unifiedType)
    }

    /**
     * WARNING: HEAVY!
     *
     * If the specific [ValueType] of the input is not known, but you still want to call [aggregate],
     * this function can be called to calculate it in terms of [number unification][UnifyingNumbers]
     * by getting the types of [values] at runtime.
     * This is heavy because it uses reflection on each value.
     *
     * @throws IllegalArgumentException if the input type is not [Number]`(?)` or a primitive number type.
     * @return The (primitive) unified number type of the input values.
     *   If no valid unification can be found or the input is solely [Number]`(?)`, the type [Number]`(?)` is returned.
     */
    override fun calculateValueType(values: Sequence<Number?>): ValueType =
        calculateValueType(values.asIterable().types().toSet())

    override var aggregator: Aggregator<Number, @UnsafeVariance Return>? = null
}

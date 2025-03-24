package org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.impl.UnifiedNumberTypeOptions.Companion.PRIMITIVES_ONLY
import org.jetbrains.kotlinx.dataframe.impl.convertToUnifiedNumberType
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

internal class NumberInputHandler<out Return> : AggregatorInputHandler<Number, Return> {

    override fun preprocessAggregation(
        values: Sequence<Number?>,
        valueType: ValueType,
    ): Pair<Sequence<Number?>, KType> {
        require(valueType.kType.isSubtypeOf(typeOf<Number?>())) {
            "${NumberInputHandler::class.simpleName}: Type $valueType is not a subtype of Number?"
        }
        return when (valueType.kType.withNullability(false)) {
            // If the type is not a specific number, but rather a mixed Number, we unify the types first.
            // This is heavy and could be avoided by calling aggregate with a specific number type
            // or calling aggregateCalculatingType with all known number types
            typeOf<Number>() -> {
                val unifiedType = calculateValueType(values).kType
                val unifiedValues = values.convertToUnifiedNumberType(PRIMITIVES_ONLY, unifiedType)
                Pair(unifiedValues, unifiedType)
            }

            // Nothing can occur when values are empty
            nothingType -> Pair(values, valueType.kType)

            !in primitiveNumberTypes -> throw IllegalArgumentException(
                "Cannot calculate ${aggregator!!.name} of ${
                    renderType(valueType.kType)
                }, only primitive numbers are supported.",
            )

            else -> {
                val unifiedValues = if (valueType.needsFullConversion) {
                    values.convertToUnifiedNumberType(PRIMITIVES_ONLY, valueType.kType)
                } else {
                    values
                }
                Pair(unifiedValues, valueType.kType)
            }
        }
    }

    // heavy!
    override fun calculateValueType(values: Sequence<Number?>): ValueType =
        calculateValueType(values.asIterable().types().toSet())

    override fun calculateValueType(valueTypes: Set<KType>): ValueType {
        val unifiedType = valueTypes.unifiedNumberTypeOrNull(PRIMITIVES_ONLY)
            ?: throw IllegalArgumentException(
                "Cannot calculate the ${aggregator!!.name} of the number types: ${
                    valueTypes.joinToString { renderType(it) }
                }. Note, only primitive number types are supported in statistics.",
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
                "Cannot calculate ${aggregator!!.name} of ${
                    renderType(
                        unifiedType,
                    )
                }, only primitive numbers are supported.",
            )
        }

        return unifiedType.toValueType(needsFullConversion = valueTypes.singleOrNull() != unifiedType)
    }

    override var aggregator: Aggregator<Number, @UnsafeVariance Return>? = null
}

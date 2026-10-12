package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.QuantileMethod
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveNumber
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.impl.renderType
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.full.withNullability
import org.jetbrains.kotlinx.dataframe.math.quantileOrNull as sequenceQuantileOrNull

/** Delegates selection to the existing engine without converting the selected value type. */
@Suppress("UNCHECKED_CAST")
internal fun <T : Comparable<T & Any>?> DataColumn<T>.quantileSelectingOrNullImpl(
    p: Double,
    method: QuantileMethod.Selecting,
    skipNaN: Boolean,
): T? {
    requireQuantileProbability(p)
    val estimator: QuantileEstimationMethod.Selecting = when (method) {
        QuantileMethod.Selecting.R1 -> QuantileEstimationMethod.R1
        QuantileMethod.Selecting.R3 -> QuantileEstimationMethod.R3
    }
    return asSequence().filterNotNull().sequenceQuantileOrNull(
        p,
        type().withNullability(false),
        skipNaN,
        estimator,
        "quantile",
    ) as T?
}

/** Delegates interpolation to the existing primitive-number engine after validating the declared type. */
internal fun <T> DataColumn<T>.quantileInterpolatingOrNullImpl(
    p: Double,
    method: QuantileMethod.Interpolating,
    skipNaN: Boolean,
): Double?
    where T : Comparable<T & Any>?, T : Number? {
    requireQuantileProbability(p)
    val inputType = type().withNullability(false)
    require(inputType.isPrimitiveNumber() || inputType == nothingType) {
        "Quantile interpolation requires primitive numbers, but the column type is ${renderType(inputType)}."
    }
    val estimator: QuantileEstimationMethod.Interpolating = when (method) {
        QuantileMethod.Interpolating.R7 -> QuantileEstimationMethod.R7
        QuantileMethod.Interpolating.R8 -> QuantileEstimationMethod.R8
    }
    return asSequence().filterNotNull().sequenceQuantileOrNull(
        p,
        inputType,
        skipNaN,
        estimator,
        "quantile",
    ) as Double?
}

/** Validates new public quantile calls before empty-input shortcuts in the shared engine. */
private fun requireQuantileProbability(p: Double) {
    require(p in 0.0..1.0) { "Quantile probability p must be finite and in [0, 1], but was $p." }
}

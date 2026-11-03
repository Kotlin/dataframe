package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.RowExpressionsLink
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

// region Pivot

public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    delegate {
        with(expression)
    }

// endregion

// region ReducedPivot

/**
 * Returns a [DataRow] with the [pivot] keys of this [ReducedPivot] as columns,
 * containing the value of [expression] computed on the reduced row
 * of the corresponding group.
 *
 * @include [PivotDocs.ReducedPivotFinalStepSnippet]
 *
 * [expression] is a [RowExpression] computed only on the single reduced row of each group;
 * this row is both the receiver and the argument (`it`) of the expression.
 * See {@include [RowExpressionsLink]}
 *
 * For more information: {@include [DocumentationUrls.PivotReducing]}
 *
 * Don't confuse this with [with][Pivot.with] on a [Pivot], which computes the expression on all rows
 * of each group.
 *
 * See also:
 * - [values][ReducedPivot.values] — takes the values of the selected columns of the reduced rows.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotWith_properties]
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotWith_strings]
 *
 * @param [T] The schema marker type of the pivoted [DataFrame].
 * @param [V] The type of the values computed by [expression].
 * @param [expression] The [RowExpression] computing the value from the reduced row.
 * @return A new [DataRow] with the computed values under their [pivot] keys.
 */
public inline fun <T, reified V> ReducedPivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    pivot.delegate {
        reduce(reducer).with(expression)
    }

// endregion

// region PivotGroupBy

public inline fun <T, reified V> PivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return aggregate { internal().withExpr(type, emptyPath(), expression) }
}

// endregion

// region ReducedPivotGroupBy

public inline fun <T, reified V> ReducedPivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return pivot.aggregate {
        val value = reducer(this)?.let {
            val value = expression(it, it)
            if (value is AnyColumnReference) {
                value.getValue(it)
            } else {
                value
            }
        }
        internal().yield(emptyPath(), value, type)
    }
}

// endregion

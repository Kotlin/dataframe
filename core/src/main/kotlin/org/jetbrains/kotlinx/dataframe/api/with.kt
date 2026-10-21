package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

// region Pivot

/**
 * Aggregates this [Pivot] by evaluating the given [row expression][expression] on every row of each pivot group.
 * A single result is used directly; multiple results are collected for the corresponding pivot cell.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * @param V The result type of [expression], captured at runtime for constructing the result columns.
 * @param expression The expression evaluated for each row within each pivot group.
 * @return A new [DataRow] with pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    delegate {
        with(expression)
    }

// endregion

// region ReducedPivot

/**
 * Transforms this [ReducedPivot] by evaluating the given [row expression][expression] on the single row
 * selected by the reducer in each pivot group.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * @param V The result type of [expression], captured at runtime for constructing the result columns.
 * @param expression The expression evaluated on each selected row.
 * @return A new [DataRow] with pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> ReducedPivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    pivot.delegate {
        reduce(reducer).with(expression)
    }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [PivotGroupBy] by evaluating the given [row expression][expression] on every row
 * in each group defined by the grouping and pivot keys.
 * A single result is used directly; multiple results are collected for the corresponding pivot cell.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * @param V The result type of [expression], captured at runtime for constructing the result columns.
 * @param expression The expression evaluated for each row within each pivot cell group.
 * @return A new [DataFrame] with the grouping key columns, one row per grouping key combination,
 * and pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> PivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return aggregate { internal().withExpr(type, emptyPath(), expression) }
}

// endregion

// region ReducedPivotGroupBy

/**
 * Transforms this [ReducedPivotGroupBy] by evaluating the given [row expression][expression] on the single row
 * selected by the reducer in each group defined by the grouping and pivot keys.
 * If [expression] returns a column reference, it is resolved to that column's value in the selected row.
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * @param V The result type of [expression], captured at runtime for constructing the result columns.
 * @param expression The expression evaluated on each selected row.
 * @return A new [DataFrame] with the grouping key columns, one row per grouping key combination,
 * and pivot keys as columns or column groups containing the computed results.
 */
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

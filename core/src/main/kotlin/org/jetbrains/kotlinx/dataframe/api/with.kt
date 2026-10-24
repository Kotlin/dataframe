package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

// region docs

/**
 * For each pivot cell: an empty group yields `null` (type `V?`), a group of exactly one row yields
 * that single value (type `V`), and a group of several rows yields a `List<V>`.
 */
@ExcludeFromSources
private typealias PivotWithOneOrManyResultSnippet = Nothing

/**
 * For each pivot cell: a group with no row selected by the reducer yields `null` (type `V?`),
 * otherwise the value produced by the expression (type `V`).
 */
@ExcludeFromSources
private typealias PivotWithReducedResultSnippet = Nothing

/**
 * If [expression\] returns a column reference, it is resolved to that column's value in the row
 * the expression was evaluated on.
 */
@ExcludeFromSources
private typealias PivotWithColumnReferenceSnippet = Nothing

// endregion

// region Pivot

/**
 * Aggregates this [Pivot] by evaluating the given [row expression][expression] on every row of each pivot group.
 *
 * @include [PivotWithOneOrManyResultSnippet]
 *
 * @include [PivotWithColumnReferenceSnippet]
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * Check out the [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.withOnPivot_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.withOnPivot_strings]
 *
 * @param [V] The result type of [expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated for each row within each pivot group.
 * @return A new [DataRow] with pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    delegate {
        with(expression)
    }

// endregion

// region ReducedPivot

/**
 * Aggregates this [ReducedPivot] by evaluating the given [row expression][expression] on the single row
 * selected by the reducer in each pivot group.
 *
 * @include [PivotWithReducedResultSnippet]
 *
 * @include [PivotWithColumnReferenceSnippet]
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotWith_properties]
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.pivotWith_strings]
 *
 * @param [V] The result type of [expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated on each selected row.
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
 *
 * @include [PivotWithOneOrManyResultSnippet]
 *
 * @include [PivotWithColumnReferenceSnippet]
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.withOnPivotGroupBy]
 *
 * @param [V] The result type of [expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated for each row within each pivot cell group.
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
 * Aggregates this [ReducedPivotGroupBy] by evaluating the given [row expression][expression] on the single row
 * selected by the reducer in each group defined by the grouping and pivot keys.
 *
 * @include [PivotWithReducedResultSnippet]
 *
 * @include [PivotWithColumnReferenceSnippet]
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples.withOnReducedPivotGroupBy]
 *
 * @param [V] The result type of [expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated on each selected row.
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

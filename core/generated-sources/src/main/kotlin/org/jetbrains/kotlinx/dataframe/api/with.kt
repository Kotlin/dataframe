package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.aggregation.internal
import org.jetbrains.kotlinx.dataframe.impl.aggregation.withExpr
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import kotlin.reflect.typeOf

// region docs

// endregion

// region Pivot

/**
 * Aggregates this [<code>Pivot</code>][Pivot] by evaluating the given [<code>row expression</code>][expression] on every row of each pivot group.
 *
 * For each pivot cell: an empty group yields `null` (type `V?`), a group of exactly one row yields
 * that single value (type `V`), and a group of several rows yields a `List<V>`.
 *
 * If [expression] returns a column reference, it is resolved to that column's value in the row
 * the expression was evaluated on.
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * Check out the [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * ### Examples
 *
 * ```kotlin
 * df.pivot { isHappy }.with { name.lastName }
 * ```
 * ```kotlin
 * df.pivot("isHappy").with { "name"["lastName"]<String>() }
 * ```
 *
 * @param [V] The result type of [<code>expression</code>][expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated for each row within each pivot group.
 * @return A new [<code>DataRow</code>][DataRow] with pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> Pivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    delegate {
        with(expression)
    }

// endregion

// region ReducedPivot

/**
 * Returns a [<code>DataRow</code>][DataRow] with the [<code>pivot</code>][pivot] keys of this [<code>ReducedPivot</code>][ReducedPivot] as columns,
 * containing the value of [<code>expression</code>][expression] computed on the reduced row
 * of the corresponding group.
 *
 *
 *
 * This is the final step of [<code>Pivot reducing</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing].
 * A [<code>ReducedPivot</code>][org.jetbrains.kotlinx.dataframe.api.ReducedPivot] is created by a reducing method, like [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first], [<code>last</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.last],
 * [<code>minBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.minBy], [<code>maxBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.maxBy], [<code>medianBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.medianBy], or
 * [<code>percentileBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.percentileBy], which selects at most one row in each [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] group.
 *
 * If no row is selected in a group (for example, if no row of the group satisfies
 * the predicate of [<code>first</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.first]), the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key of this group is still present
 * in the resulting [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], but all the values under it are `null`.
 *
 * Check out [<code>`Pivot grammar`</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * [<code>expression</code>][expression] is a [<code>RowExpression</code>][RowExpression] computed only on the single reduced row of each group;
 * this row is both the receiver and the argument (`it`) of the expression.
 * See [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow]
 *
 * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 *
 * Don't confuse this with [<code>with</code>][Pivot.with] on a [<code>Pivot</code>][Pivot], which computes the expression on all rows
 * of each group.
 *
 * See also:
 * - [<code>values</code>][ReducedPivot.values] — takes the values of the selected columns of the reduced rows.
 *
 * ### Examples
 *
 * ```kotlin
 * df.pivot { isHappy }
 *     .maxBy { age }
 *     .with { name.firstName + " " + name.lastName }
 * ```
 *
 * ```kotlin
 * df.pivot("isHappy")
 *     .maxBy("age")
 *     .with { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
 * ```
 *
 * @param [T] The schema marker type of the pivoted [<code>DataFrame</code>][DataFrame].
 * @param [V] The type of the values computed by [<code>expression</code>][expression].
 * @param [expression] The [<code>RowExpression</code>][RowExpression] computing the value from the reduced row.
 * @return A new [<code>DataRow</code>][DataRow] with the computed values under their [<code>pivot</code>][pivot] keys.
 */
public inline fun <T, reified V> ReducedPivot<T>.with(noinline expression: RowExpression<T, V>): DataRow<T> =
    pivot.delegate {
        reduce(reducer).with(expression)
    }

// endregion

// region PivotGroupBy

/**
 * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] by evaluating the given [<code>row expression</code>][expression] on every row
 * in each group defined by the grouping and pivot keys.
 *
 * For each pivot cell: an empty group yields `null` (type `V?`), a group of exactly one row yields
 * that single value (type `V`), and a group of several rows yields a `List<V>`.
 *
 * If [expression] returns a column reference, it is resolved to that column's value in the row
 * the expression was evaluated on.
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { city }.pivot { isHappy }.with { name.lastName }
 * df.groupBy("city").pivot("isHappy").with { "name"["lastName"]<String>() }
 * ```
 *
 * @param [V] The result type of [<code>expression</code>][expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated for each row within each pivot cell group.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns, one row per grouping key combination,
 * and pivot keys as columns or column groups containing the computed results.
 */
public inline fun <T, reified V> PivotGroupBy<T>.with(noinline expression: RowExpression<T, V>): DataFrame<T> {
    val type = typeOf<V>()
    return aggregate { internal().withExpr(type, emptyPath(), expression) }
}

// endregion

// region ReducedPivotGroupBy

/**
 * Aggregates this [<code>ReducedPivotGroupBy</code>][ReducedPivotGroupBy] by evaluating the given [<code>row expression</code>][expression] on the single row
 * selected by the reducer in each group defined by the grouping and pivot keys.
 *
 * For each pivot cell: a group with no row selected by the reducer yields `null` (type `V?`),
 * otherwise the value produced by the expression (type `V`).
 *
 * If [expression] returns a column reference, it is resolved to that column's value in the row
 * the expression was evaluated on.
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { city }.pivot { isHappy }.maxBy { age }.with { name.lastName }
 * df.groupBy("city").pivot("isHappy").maxBy("age").with { "name"["lastName"]<String>() }
 * ```
 *
 * @param [V] The result type of [<code>expression</code>][expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated on each selected row.
 * @return A new [<code>DataFrame</code>][DataFrame] with the grouping key columns, one row per grouping key combination,
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

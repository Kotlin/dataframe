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
 * Aggregates this [<code>ReducedPivot</code>][ReducedPivot] by evaluating the given [<code>row expression</code>][expression] on the single row
 * selected by the reducer in each pivot group.
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
 * df.pivot { isHappy }
 *     .maxBy { age }
 *     .with { name.firstName + " " + name.lastName }
 * ```
 * ```kotlin
 * df.pivot("isHappy")
 *     .maxBy("age")
 *     .with { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
 * ```
 *
 * @param [V] The result type of [<code>expression</code>][expression], captured at runtime for constructing the result columns.
 * @param [expression] The expression evaluated on each selected row.
 * @return A new [<code>DataRow</code>][DataRow] with pivot keys as columns or column groups containing the computed results.
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

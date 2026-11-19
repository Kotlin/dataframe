package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDslLink
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ConfiguredAggregateColumn
import org.jetbrains.kotlinx.dataframe.util.AGGREGATE_DSL_PATH
import org.jetbrains.kotlinx.dataframe.util.AGGREGATE_DSL_PATH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.PATH_OF_IMPORT

/**
 * A specialized [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] for selecting columns to aggregate
 * and specifying how their aggregation results should be stored.
 *
 *
 *
 *
 *
 * The [<code>Columns For Aggregate Selection DSL</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl] allows selecting columns
 * to aggregate, just like the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * It also allows configuring how the aggregation result of each selected column is stored:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.into] sets the name
 *   (or the [<code>path</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], see [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]) of the resulting column.
 *   If several columns are selected, their results are put into a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   with this name, keeping the names of the columns.
 * - [<code>default</code>][org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl.default] sets the value used in [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] aggregations
 *   instead of `null` results, including the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] cells without any rows.
 *
 * ### Examples
 *
 * ```kotlin
 * // Store the maximum "age" of each city in the "maxAge" column
 * df.groupBy { city }.maxFor { age into "maxAge" }
 *
 * // Put the maximum "age" and "weight" values into the "max" column group
 * df.groupBy { isHappy }.maxFor { (age and weight) into "max" }
 *
 * // Put the maximum "age" and "weight" values into the "max" column group,
 * // nested in the "stats" column group
 * df.groupBy { isHappy }.maxFor { (age and weight) into pathOf("stats", "max") }
 *
 * // Use 0 for the pivot cells without any "weight" values
 * df.groupBy { city }.pivot { isHappy }.maxFor { weight default 0 }
 * ```
 */
public interface ColumnsForAggregateSelectionDsl<out T> : ColumnsSelectionDsl<T> {

    /**
     * Sets the value used instead of `null` aggregation results of the selected columns
     * in [<code>pivot</code>][pivot] aggregations (of a [<code>Pivot</code>][Pivot], a [<code>PivotGroupBy</code>][PivotGroupBy], or a [<code>pivot</code>][pivot] inside an aggregation),
     * including the [<code>pivot</code>][pivot] cells without any rows.
     *
     * Outside of [<code>pivot</code>][pivot] aggregations, `null` results are not replaced.
     *
     * @param [defaultValue] The value to use instead of `null` results.
     */
    public infix fun <C> ColumnSet<C>.default(defaultValue: C): ColumnSet<C> =
        ConfiguredAggregateColumn.withDefault(this, defaultValue)

    /**
     * Sets the value used instead of `null` aggregation results of the selected column
     * in [<code>pivot</code>][pivot] aggregations (of a [<code>Pivot</code>][Pivot], a [<code>PivotGroupBy</code>][PivotGroupBy], or a [<code>pivot</code>][pivot] inside an aggregation),
     * including the [<code>pivot</code>][pivot] cells without any rows.
     *
     * Outside of [<code>pivot</code>][pivot] aggregations, `null` results are not replaced.
     *
     * @param [defaultValue] The value to use instead of `null` results.
     */
    public infix fun <C> SingleColumn<C>.default(defaultValue: C): SingleColumn<C> =
        ConfiguredAggregateColumn.withDefault(this, defaultValue)

    @Deprecated(AGGREGATE_DSL_PATH, ReplaceWith(AGGREGATE_DSL_PATH_REPLACE, PATH_OF_IMPORT), DeprecationLevel.WARNING)
    public fun path(vararg names: String): ColumnPath = ColumnPath(names.asList())

    /**
     * Stores the aggregation results of the selected columns under the given [<code>name</code>][name].
     *
     * If a single column is selected, its result is stored in a column named [<code>name</code>][name].
     * If several columns are selected, their results are put into a [<code>column group</code>][ColumnGroup]
     * named [<code>name</code>][name], keeping the original names of the columns.
     *
     * @param [name] The name of the resulting column or column group.
     */
    public infix fun <C> ColumnSet<C>.into(name: String): ColumnSet<C> =
        ConfiguredAggregateColumn.withPath(this, pathOf(name))

    /**
     * Stores the aggregation result of the selected column in a column named [<code>newName</code>][newName].
     *
     * @param [newName] The name of the resulting column.
     */
    override infix fun <C> SingleColumn<C>.into(newName: String): SingleColumn<C> =
        ConfiguredAggregateColumn.withPath(this, pathOf(newName))

    /**
     * Stores the aggregation results of the selected columns at the given [<code>path</code>][path].
     *
     * If a single column is selected, its result is stored in a column at [<code>path</code>][path].
     * If several columns are selected, their results are put into a [<code>column group</code>][ColumnGroup]
     * at [<code>path</code>][path], keeping the original names of the columns.
     *
     * @param [path] The [<code>ColumnPath</code>][ColumnPath] of the resulting column or column group,
     *   for example, created with [<code>pathOf</code>][pathOf].
     */
    public infix fun <C> ColumnSet<C>.into(path: ColumnPath): ColumnSet<C> =
        ConfiguredAggregateColumn.withPath(this, path)

    /**
     * Stores the aggregation result of the selected column in a column at the given [<code>path</code>][path].
     *
     * @param [path] The [<code>ColumnPath</code>][ColumnPath] of the resulting column,
     *   for example, created with [<code>pathOf</code>][pathOf].
     */
    public infix fun <C> SingleColumn<C>.into(path: ColumnPath): SingleColumn<C> =
        ConfiguredAggregateColumn.withPath(this, path)
}

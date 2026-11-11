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
 * {@comment Description of the DSL without [default][ColumnsForAggregateSelectionDsl.default],
 *    which only has an effect in pivot aggregations. KDoc-snippet.}
 *
 * The [Columns For Aggregate Selection DSL][ColumnsForAggregateSelectionDsl] allows selecting columns
 * to aggregate, just like the {@include [ColumnsSelectionDslLink]}.
 * It also allows configuring how the aggregation result of each selected column is stored:
 * - [into][ColumnsForAggregateSelectionDsl.into] sets the name
 *   (or the [path][ColumnPath], see [pathOf]) of the resulting column.
 *   If several columns are selected, their results are put into a [column group][ColumnGroup]
 *   with this name, keeping the names of the columns.
 */
@ExcludeFromSources
internal typealias ColumnsForAggregateSelectionDslWithoutDefaultSnippet = Nothing

/**
 * {@comment Full description of the DSL. KDoc-snippet.}
 *
 * @include [ColumnsForAggregateSelectionDslWithoutDefaultSnippet]
 * - [default][ColumnsForAggregateSelectionDsl.default] sets the value used in [pivot] aggregations
 *   instead of `null` results, including the [pivot] cells without any rows.
 */
@ExcludeFromSources
internal typealias ColumnsForAggregateSelectionDslSnippet = Nothing

/**
 * A specialized {@include [ColumnsSelectionDslLink]} for selecting columns to aggregate
 * and specifying how their aggregation results should be stored.
 *
 * @include [ColumnsForAggregateSelectionDslSnippet]
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples.columnsForAggregateSelectionDsl]
 */
public interface ColumnsForAggregateSelectionDsl<out T> : ColumnsSelectionDsl<T> {

    /**
     * Sets the value used instead of `null` aggregation results of the selected columns
     * in [pivot] aggregations (of a [Pivot], a [PivotGroupBy], or a [pivot] inside an aggregation),
     * including the [pivot] cells without any rows.
     *
     * Outside of [pivot] aggregations, `null` results are not replaced.
     *
     * @param [defaultValue] The value to use instead of `null` results.
     */
    public infix fun <C> ColumnSet<C>.default(defaultValue: C): ColumnSet<C> =
        ConfiguredAggregateColumn.withDefault(this, defaultValue)

    /**
     * Sets the value used instead of `null` aggregation results of the selected column
     * in [pivot] aggregations (of a [Pivot], a [PivotGroupBy], or a [pivot] inside an aggregation),
     * including the [pivot] cells without any rows.
     *
     * Outside of [pivot] aggregations, `null` results are not replaced.
     *
     * @param [defaultValue] The value to use instead of `null` results.
     */
    public infix fun <C> SingleColumn<C>.default(defaultValue: C): SingleColumn<C> =
        ConfiguredAggregateColumn.withDefault(this, defaultValue)

    @Deprecated(AGGREGATE_DSL_PATH, ReplaceWith(AGGREGATE_DSL_PATH_REPLACE, PATH_OF_IMPORT), DeprecationLevel.WARNING)
    public fun path(vararg names: String): ColumnPath = ColumnPath(names.asList())

    /**
     * Stores the aggregation results of the selected columns under the given [name].
     *
     * If a single column is selected, its result is stored in a column named [name].
     * If several columns are selected, their results are put into a [column group][ColumnGroup]
     * named [name], keeping the original names of the columns.
     *
     * @param [name] The name of the resulting column or column group.
     */
    public infix fun <C> ColumnSet<C>.into(name: String): ColumnSet<C> =
        ConfiguredAggregateColumn.withPath(this, pathOf(name))

    /**
     * Stores the aggregation result of the selected column in a column named [newName].
     *
     * @param [newName] The name of the resulting column.
     */
    override infix fun <C> SingleColumn<C>.into(newName: String): SingleColumn<C> =
        ConfiguredAggregateColumn.withPath(this, pathOf(newName))

    /**
     * Stores the aggregation results of the selected columns at the given [path].
     *
     * If a single column is selected, its result is stored in a column at [path].
     * If several columns are selected, their results are put into a [column group][ColumnGroup]
     * at [path], keeping the original names of the columns.
     *
     * @param [path] The [ColumnPath] of the resulting column or column group,
     *   for example, created with [pathOf].
     */
    public infix fun <C> ColumnSet<C>.into(path: ColumnPath): ColumnSet<C> =
        ConfiguredAggregateColumn.withPath(this, path)

    /**
     * Stores the aggregation result of the selected column in a column at the given [path].
     *
     * @param [path] The [ColumnPath] of the resulting column,
     *   for example, created with [pathOf].
     */
    public infix fun <C> SingleColumn<C>.into(path: ColumnPath): SingleColumn<C> =
        ConfiguredAggregateColumn.withPath(this, path)
}

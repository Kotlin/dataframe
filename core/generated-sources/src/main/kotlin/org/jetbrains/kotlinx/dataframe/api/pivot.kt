package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar
import org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotGroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotInAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * Splits the rows of this [DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [columns] of the original [DataFrame].
 *
 * Returns a [Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [column groups][ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [DataFrame]).
 *
 * Works like [DataFrame.groupBy] but groups rows horizontally.
 *
 * A [Pivot] can then be:
 * * [reduced][Reducing] into a [DataRow], where each group is collapsed into a single representative row;
 * * [aggregated][Aggregation] into a [DataRow], where each group is transformed into a new row of derived values;
 * * [grouped][Grouping] into a [PivotGroupBy] structure, which combines [pivot] and [groupBy] operations
 *   and then reduced or aggregated into a [DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [expr][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [named][org.jetbrains.kotlinx.dataframe.api.named]) :
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 */
internal interface PivotDocs {

    /**
     * ## [pivot][pivot] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * ### Create [Pivot]
     *
     * [**`pivot`**][pivot]**`(`**`inward: `[`Boolean`][Boolean]**` = true) {  `**`pivotColumns: `[`PivotColumnsSelector`][PivotColumnsSelector]**` }`**
     *
     * ### Reduce [Pivot] into [DataRow]
     *
     * [Pivot][Pivot]`.`[**`minBy`**][Pivot.minBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`maxBy`**][Pivot.maxBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`first`**][Pivot.first]`  [ `**` {  `**`rowCondition: `[`RowFilter`][RowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`last`**][Pivot.last]`  [ `**`{  `**`rowCondition: `[`RowFilter`][RowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`medianBy`**][Pivot.medianBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`percentileBy`**][Pivot.percentileBy]**`(`**`percentile: `[`Double`][Double]**`)  {  `**`column: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][Pivot.with]**`  {  `**`rowExpression: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`values`**][Pivot.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * ### Aggregate [Pivot] into [DataRow]
     *
     * [Pivot][Pivot]`.`[**`count`**][Pivot.count]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`frames`**][Pivot.frames]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`with`**][Pivot.with]**`  {  `**`rowExpression: `[`RowExpression`][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`values`**][Pivot.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`aggregate`**][Pivot.aggregate]**`  {  `**`aggregations: `[`AggregateDsl`][AggregateDsl]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<aggregation_statistic>][PivotDocs.AggregationStatistics]
     *
     * ### Group [Pivot] into [PivotGroupBy] and reduce / aggregate it
     *
     * [Pivot][Pivot]`.`[**`groupBy`**][Pivot.groupBy]**`  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`groupByOther`**][Pivot.groupByOther]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`default`**][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<pivot_groupBy_reducer>][PivotGroupByDocs.Reducing]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<pivot_groupBy_aggregator>][PivotGroupByDocs.Aggregation]
     *
     * Check out [PivotGroupBy Grammar][PivotGroupByDocs.Grammar] for more information.
     */
    interface Grammar

    /**
     * ### [Pivot] reducing
     *
     * Each [Pivot] group can be collapsed into a single row and then concatenated
     * into a new [DataRow] with these row values (or their derived representation)
     * with [pivot] keys as top-level columns or as [column groups][ColumnGroup].
     *
     * Reducing is a specific case of [aggregation][Aggregation].
     *
     * First, choose a [Pivot] reducing method:
     * * [first][Pivot.first], [last][Pivot.last] — take the first or last row
     *   (optionally, the first or last one that satisfies a predicate) of each group;
     * * [minBy][Pivot.minBy] / [maxBy][Pivot.maxBy] — take the row with the minimum or maximum value
     *   of the given [RowExpression] evaluated on rows within each group;
     * * [medianBy][Pivot.medianBy] / [percentileBy][Pivot.percentileBy] — take the row with
     *   the median or a specific percentile value of the given [RowExpression] evaluated on rows within each group.
     *
     * These functions return a [ReducedPivot], which can then be transformed into a new [DataFrame]
     * containing a single combined row (either using the original reduced rows or their transformed versions)
     * through one of the following methods:
     * * [values][ReducedPivot.values] — creates a new row containing the values
     *   from the reduced rows in the selected columns and produces a [DataRow] of
     *   these values;
     * * [with][ReducedPivot.with] — computes a new value for each reduced row using a [RowExpression],
     *   and produces a [DataRow] containing these computed values.
     *
     * Each method returns a new [DataRow] with [pivot] keys as top-level columns
     * (or as [column groups][ColumnGroup]) and values composed of the reduced results from each group.
     *
     * Check out [`Pivot grammar`][Grammar].
     *
     * For more information: [See "Pivot` reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
     */
    interface Reducing

    /**
     * ### [Pivot] aggregation
     *
     * Each [Pivot] group can be aggregated — that is, transformed into a new value, [DataRow], or [DataFrame] —
     * and then concatenated into a single [DataRow] composed of these aggregated results,
     * with [pivot] keys as top-level columns or as [column groups][ColumnGroup].
     *
     * The following aggregation methods are available:
     * * [frames][Pivot.frames] — returns this [Pivot] as a [DataRow] with pivot keys as columns
     *   (or [column groups][ColumnGroup]) and corresponding groups stored as [FrameColumn]s;
     * * [values][Pivot.values] — creates a [DataRow] containing values collected into a single [List]
     *   from all rows of each group for the selected columns;
     * * [count][Pivot.count] — creates a [DataRow] containing the pivot key columns and an additional column
     *   with the number of rows in each corresponding group;
     * * [with][Pivot.with] — creates a [DataRow] containing values computed using a [RowExpression]
     *   across all rows of each group and collected into a single [List] for every group;
     * * [aggregate][Pivot.aggregate] — performs a set of custom aggregations using [AggregateDsl],
     *   allowing computation of one or more derived values per group;
     * * [Various aggregation statistics][AggregationStatistics] — predefined shortcuts
     *   for common statistical aggregations such as [sum][Pivot.sum], [mean][Pivot.mean],
     *   [median][Pivot.median], and others.
     *
     * Each of these methods returns a new [DataRow] with [pivot] keys as top-level columns
     * (or as [column groups][ColumnGroup]) and values representing the aggregated results of each group.
     *
     * Check out [`Pivot grammar`][Grammar].
     *
     * For more information: [See "Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation)
     */
    interface Aggregation

    /**
     * ### [Pivot] grouping
     *
     * [Pivot] can be grouped with [groupBy][Pivot.groupBy] method. It will produce a [PivotGroupBy].
     *
     * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] and [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy],
     * representing a matrix table with vertical [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
     * and horizontal [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
     * where each cell represents a group corresponding
     * to both the [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
     *
     * Reversed order of `pivot` and `groupBy`
     * (i.e., [DataFrame.pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [Pivot.groupBy][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [DataFrame.groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [GroupBy.pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
     * will produce the same result.
     *
     * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [reduced][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
     * or [aggregated][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * Check out [PivotGroupBy Grammar][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
     *
     * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
     */
    interface Grouping

    /**
     * ### [Pivot] aggregation statistics
     *
     * Provides predefined shortcuts for the most common statistical aggregation operations
     * that can be applied to each group within a [Pivot].
     *
     * Each function computes a statistic across the rows of a group and returns the result as
     * a new row of computed values in the resulting [DataFrame].
     *
     * * [count][Pivot.count] — calculate the number of rows in each group;
     * * [max][Pivot.max] / [maxOf][Pivot.maxOf] / [maxFor][Pivot.maxFor] —
     *   calculate the maximum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [min][Pivot.min] / [minOf][Pivot.minOf] / [minFor][Pivot.minFor] —
     *   calculate the minimum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [sum][Pivot.sum] / [sumOf][Pivot.sumOf] / [sumFor][Pivot.sumFor] —
     *   calculate the sum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [mean][Pivot.mean] / [meanOf][Pivot.meanOf] / [meanFor][Pivot.meanFor] —
     *   calculate the mean (average) of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [std][Pivot.std] / [stdOf][Pivot.stdOf] / [stdFor][Pivot.stdFor] —
     *   calculate the standard deviation of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [median][Pivot.median] / [medianOf][Pivot.medianOf] / [medianFor][Pivot.medianFor] —
     *   calculate the median of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [percentile][Pivot.percentile] / [percentileOf][Pivot.percentileOf] / [percentileFor][Pivot.percentileFor] —
     *   calculate a specified percentile of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group.
     *
     * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics)
     */
    interface AggregationStatistics
}

/**
 * A specialized [ColumnsSelectionDsl] that allows specifying [pivot] key ordering
 * using the [then] function.
 *
 * [PivotDsl][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [and][org.jetbrains.kotlinx.dataframe.api.and] produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [then] produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [Columns Selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Pivot by the "city" column
 * df.pivot { city }
 *
 * // Independent pivot by "city" and "lastName" (from the "name" column group)
 * df.pivot { city and name.lastName }
 *
 * // Hierarchical pivot by two columns with composite ("city", "lastName") keys
 * df.pivot { city then name.lastName }
 * ```
 */
public interface PivotDsl<out T> : ColumnsSelectionDsl<T> {

    /**
     * Specifies the ordering of the [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [other].
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> ColumnsResolver<C>.then(other: ColumnsResolver<C>): ColumnSet<C> =
        PivotChainColumnSet(this, other)

    /**
     * Specifies the ordering of the [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [other].
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> String.then(other: ColumnsResolver<C>): ColumnSet<C> = toColumnOf<C>() then other

    /**
     * Specifies the ordering of the [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [other].
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> ColumnsResolver<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

    /**
     * Specifies the ordering of the [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [other].
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun String.then(other: String): ColumnSet<Any?> = toColumnAccessor() then other.toColumnAccessor()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.then(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() then other

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnsResolver<C>.then(other: KProperty<C>): ColumnSet<C> = this then other.toColumnAccessor()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.then(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() then other.toColumnAccessor()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.then(other: String): ColumnSet<C> = toColumnAccessor() then other.toColumnOf()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> String.then(other: KProperty<C>): ColumnSet<C> = toColumnOf<C>() then other.toColumnAccessor()
}

// region DataFrame

// region pivot

/**
 * Splits the rows of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [columns] of the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * Works like [DataFrame.groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] but groups rows horizontally.
 *
 * A [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] can then be:
 * * [reduced][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing] into a [DataRow][org.jetbrains.kotlinx.dataframe.DataRow], where each group is collapsed into a single representative row;
 * * [aggregated][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] into a [DataRow][org.jetbrains.kotlinx.dataframe.DataRow], where each group is transformed into a new row of derived values;
 * * [grouped][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grouping] into a [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] and [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [expr][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [named][org.jetbrains.kotlinx.dataframe.api.named]) :
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 * ### This `pivot` Overload
 * Select or express pivot columns using the [PivotDsl].
 *
 * [PivotDsl][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [and][org.jetbrains.kotlinx.dataframe.api.and] produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [then] produces a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [Columns Selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Pivot by the "city" column
 * df.pivot { city }
 *
 * // Independent pivot by "city" and "lastName" (from the "name" column group)
 * df.pivot { city and name.lastName }
 *
 * // Hierarchical pivot by two columns with composite ("city", "lastName") keys
 * df.pivot { city then name.lastName }
 * ```
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
 *     or when the [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] has been grouped; `false` otherwise.
 * @param columns The [Pivot Columns Selector][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [Pivot] containing the unique values of the selected column as new columns
 * (or as [column groups][ColumnGroup] for multiple key columns),
 * with their corresponding groups of rows represented as [DataFrame]s.
 */
public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> =
    PivotImpl(this, columns, inward)

/**
 * Splits the rows of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [columns] of the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * Works like [DataFrame.groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] but groups rows horizontally.
 *
 * A [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] can then be:
 * * [reduced][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing] into a [DataRow][org.jetbrains.kotlinx.dataframe.DataRow], where each group is collapsed into a single representative row;
 * * [aggregated][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] into a [DataRow][org.jetbrains.kotlinx.dataframe.DataRow], where each group is transformed into a new row of derived values;
 * * [grouped][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grouping] into a [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] and [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [expr][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [named][org.jetbrains.kotlinx.dataframe.api.named]) :
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 * ### This `pivot` Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * * [pivot] with a single key column produces a [Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [pivot] with multiple keys combined using [and] produces a [Pivot]
 *   with independent [column groups][ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 *
 * For pivoting by multiple keys combinations from different columns, use the [pivot] overload with [PivotDsl].
 * ### Examples
 * ```kotlin
 * // Pivot by the "city" column
 * df.pivot("city")
 *
 * // Independent pivot by "city" and "lastName"
 * df.pivot("city", "lastName")
 * ```
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
 *     or when the [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] has been grouped; `false` otherwise.
 * @param columns The [Column Names][String] that defines which columns are used
 * as keys for pivoting.
 * @return A new [Pivot] containing the unique values of the selected column as new columns
 * (or as [column groups][ColumnGroup] for multiple key columns),
 * with their corresponding groups of rows represented as [DataFrame]s.
 */
public fun <T> DataFrame<T>.pivot(vararg columns: String, inward: Boolean? = null): Pivot<T> =
    pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivot(vararg columns: AnyColumnReference, inward: Boolean? = null): Pivot<T> =
    pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivot(vararg columns: KProperty<*>, inward: Boolean? = null): Pivot<T> =
    pivot(inward) { columns.toColumnSet() }

// endregion

// region pivotMatches

/**
 * Computes whether matching rows exist in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] for all unique values of the
 * selected [columns] across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * then [groups it by][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther] the remaining columns,
 * and produces a new [Boolean] matrix (in the form of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [groupByOther][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also:
 * * [pivotCounts][org.jetbrains.kotlinx.dataframe.api.pivotCounts], which performs a similar operation
 *   but counts the number of matching rows instead of checking for their presence
 *   to produce a count matrix.
 *
 * ### This `pivotMatches` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotMatches { city and name }
 * ```
 *
 * @param [inward] If `true` (default), the generated pivoted columns are nested inside the original column;
 *               if `false`, they are placed at the top level.
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a [Boolean] presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().matches()

/**
 * Computes whether matching rows exist in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] for all unique values of the
 * selected [columns] across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * then [groups it by][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther] the remaining columns,
 * and produces a new [Boolean] matrix (in the form of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [groupByOther][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also:
 * * [pivotCounts][org.jetbrains.kotlinx.dataframe.api.pivotCounts], which performs a similar operation
 *   but counts the number of matching rows instead of checking for their presence
 *   to produce a count matrix.
 *
 * ### This `pivotMatches` Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotMatches("city", "name")
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
 *     or when the [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] has been grouped; `false` otherwise.
 * @param [columns] The [Column Names][String] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <T> DataFrame<T>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<T> =
    pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivotMatches(vararg columns: AnyColumnReference, inward: Boolean = true): DataFrame<T> =
    pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> =
    pivotMatches(inward) { columns.toColumnSet() }

// endregion

// region pivotCounts

/**
 * Computes number of matching rows in this [DataFrame] for all unique values of the
 * selected [columns] (independently) across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot] operation on the specified [columns] of this [DataFrame],
 * then [groups it by][Pivot.groupByOther] the remaining columns,
 * and produces a new count matrix (in the form of a [DataFrame]).
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][DataFrame.pivot], [groupByOther][Pivot.groupByOther],
 * and [count][PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 */
internal interface DataFramePivotCountsCommonDocs

/**
 * Computes number of matching rows in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] for all unique values of the
 * selected [columns] (independently) across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * then [groups it by][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther] the remaining columns,
 * and produces a new count matrix (in the form of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [groupByOther][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches][org.jetbrains.kotlinx.dataframe.api.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotCounts { city and name }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
 *     or when the [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] has been grouped; `false` otherwise.
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <T> DataFrame<T>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().count()

/**
 * Computes number of matching rows in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] for all unique values of the
 * selected [columns] (independently) across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * then [groups it by][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther] the remaining columns,
 * and produces a new count matrix (in the form of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [groupByOther][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches][org.jetbrains.kotlinx.dataframe.api.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotCounts("city", "name")
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
 *     or when the [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] has been grouped; `false` otherwise.
 * @param [columns] The [Column Names][String] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <T> DataFrame<T>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<T> =
    pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivotCounts(vararg columns: AnyColumnReference, inward: Boolean = true): DataFrame<T> =
    pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> =
    pivotCounts(inward) { columns.toColumnSet() }

// endregion

// endregion

// region GroupBy

// region pivot

/**
 * Pivots the selected [columns] of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups.
 * Returns a [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 *
 * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] and [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * representing a matrix table with vertical [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
 * and horizontal [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
 * where each cell represents a group corresponding
 * to both the [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
 *
 * Reversed order of `pivot` and `groupBy`
 * (i.e., [DataFrame.pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [Pivot.groupBy][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [DataFrame.groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [GroupBy.pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
 * will produce the same result.
 *
 * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [reduced][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
 * or [aggregated][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [PivotGroupBy Grammar][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [expr][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [named][org.jetbrains.kotlinx.dataframe.api.named]) :
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `pivot` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are pivoted.
 * @return A new [PivotGroupBy] that preserves the original [groupBy] key columns
 * and pivots the provided columns.
 */
public fun <G> GroupBy<*, G>.pivot(inward: Boolean = true, columns: ColumnsSelector<G, *>): PivotGroupBy<G> =
    PivotGroupByImpl(this, columns, inward)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivot(vararg columns: AnyColumnReference, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

/**
 * Pivots the selected [columns] of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups.
 * Returns a [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 *
 * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] and [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * representing a matrix table with vertical [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
 * and horizontal [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
 * where each cell represents a group corresponding
 * to both the [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [Pivot][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
 *
 * Reversed order of `pivot` and `groupBy`
 * (i.e., [DataFrame.pivot][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [Pivot.groupBy][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [DataFrame.groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [GroupBy.pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
 * will produce the same result.
 *
 * [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [reduced][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
 * or [aggregated][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [PivotGroupBy Grammar][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [expr][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [named][org.jetbrains.kotlinx.dataframe.api.named]) :
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `pivot` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`gb`</code>`.`[pivot][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Column names][String] that defines which columns are pivoted.
 * @return A new [PivotGroupBy] that preserves the original [groupBy] key columns
 * and pivots the provided columns.
 */
public fun <G> GroupBy<*, G>.pivot(vararg columns: String, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivot(vararg columns: KProperty<*>, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

// endregion

// region pivotMatches

/**
 * Computes whether matching rows exist in groups of this [GroupBy] for all unique values of the
 * selected columns (independently) across all [groupBy] key combinations.
 *
 * Performs a [pivot][GroupBy.pivot] operation on the specified [columns] of this [GroupBy] groups,
 * and produces a new matrix-like [DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][GroupBy.pivot]
 * and [matches][PivotGroupBy.matches] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [pivotCounts][GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 */
internal interface GroupByPivotMatchesCommonDocs

/**
 * Computes whether matching rows exist in groups of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] for all unique values of the
 * selected columns (independently) across all [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] key combinations.
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] operation on the specified [columns] of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups,
 * and produces a new matrix-like [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [pivotCounts][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations
 * gb.pivotMatches { city and name }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <G> GroupBy<*, G>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).matches()

/**
 * Computes whether matching rows exist in groups of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] for all unique values of the
 * selected columns (independently) across all [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] key combinations.
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] operation on the specified [columns] of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups,
 * and produces a new matrix-like [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [pivotCounts][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations
 * df.pivotMatches("city", "name")
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Column Names][String] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<G> =
    pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: AnyColumnReference, inward: Boolean = true): DataFrame<G> =
    pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<G> =
    pivotMatches(inward) { columns.toColumnSet() }

// endregion

// region pivotCounts

/**
 * Computes number of matching rows in groups of this [GroupBy] for all unique values of the
 * selected [columns] (independently) across all [groupBy] key combinations.
 *
 * Performs a [pivot] operation on the specified [columns] of this [DataFrame]
 * and produces a new matrix-like [DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][GroupBy.pivot]
 * and [count][PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches][GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 */
internal interface GroupByPivotCountsCommonDocs

/**
 * Computes number of matching rows in groups of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] for all unique values of the
 * selected [columns] (independently) across all [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] key combinations.
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * and produces a new matrix-like [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations.
 * df.pivotCounts { city and name }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <G> GroupBy<*, G>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).count()

/**
 * Computes number of matching rows in groups of this [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] for all unique values of the
 * selected [columns] (independently) across all [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy] key combinations.
 *
 * Performs a [pivot][org.jetbrains.kotlinx.dataframe.api.pivot] operation on the specified [columns] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * and produces a new matrix-like [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * In the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]:
 * * Pivoted columns are displayed vertically — as [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each pivoted column,
 *   with subcolumns corresponding to their unique values;
 * * Grouping key columns are displayed horizontally — as columns representing
 *   unique combinations of grouping key values;
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 *
 * This function combines [pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivotMatches][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations.
 * df.pivotCounts("city", "name")
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Column Names][String] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<G> =
    pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: AnyColumnReference, inward: Boolean = true): DataFrame<G> =
    pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<G> =
    pivotCounts(inward) { columns.toColumnSet() }

// endregion

// endregion

// region GroupBy.aggregate

// region pivot

/**
 * Pivots the selected [columns] within each group for further
 * [pivot aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation].
 *
 * This function itself does not directly modify the result of [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate],
 * but instead creates an intermediate [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns produced by its [aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] are then
 * inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate]
 * when those aggregation functions are executed (as usual aggregations).
 * Their structure depends on the specific
 * [PivotGroupBy aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] used.
 *
 * See [GroupBy.pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] and [PivotGroupByDocs.Aggregation][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] for more information.
 *
 * For more information: [See "`pivot` inside aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-inside-aggregate)
 *
 * Check out [`PivotGroupBy` Grammar][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also [pivotMatches][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 * and [pivotCounts][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts] shortcuts.
 *
 * ### This `pivot` overload
 * Select or express pivot columns using the [PivotDsl].
 * ### Example
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Pivot the "city" column within each group,
 *     // creating a PivotGroupBy with "firstName" as grouping keys
 *     // and "city" as pivoted columns
 *     pivot { city }.aggregate {
 *         // Aggregate the mean of "age" column values for each
 *         // groupBy × pivot combination group into the "meanAge" column
 *         mean { age } into "meanAge"
 *
 *         // Aggregate the size of each `PivotGroupBy` group
 *         // into the "count" column
 *         count() into "count"
 *     }
 *
 *     // Shortcut for `count` aggregation in "firstName" × "lastName" groups
 *     // into "namesCount" column
 *     pivot { name.lastName }.count() into "namesCount"
 *
 *     // Common `count` aggregation
 *     // into "total" column
 *     count() into "total"
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [Pivot Columns Selector][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A [PivotGroupBy] for further [aggregations][PivotGroupByDocs.Aggregation].
 */
public fun <T> AggregateGroupedDsl<T>.pivot(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): PivotGroupBy<T> = PivotInAggregateImpl(this, columns, inward)

/**
 * Pivots the selected [columns] within each group for further
 * [pivot aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation].
 *
 * This function itself does not directly modify the result of [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate],
 * but instead creates an intermediate [PivotGroupBy][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns produced by its [aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] are then
 * inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate]
 * when those aggregation functions are executed (as usual aggregations).
 * Their structure depends on the specific
 * [PivotGroupBy aggregations][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] used.
 *
 * See [GroupBy.pivot][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] and [PivotGroupByDocs.Aggregation][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] for more information.
 *
 * For more information: [See "`pivot` inside aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-inside-aggregate)
 *
 * Check out [`PivotGroupBy` Grammar][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also [pivotMatches][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 * and [pivotCounts][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts] shortcuts.
 *
 * ### This `pivot` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * df.groupBy("firstName").aggregate {
 *     // Pivot the "city" column within each group,
 *     // creating a PivotGroupBy with "firstName" as grouping keys
 *     // and "city" as pivoted columns
 *     pivot("city").aggregate {
 *         // Aggregate the mean of "age" column values for each
 *         // groupBy × pivot combination group into the "meanAge" column
 *         mean("age") into "meanAge"
 *
 *         // Aggregate the size of each `PivotGroupBy` group
 *         // into the "count" column
 *         count() into "count"
 *     }
 *
 *     // Shortcut for `count` aggregation in "firstName" × "lastName" groups
 *     // into "namesCount" column
 *     pivot("lastName").count() into "namesCount"
 *
 *     // Common `count` aggregation
 *     // into "total" column
 *     count() into "total"
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param columns The [Pivot Columns Selector][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A [PivotGroupBy] for further [aggregations][PivotGroupByDocs.Aggregation].
 */
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: String, inward: Boolean = true): PivotGroupBy<T> =
    pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivot(
    vararg columns: AnyColumnReference,
    inward: Boolean = true,
): PivotGroupBy<T> = pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivot(vararg columns: KProperty<*>, inward: Boolean = true): PivotGroupBy<T> =
    pivot(inward) { columns.toColumnSet() }

// endregion

// region pivotMatches

/**
 * Computes the [pivotMatches][org.jetbrains.kotlinx.dataframe.DataFrame.pivotMatches] statistic for the selected [columns]
 * within each group and adds it to the [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches].
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [into][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [GroupBy.pivotMatches][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches] for more details.
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [pivotCounts][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts].
 *
 * ### This `pivotMatches` overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Example
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Compute whether matching rows exist for all unique values of "city"
 *     // across all "name.firstName" key values and adds it to the aggregation result
 *     pivotMatches { city }
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param columns The [Columns Selector][ColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 * This [DataFrame] is added to the [aggregate][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotMatches(
    inward: Boolean = true,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).matches()

/**
 * Computes the [pivotMatches][org.jetbrains.kotlinx.dataframe.DataFrame.pivotMatches] statistic for the selected [columns]
 * within each group and adds it to the [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [matches][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches].
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [into][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [GroupBy.pivotMatches][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches] for more details.
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [pivotCounts][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts].
 *
 * ### This `pivotMatches` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * df.groupBy("firstName").aggregate {
 *     // Compute whether matching rows exist for all unique values of "city"
 *     // across all "firstName" key values and adds it to the aggregation result
 *     pivotMatches("city")
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param columns The [Column Names][String] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 * This [DataFrame] is added to the [aggregate][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotMatches(vararg columns: String, inward: Boolean = true): DataFrame<T> =
    pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivotMatches(
    vararg columns: AnyColumnReference,
    inward: Boolean = true,
): DataFrame<T> = pivotMatches(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivotMatches(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> =
    pivotMatches(inward) { columns.toColumnSet() }

// endregion

// region pivotCounts

/**
 * Computes the [pivotCounts][org.jetbrains.kotlinx.dataframe.DataFrame.pivotCounts] statistic for the selected [columns]
 * within each group and adds it to the [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count].
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [into][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [GroupBy.pivotCounts][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts] for more details.
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [pivotMatches][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches].
 *
 * ### This `pivotCounts` overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Example
 * ```kotlin
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Compute number of for all unique values of "city"
 *     // across all "name.firstName" key values and adds it to the aggregation result
 *     pivotCounts { city }
 * }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
 * @param columns The [Columns Selector][ColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 * pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 * This [DataFrame] is added to the [aggregate][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotCounts(
    inward: Boolean = true,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).count()

/**
 * Computes the [pivotCounts][org.jetbrains.kotlinx.dataframe.DataFrame.pivotCounts] statistic for the selected [columns]
 * within each group and adds it to the [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [count][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count].
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [aggregate][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [into][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [GroupBy.pivotCounts][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts] for more details.
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [pivot][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [pivotMatches][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches].
 *
 * ### This `pivotCounts` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * df.groupBy("firstName").aggregate {
 *     // Compute number of for all unique values of "city"
 *     // across all "firstName" key values and adds it to the aggregation result
 *     pivotCounts("city")
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param columns The [Column Names][String] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 * pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 * This [DataFrame] is added to the [aggregate][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotCounts(vararg columns: String, inward: Boolean = true): DataFrame<T> =
    pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivotCounts(
    vararg columns: AnyColumnReference,
    inward: Boolean = true,
): DataFrame<T> = pivotCounts(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> AggregateGroupedDsl<T>.pivotCounts(vararg columns: KProperty<*>, inward: Boolean = true): DataFrame<T> =
    pivotCounts(inward) { columns.toColumnSet() }

// endregion

// endregion

/**
 * A dataframe-like structure that contains all unique combinations of key values
 * as columns (or [column groups][ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [DataFrame]).
 *
 * Similar to [GroupBy] but contains horizontal groups.
 *
 * A [Pivot] can be:
 * * [reduced][PivotDocs.Reducing] into a [DataRow], where each group is collapsed into a single representative row;
 * * [aggregated][PivotDocs.Aggregation] into a [DataRow], where each group is transformed into a new row of derived values;
 * * [grouped][PivotDocs.Grouping] into a [PivotGroupBy] structure, which combines [pivot] and [groupBy] operations
 *   and then reduced or aggregated into a [DataFrame].
 *
 * Check out [`Pivot` Grammar][PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 */
public interface Pivot<T> : Aggregatable<T>

/**
 * A specialized [ColumnsSelector] used for selecting columns in a [pivot] operation.
 *
 * Provides [PivotDsl] both as the receiver and the lambda parameter, and expects
 * a [ColumnsResolver] as the return value.
 *
 * Enables defining the hierarchy of pivot columns using [then][PivotDsl.then].
 */
public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnsResolver<C>>

/**
 * An intermediate class used in [`Pivot` reducing][PivotDocs.Reducing] operations.
 *
 * Serves as a transitional step between performing a reduction on pivot groups
 * and specifying how the resulting reduced rows should be represented
 * in a resulting [DataRow].
 *
 * Available transformation methods:
 * * [values][ReducedPivot.values] — creates a new row containing the values
 *   from the reduced rows in the selected columns and produces a [DataRow] of
 *   these values;
 * * [with][ReducedPivot.with] — computes a new value for each reduced row using a [RowExpression],
 *   and produces a [DataRow] containing these computed values.
 *
 * Each method returns a new [DataRow] with [pivot] keys as top-level columns
 * (or as [column groups][ColumnGroup]) and values composed of the reduced results from each group.
 *
 * Check out [`Pivot grammar`][Grammar].
 *
 * For more information, refer to: [See "Pivot` reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
 */
public class ReducedPivot<T>(
    @PublishedApi internal val pivot: Pivot<T>,
    @PublishedApi internal val reducer: Selector<DataFrame<T>, DataRow<T>?>,
) {
    override fun toString(): String = "ReducedPivot(pivot=$pivot, reducer=$reducer)"
}

@PublishedApi
internal fun <T> Pivot<T>.reduce(reducer: Selector<DataFrame<T>, DataRow<T>?>): ReducedPivot<T> =
    ReducedPivot(this, reducer)

@PublishedApi
internal inline fun <T> Pivot<T>.delegate(crossinline body: PivotGroupBy<T>.() -> DataFrame<T>): DataRow<T> =
    body(groupBy { none() })[0]

/**
 * TODO (#1536)
 */
internal interface PivotGroupByDocs {

    /**
     * In the resulting [DataFrame]:
     * * Pivoted columns are displayed vertically — as [column groups][ColumnGroup] for each pivoted column,
     *   with subcolumns corresponding to their unique values;
     * * Grouping key columns are displayed horizontally — as columns representing
     *   unique combinations of grouping key values;
     */
    interface ResultingMatrixCommonDescription

    /**
     * [PivotGroupBy] is a dataframe-like structure that combines [Pivot] and [GroupBy],
     * representing a matrix table with vertical [Pivot] groups (as columns)
     * and horizontal [GroupBy] groups (as rows),
     * where each cell represents a group corresponding
     * to both the [GroupBy] and [Pivot] key.
     *
     * Reversed order of `pivot` and `groupBy`
     * (i.e., [DataFrame.pivot] + [Pivot.groupBy] or [DataFrame.groupBy] + [GroupBy.pivot])
     * will produce the same result.
     *
     * [PivotGroupBy] can be [reduced][PivotGroupByDocs.Reducing]
     * or [aggregated][PivotGroupByDocs.Aggregation] into a [DataFrame].
     *
     * Check out [PivotGroupBy Grammar][PivotGroupByDocs.Grammar].
     *
     * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
     */
    interface CommonDescription

    interface Grammar

    interface Reducing

    interface Aggregation
}

public interface PivotGroupBy<out T> : Aggregatable<T> {

    public fun <R> aggregate(separate: Boolean = false, body: AggregateBody<T, R>): DataFrame<T>

    public fun default(value: Any?): PivotGroupBy<T>
}

public class ReducedPivotGroupBy<T>(
    @PublishedApi internal val pivot: PivotGroupBy<T>,
    @PublishedApi internal val reducer: Selector<DataFrame<T>, DataRow<T>?>,
) {
    override fun toString(): String = "ReducedPivotGroupBy(pivot=$pivot, reducer=$reducer)"
}

@PublishedApi
internal fun <T> PivotGroupBy<T>.reduce(reducer: Selector<DataFrame<T>, DataRow<T>?>): ReducedPivotGroupBy<T> =
    ReducedPivotGroupBy(this, reducer)

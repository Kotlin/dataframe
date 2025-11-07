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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotGroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotInAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * Splits the rows of this [DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [\columns] of the original [DataFrame].
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
 * @include [PivotedColumnsInline]
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: {@include [DocumentationUrls.Pivot]}
 */
internal interface PivotDocs {

    /**
     * ## [pivot][pivot] Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * ### Create [Pivot]
     *
     * [**`pivot`**][pivot]**`(`**`inward: `[`Boolean`][Boolean]**` = true)  {  `**`pivotColumns: `[`PivotColumnsSelector`][PivotColumnsSelector]**` }`**
     *
     * ### Reduce [Pivot] into [DataRow]
     *
     * [Pivot][Pivot]`.`[**`minBy`**][Pivot.minBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`maxBy`**][Pivot.maxBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`first`**][Pivot.first]`  \[ `**` {  `**`rowCondition: `[`RowFilter`][RowFilter]**`  }  `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`last`**][Pivot.last]`  \[ `**`{  `**`rowCondition: `[`RowFilter`][RowFilter]**`  }  `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`medianBy`**][Pivot.medianBy]**`  {  `**`column: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`percentileBy`**][Pivot.percentileBy]**`(`**`percentile: `[`Double`][Double]**`)  {  `**`column: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * __`.`__[**`with`**][Pivot.with]**`  {  `**`rowExpression: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`values`**][Pivot.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * ### Aggregate [Pivot] into [DataRow]
     *
     * [Pivot][Pivot]`.`[**`count`**][Pivot.count]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`frames`**][Pivot.frames]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`with`**][Pivot.with]**`  {  `**`rowExpression: `[`RowExpression`][RowExpression]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`values`**][Pivot.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`aggregate`**][Pivot.aggregate]**`  {  `**`aggregations: `[`AggregateDsl`][AggregateDsl]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[<aggregation_statistic>][PivotDocs.AggregationStatistics]
     *
     * ### Group [Pivot] into [PivotGroupBy] and reduce / aggregate it
     *
     * [Pivot][Pivot]`.`[**`groupBy`**][Pivot.groupBy]**`  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`groupByOther`**][Pivot.groupByOther]**`()`**
     *
     * {@include [Indent]}
     * `    \[ `__`.`__[**`default`**][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[<pivot_groupBy_reducer>][PivotGroupByDocs.Reducing]
     *
     * {@include [Indent]}
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
     */
    interface Aggregation

    /**
     * ### [Pivot] grouping
     *
     * [Pivot] can be pivoted with [groupBy][Pivot.groupBy] method. It will produce a [PivotGroupBy].
     *
     * @include [PivotGroupByDocs.CommonDescription]
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
     * For more information: {@include [DocumentationUrls.PivotStatistics]}
     */
    interface AggregationStatistics

    /**
     * Pivoted columns can also be created inline:
     * ```kotlin
     * // Create a new column "newName" based on existing "oldName" values
     * // and pivot it:
     * df.pivot { expr("newName") { oldName.drop(5) } }
     * ```
     */
    @ExcludeFromSources
    interface PivotedColumnsInline

    /**
     * @param [\inward] Defines whether the generated columns are nested under a supercolumn:
     *   - `true` — pivot key columns are nested under a supercolumn named after
     *     the original pivoted column (independently for multiple pivoted columns);
     *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
     *   - `null` (default) — inferred automatically: `true` for multiple pivoted columns
     *     or when the [Pivot] has been grouped; `false` otherwise.
     */
    @ExcludeFromSources
    interface InwardKDocs

    /**
     * @param [\inward] Defines whether the generated columns are nested under a supercolumn:
     *   - `true` (default) — pivot key columns are nested under a supercolumn named after
     *     the original pivoted column (independently for multiple pivoted columns);
     *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
     */
    @ExcludeFromSources
    interface InwardKDocsForGrouped

}

/** {@set [SelectingColumns.OPERATION] [pivot][pivot]} */
@ExcludeFromSources
private interface SetPivotOperationArg


/**
 * A specialized [ColumnsSelectionDsl] that allows specifying [pivot] key ordering
 * using the [then] function.
 *
 * @include [PivotDslDocs]
 */
public interface PivotDsl<out T> : ColumnsSelectionDsl<T> {

    /**
     * @include [ThenDocs]
     */
    public infix fun <C> ColumnsResolver<C>.then(other: ColumnsResolver<C>): ColumnSet<C> =
        PivotChainColumnSet(this, other)

    /**
     * @include [ThenDocs]
     */
    public infix fun <C> String.then(other: ColumnsResolver<C>): ColumnSet<C> = toColumnOf<C>() then other

    /**
     * @include [ThenDocs]
     */
    public infix fun <C> ColumnsResolver<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

    /**
     * @include [ThenDocs]
     */
    public infix fun String.then(other: String): ColumnSet<Any?> = toColumnAccessor() then other.toColumnAccessor()

    /**
     * Specifies the ordering of the [pivot] key columns.
     *
     * In the resulting [Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [\other].
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [\other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [ColumnSet] representing the hierarchical pivot key ordering.
     */
    @ExcludeFromSources
    private interface ThenDocs

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

/**
 * [PivotDsl] defines how key columns are selected and structured in a [pivot]:
 * * [pivot] with a single key column produces a [Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [pivot] with multiple keys combined using [and] produces a [Pivot]
 *   with independent [column groups][ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [pivot] with multiple keys ordered using [then] produces a [Pivot]
 *   with nested [column groups][ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [Columns Selection via DSL][SelectingColumns.Dsl].
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
@ExcludeFromSources
private interface PivotDslDocs

// region DataFrame

/**
 * {@include [PivotDocs]}
 * ### This `pivot` Overload
 */
@ExcludeFromSources
private interface CommonPivotDocs


// region pivot

/**
 * @include [CommonPivotDocs]
 * Select or express pivot columns using the [PivotDsl].
 *
 * @include [PivotDslDocs]
 * @include [PivotDocs.InwardKDocs]
 * @param columns The [Pivot Columns Selector][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [Pivot] containing the unique values of the selected column as new columns
 * (or as [column groups][ColumnGroup] for multiple key columns),
 * with their corresponding groups of rows represented as [DataFrame]s.
 */
public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> =
    PivotImpl(this, columns, inward)

/**
 * @include [CommonPivotDocs]
 * @include [SelectingColumns.ColumnNames]
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
 * @include [PivotDocs.InwardKDocs]
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
 * * Cell values are [Boolean] indicators showing whether matching rows exist
 *   for each pivoting/grouping key combination.
 */
internal interface PivotMatchesResultDescription

/**
 * Computes whether matching rows exist in this [DataFrame] for all unique values of the
 * selected [\columns] (independently) across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot] operation on the specified [\columns] of this [DataFrame],
 * then [groups it by][Pivot.groupByOther] the remaining columns,
 * and produces a new matrix-like [DataFrame].
 *
 * @include [PivotGroupByDocs.ResultingMatrixCommonDescription]
 * @include [PivotMatchesResultDescription]
 *
 * This function combines [pivot][DataFrame.pivot], [groupByOther][Pivot.groupByOther],
 * and [matches][PivotGroupBy.matches] operations into a single call.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.PivotMatches]}
 *
 * See also:
 * * [pivotCounts], which performs a similar operation
 *   but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 */
internal interface DataFramePivotMatchesCommonDocs

/**
 * @include [DataFramePivotMatchesCommonDocs]
 * @include [SelectingColumns.Dsl]
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
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().matches()

/**
 * @include [DataFramePivotMatchesCommonDocs]
 * @include [SelectingColumns.ColumnNames]
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotMatches("city", "name")
 * ```
 *
 * @include [PivotDocs.InwardKDocs]
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
 * * Cell values represent the number of matching rows
 *   for each pivoting/grouping key combination.
 */
internal interface PivotCountsResultDescription

/**
 * Computes number of matching rows in this [DataFrame] for all unique values of the
 * selected [\columns] (independently) across all possible combinations
 * of values in the remaining columns (all expecting selected).
 *
 * Performs a [pivot] operation on the specified [\columns] of this [DataFrame],
 * then [groups it by][Pivot.groupByOther] the remaining columns,
 * and produces a new matrix-like [DataFrame].
 *
 * @include [PivotGroupByDocs.ResultingMatrixCommonDescription]
 * @include [PivotCountsResultDescription]
 *
 * This function combines [pivot][DataFrame.pivot], [groupByOther][Pivot.groupByOther],
 * and [count][PivotGroupBy.count] operations into a single call.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.PivotCounts]}
 *
 * See also: [pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 */
internal interface DataFramePivotCountsCommonDocs

/**
 * @include [DataFramePivotCountsCommonDocs]
 * @include [SelectingColumns.Dsl]
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotCounts { city and name }
 * ```
 *
 * @include [PivotDocs.InwardKDocs]
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <T> DataFrame<T>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().count()

/**
 * @include [DataFramePivotCountsCommonDocs]
 * @include [SelectingColumns.ColumnNames]
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all possible combinations
 * // of values in the remaining columns.
 * df.pivotCounts("city", "name")
 * ```
 *
 * @include [PivotDocs.InwardKDocs]
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
 * Pivots the selected [\columns] of this [GroupBy] groups.
 * Returns a [PivotGroupBy].
 *
 * @include [PivotGroupByDocs.CommonDescription]
 *
 * @include [PivotDocs.PivotedColumnsInline]
 */
@ExcludeFromSources
private interface PivotForGroupByDocs

/**
 * {@include [PivotForGroupByDocs]}
 * ### This `pivot` Overload
 */
@ExcludeFromSources
private interface CommonPivotForGroupByDocs

/**
 * @include [CommonPivotForGroupByDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetPivotOperationArg] {@set [SelectingColumns.RECEIVER] <code>`gb`</code>}}
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * @include [CommonPivotForGroupByDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetPivotOperationArg] {@set [SelectingColumns.RECEIVER] <code>`gb`</code>}}
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * Performs a [pivot][GroupBy.pivot] operation on the specified [\columns] of this [GroupBy] groups,
 * and produces a new matrix-like [DataFrame].
 *
 * @include [PivotGroupByDocs.ResultingMatrixCommonDescription]
 * @include [PivotMatchesResultDescription]
 *
 * This function combines [pivot][GroupBy.pivot]
 * and [matches][PivotGroupBy.matches] operations into a single call.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.PivotMatches]}
 *
 * See also: [pivotCounts][GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 */
internal interface GroupByPivotMatchesCommonDocs

/**
 * @include [GroupByPivotMatchesCommonDocs]
 * @include [SelectingColumns.Dsl]
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations
 * gb.pivotMatches { city and name }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <G> GroupBy<*, G>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).matches()

/**
 * @include [GroupByPivotMatchesCommonDocs]
 * @include [SelectingColumns.ColumnNames]
 *
 * ### Example
 * ```kotlin
 * // Compute whether matching rows exist for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations
 * df.pivotMatches("city", "name")
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * selected [\columns] (independently) across all [groupBy] key combinations.
 *
 * Performs a [pivot] operation on the specified [\columns] of this [DataFrame]
 * and produces a new matrix-like [DataFrame].
 *
 * @include [PivotGroupByDocs.ResultingMatrixCommonDescription]
 * @include [PivotCountsResultDescription]
 *
 * This function combines [pivot][GroupBy.pivot]
 * and [count][PivotGroupBy.count] operations into a single call.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.PivotCounts]}
 *
 * See also: [pivotMatches][GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 */
internal interface GroupByPivotCountsCommonDocs

/**
 * @include [GroupByPivotCountsCommonDocs]
 * @include [SelectingColumns.Dsl]
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations.
 * df.pivotCounts { city and name }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used as [pivot] keys for the operation.
 * @return A new [DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <G> GroupBy<*, G>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).count()

/**
 * @include [GroupByPivotCountsCommonDocs]
 * @include [SelectingColumns.ColumnNames]
 *
 * ### Example
 * ```kotlin
 * // Compute number of matching rows for all unique values of "city"
 * // and "name" (independently) across all grouping key combinations.
 * df.pivotCounts("city", "name")
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * Pivots the selected [\columns] within each group for further
 * [pivot aggregations][PivotGroupByDocs.Aggregation].
 *
 * This function itself does not directly modify the result of [aggregate][Grouped.aggregate],
 * but instead creates an intermediate [PivotGroupBy].
 * The resulting [DataFrame] columns produced by its [aggregations][PivotGroupByDocs.Aggregation] are then
 * inserted into the final [DataFrame] returned by [aggregate][Grouped.aggregate]
 * when those aggregation functions are executed (as usual aggregations).
 * Their structure depends on the specific
 * [PivotGroupBy aggregations][PivotGroupByDocs.Aggregation] used.
 *
 * See [GroupBy.pivot] and [PivotGroupByDocs.Aggregation] for more information.
 *
 * For more information: {@include [DocumentationUrls.PivotInsideAggregationStatistics]}
 *
 * Check out [`PivotGroupBy` Grammar][PivotGroupByDocs.Grammar].
 *
 * See also [pivotMatches][AggregateGroupedDsl.pivotMatches]
 * and [pivotCounts][AggregateGroupedDsl.pivotCounts] shortcuts.
 *
 * ### This `pivot` overload
 */
@ExcludeFromSources
internal interface AggregateGroupedDslPivotDocs

/**
 * @include [AggregateGroupedDslPivotDocs]
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
 * @include [PivotDocs.InwardKDocsForGrouped]
 * @param [columns] The [Pivot Columns Selector][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A [PivotGroupBy] for further [aggregations][PivotGroupByDocs.Aggregation].
 */
public fun <T> AggregateGroupedDsl<T>.pivot(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): PivotGroupBy<T> = PivotInAggregateImpl(this, columns, inward)

/**
 * @include [AggregateGroupedDslPivotDocs]
 * @include [SelectingColumns.ColumnNames]
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
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * Computes the [pivotMatches][DataFrame.pivotMatches] statistic for the selected [\columns]
 * within each group and adds it to the [aggregate][Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][AggregateGroupedDsl.pivot]
 * and [matches][PivotGroupBy.matches].
 *
 * The resulting [DataFrame] columns are inserted into the final [DataFrame]
 * returned by [aggregate][Grouped.aggregate].
 * The resulting column name can be specified using [into].
 *
 * See [GroupBy.pivotMatches] for more details.
 *
 * For more information: {@include [DocumentationUrls.PivotMatches]}
 *
 * See also: [pivot][AggregateGroupedDsl.pivot], [pivotCounts][AggregateGroupedDsl.pivotCounts].
 *
 * ### This `pivotMatches` overload
 */
@ExcludeFromSources
internal interface AggregateGroupedDslPivotMatchesDocs

/**
 * @include [AggregateGroupedDslPivotMatchesDocs]
 * @include [SelectingColumns.Dsl]
 * ### Example
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Compute whether matching rows exist for all unique values of "city"
 *     // across all "name.firstName" key values and adds it to the aggregation result
 *     pivotMatches { city }
 * }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * @include [AggregateGroupedDslPivotMatchesDocs]
 * @include [SelectingColumns.ColumnNames]
 * ### Example
 * ```kotlin
 * df.groupBy("firstName").aggregate {
 *     // Compute whether matching rows exist for all unique values of "city"
 *     // across all "firstName" key values and adds it to the aggregation result
 *     pivotMatches("city")
 * }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * Computes the [pivotCounts][DataFrame.pivotCounts] statistic for the selected [\columns]
 * within each group and adds it to the [aggregate][Grouped.aggregate] result.
 *
 * This is a shortcut for combining [pivot][AggregateGroupedDsl.pivot]
 * and [count][PivotGroupBy.count].
 *
 * The resulting [DataFrame] columns are inserted into the final [DataFrame]
 * returned by [aggregate][Grouped.aggregate].
 * The resulting column name can be specified using [into].
 *
 * See [GroupBy.pivotCounts] for more details.
 *
 * For more information: {@include [DocumentationUrls.PivotCounts]}
 *
 * See also: [pivot][AggregateGroupedDsl.pivot], [pivotMatches][AggregateGroupedDsl.pivotMatches].
 *
 * ### This `pivotCounts` overload
 */
@ExcludeFromSources
internal interface AggregateGroupedDslPivotCountsDocs

/**
 * @include [AggregateGroupedDslPivotCountsDocs]
 * @include [SelectingColumns.Dsl]
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
 * @include [AggregateGroupedDslPivotCountsDocs]
 * @include [SelectingColumns.ColumnNames]
 * ### Example
 * ```kotlin
 * df.groupBy("firstName").aggregate {
 *     // Compute number of for all unique values of "city"
 *     // across all "firstName" key values and adds it to the aggregation result
 *     pivotCounts("city")
 * }
 * ```
 *
 * @include [PivotDocs.InwardKDocsForGrouped]
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
 * For more information: {@include [DocumentationUrls.Pivot]}
 */
public interface Pivot<T> : Aggregatable<T>

/**
 * A specialized [ColumnsSelector] used for selecting columns in a [pivot] operation.
 *
 * Provides a [PivotDsl] both as the receiver and the lambda parameter, and expects
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
 * For more information, refer to: {@include [DocumentationUrls.PivotReducing]}
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
     * [PivotGroupBy] is a dataframe-like structure, combining [Pivot] and [GroupBy]
     * and representing a matrix table with vertical [Pivot] groups (as columns)
     * and horizontal [GroupBy] groups (as rows), and each cell
     * represents a group corresponding both to [GroupBy] and [Pivot] key.
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
     * For more information: {@include [DocumentationUrls.PivotGroupBy]}
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

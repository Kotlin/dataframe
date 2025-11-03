package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelectionDsl
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.OPERATION
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Groups the rows of this [DataFrame] based on the values in one or more specified [key columns][cols].
 * Each unique value in a key column — or a unique tuple of values for multiple columns —
 * defines a group consisting of all rows where the column(s) contain that value combination.
 *
 * Returns a [GroupBy] — a dataframe-like structure that contains all unique combinations of key values
 * along with the corresponding groups of rows (each represented as a [DataFrame]) as rows.
 *
 * A [GroupBy] can then be:
 * * [transformed][Transformation] into a new [GroupBy];
 * * [reduced][Reducing] into a [DataFrame], where each group is collapsed into a single representative row;
 * * [aggregated][Aggregation] into a [DataFrame], where each group is transformed into one or more rows of derived values;
 * * [pivoted][Pivoting] into a [PivotGroupBy] structure, which combines [pivot] and [groupBy] operations
 *   and then reduced or aggregated into a [DataFrame].
 *
 * Grouping keys can also be created inline:
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][GroupBySelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.GroupBy]}
 *
 * Don't confuse this with [group], which groups column into
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * See also [pivot][DataFrame.pivot] that groups rows of [DataFrame] vertically.
 */
internal interface GroupByDocs {
    /**
     * ## [groupBy][groupBy] Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * ### Create and transform [GroupBy]
     *
     * [**`groupBy`**][groupBy]**`(`**`moveToTop: `[`Boolean`][Boolean]**` = true)  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`sortByGroup`**][GroupBy.sortByGroup]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByGroupDesc`**][GroupBy.sortByGroupDesc]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByCount`**][GroupBy.sortByCount]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByCountAsc`**][GroupBy.sortByCountAsc]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByKey`**][GroupBy.sortByKey]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByKeyDesc`**][GroupBy.sortByKeyDesc]**`() `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`sortBy`**][GroupBy.sortBy]**`  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` } `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`sortByDesc`**][GroupBy.sortByDesc]**`  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` } `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`updateGroups`**][GroupBy.updateGroups]**`  {  `**`frameExpression`**` } `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`filter`**][GroupBy.filter]**` {  `**`predicate: `[`GroupedRowFilter`][GroupedRowFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`add`**][GroupBy.add]**`(`**`column: `[`DataColumn`][DataColumn]**`)  {  `**`rowExpression: `[`RowExpression`][RowExpression]**` } `**`]`
     *
     * ### Reduce [GroupBy] into [DataFrame]
     *
     * {@include [Indent]}
     * [GroupBy][GroupBy]`.`[**`minBy`**][GroupBy.minBy]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`maxBy`**][GroupBy.maxBy]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`first`**][GroupBy.first]`  \[ `**` {  `**`rowCondition: `[`RowFilter`][RowFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`last`**][GroupBy.last]`  \[ `**`{  `**`rowCondition: `[`RowFilter`][RowFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * __`.`__[**`concat`**][ReducedGroupBy.concat]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`into`**][ReducedGroupBy.into]**`(`**`column: `[`String`][String]**`) `**`  \[ `**`{  `**`rowExpression: `[`RowExpression`][RowExpression]**` } `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`values`**][ReducedGroupBy.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * ### Aggregate [GroupBy] into [DataFrame]
     *
     * {@include [Indent]}
     * [GroupBy][GroupBy]`.`[**`concat`**][GroupBy.concat]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`concatWithKeys`**][GroupBy.concatWithKeys]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`into`**][GroupBy.into]**`(`**`column: `[`String`][String]**`) `**`  \[  `**`{  `**`rowExpression: `[`RowExpression`][RowExpression]**` } `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`values`**][Grouped.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`count`**][Grouped.count]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`aggregate`**][Grouped.aggregate]**`  {  `**`aggregations: `[`AggregateDsl`][AggregateDsl]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[<aggregation_statistic>][AggregationStatistics]
     *
     * ### Pivot [GroupBy] into [PivotGroupBy] and reduce / aggregate it
     *
     * {@include [Indent]}
     * `| `__`.`__[**`pivot`**][GroupBy.pivot]**`  {  `**`columns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `    \[ `__`.`__[**`default`**][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[<pivot_reducer>][PivotGroupByDocs.Reducing]
     *
     * {@include [Indent]}
     * `| `__`.`__[<pivot_aggregator>][PivotGroupByDocs.Aggregation]
     *
     * Check out [PivotGroupBy Grammar][PivotGroupByDocs.Grammar] for more information.
     */
    interface Grammar

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetGroupByOperationArg]}
     */
    interface GroupBySelectingOptions

    /**
     * ### [GroupBy] aggregation statistics
     *
     * Provides predefined shortcuts for the most common statistical aggregation operations
     * that can be applied to each group within a [GroupBy].
     *
     * Each function computes a statistic across the rows of a group and returns the result as
     * a new column (or several columns) in the resulting [DataFrame].
     *
     * * [count][Grouped.count] — calculate the number of rows in each group;
     * * [max][Grouped.max] / [maxOf][Grouped.maxOf] / [maxFor][Grouped.maxFor] —
     *   calculate the maximum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [min][Grouped.min] / [minOf][Grouped.minOf] / [minFor][Grouped.minFor] —
     *   calculate the minimum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [sum][Grouped.sum] / [sumOf][Grouped.sumOf] / [sumFor][Grouped.sumFor] —
     *   calculate the sum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [mean][Grouped.mean] / [meanOf][Grouped.meanOf] / [meanFor][Grouped.meanFor] —
     *   calculate the mean (average) of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [std][Grouped.std] / [stdOf][Grouped.stdOf] / [stdFor][Grouped.stdFor] —
     *   calculate the standard deviation of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [median][Grouped.median] / [medianOf][Grouped.medianOf] / [medianFor][Grouped.medianFor] —
     *   calculate the median of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [percentile][Grouped.percentile] / [percentileOf][Grouped.percentileOf] / [percentileFor][Grouped.percentileFor] —
     *   calculate a specified percentile of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group.
     *
     * For more information: {@include [DocumentationUrls.GroupByStatistics]}
     */
    interface AggregationStatistics


    /**
     * ### [GroupBy] transformations
     *
     * A [GroupBy] can be transformed into a new [GroupBy] using one of the following methods:
     * * [sortByGroup][GroupBy.sortByGroup] / [sortByGroupDesc][GroupBy.sortByGroupDesc] — sorts the **order
     *   of groups** (and their corresponding keys) by values computed with a [DataFrameExpression] applied to each group;
     * * [sortByCount][GroupBy.sortByCount] / [sortByCountAsc][GroupBy.sortByCountAsc] — sorts the **order
     *   of groups** (and their corresponding keys) by the number of rows they contain;
     * * [sortByKey][GroupBy.sortByKey] / [sortByKeyDesc][GroupBy.sortByKeyDesc] — sorts the **order
     *   of groups** (and their corresponding keys) by the grouping key values;
     * * [sortBy][GroupBy.sortBy] / [sortByDesc][GroupBy.sortByDesc] — sorts the **order of rows within each group**
     *   by one or more column values;
     * * [updateGroups][GroupBy.updateGroups] — transforms each group into a new one;
     * * [filter][GroupBy.filter] — filters group rows by the given predicate (as usual [DataFrame.filter]).
     * * [add][GroupBy.add] — adds a new column to each group.
     *
     * Each method returns a new [GroupBy] with updated group order or modified group content.
     *
     * For more information: {@include [DocumentationUrls.GroupByTransformation]}
     */
    interface Transformation

    /**
     * ### [GroupBy] reducing
     *
     * Each [GroupBy] group can be collapsed into a single row and then concatenated
     * into a new [DataFrame] composed of these rows.
     *
     * Reducing is a specific case of [aggregation][Aggregation].
     *
     * First, choose a [GroupBy] reducing method:
     * * [first][GroupBy.first], [last][GroupBy.last] — take the first or last row
     *   (optionally, the first or last one that satisfies a predicate) of each group;
     * * [minBy][GroupBy.minBy] / [maxBy][GroupBy.maxBy] — take the row with the minimum or maximum value
     *   of the given [RowExpression] calculated on rows within each group;
     * * [medianBy][GroupBy.medianBy] / [percentileBy][GroupBy.percentileBy] — take the row with
     *   the median or specific percentile value of the given [RowExpression] calculated on rows within each group;
     *
     * These functions return a [ReducedGroupBy], which can then be transformed into a new [DataFrame]
     * containing the reduced rows (either original or transformed) using one of the following methods:
     * * [concat][ReducedGroupBy.concat] — simply concatenates all reduced rows;
     * * [values][ReducedGroupBy.values] — creates a [DataFrame] with new rows by transforming each reduced row
     *   using [ColumnsForAggregateSelectionDsl];
     * * [into][ReducedGroupBy.into] — creates a new column with values computed with [RowExpression] on each row,
     *   or a new [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *   containing each group reduced to a single row;
     *
     * Each method returns a new [DataFrame] that includes the grouping key columns,
     * containing all unique grouping key values (or value combinations for multiple keys)
     * along with their corresponding reduced rows.
     *
     * Check out [`GroupBy grammar`][Grammar].
     *
     * For more information: {@include [DocumentationUrls.GroupByReducing]}
     */
    interface Reducing

    /**
     * ### [GroupBy] aggregation
     *
     * Each [GroupBy] can be directly transformed into a new [DataFrame] by applying one or more
     * aggregation operations to its groups.
     *
     * Aggregation is a generalization of [reducing][Reducing].
     *
     * The following aggregation methods are available:
     * * [concat][GroupBy.concat] — concatenates all rows from all groups into a single [DataFrame],
     *   without preserving grouping keys;
     * * [concatWithKeys][GroupBy.concatWithKeys] — a variant of [concat][GroupBy.concat] that also includes
     *   grouping keys that were not present in the original [DataFrame];
     * * [into][GroupBy.into] — creates a new column containing a list of values computed with a [RowExpression]
     *   for each group, or a new [frame column][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]
     *   containing the groups themselves;
     * * [values][ReducedGroupBy.values] — creates a [DataFrame] with new rows produced by transforming
     *   each group using [ColumnsForAggregateSelectionDsl];
     * * [count][Grouped.count] — returns a [DataFrame] containing the grouping key columns and an additional column
     *   with the number of rows in each corresponding group;
     * * [aggregate][Grouped.aggregate] — performs a set of custom aggregations using [AggregateDsl],
     *   allowing you to compute one or more derived values per group;
     * * [Various aggregation statistics][AggregationStatistics] — predefined shortcuts
     *   for common statistical aggregations such as [sum][Grouped.sum], [mean][Grouped.mean],
     *   [median][Grouped.median], and others.
     *
     * Each of these methods returns a new [DataFrame] that includes the grouping key columns
     * (except for [concat][GroupBy.concat]) along with the columns of values aggregated
     * from the corresponding groups.
     *
     * Check out [`GroupBy grammar`][Grammar].
     *
     * For more information: {@include [DocumentationUrls.GroupByAggregation]}
     */
    interface Aggregation

    /**
     * ### [GroupBy] pivoting
     *
     * [GroupBy] can be pivoted with [pivot][GroupBy.pivot] method. It will produce a [PivotGroupBy].
     *
     * @include [PivotGroupByDocs.CommonDescription]
     */
    interface Pivoting
}

/** {@set [SelectingColumns.OPERATION] [groupBy][groupBy]} */
@ExcludeFromSources
private interface SetGroupByOperationArg

/**
 * {@include [GroupByDocs]}
 * ### This `groupBy` Overload
 */
@ExcludeFromSources
private interface CommonGroupByDocs

/**
 * @include [CommonGroupByDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetGroupByOperationArg]}
 *
 * @param [moveToTop] Specifies whether nested grouping columns should be moved to the top level
 * or kept inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * Defaults to `true`.
 * @param [cols] The [Columns Selector][ColumnsSelector] that defines which columns are used
 * as keys for grouping.
 * @return A new [GroupBy] containing the unique combinations of values from the provided [key columns][cols],
 * together with their corresponding groups of rows.
 */
@Refine
@Interpretable("DataFrameGroupBy")
public fun <T> DataFrame<T>.groupBy(moveToTop: Boolean = true, cols: ColumnsSelector<T, *>): GroupBy<T, T> =
    groupByImpl(moveToTop, cols)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupBy<T, T> = groupBy { cols.toColumnSet() }

/**
 * @include [CommonGroupByDocs]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetGroupByOperationArg]}
 *
 * @param [cols] The [Column names][String] that defines which columns are used
 * as keys for grouping.
 * @return A new [GroupBy] containing the unique combinations of values from the provided [key columns][cols],
 * together with their corresponding groups of rows.
 */
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupBy<T, T> = groupBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.groupBy(vararg cols: AnyColumnReference, moveToTop: Boolean = true): GroupBy<T, T> =
    groupBy(moveToTop) { cols.toColumnSet() }

// endregion

/**
 * Groups the rows of this [Pivot] into [PivotGroupBy]
 * based on the values in one or more specified [key columns][\columns].
 *
 * Works like regular [DataFrame.groupBy] on pivot groups.
 *
 * Grouping keys can also be created inline:
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * pivot.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * @include [PivotGroupByDocs.CommonDescription]
 */
@ExcludeFromSources
internal interface GroupByForPivotDocs

/**
 * {@include [GroupByForPivotDocs]}
 * ### This `groupBy` Overload
 */
@ExcludeFromSources
private interface CommonGroupByForPivotDocs

// region Pivot

/**
 * {@include [CommonGroupByForPivotDocs]}
 * @include [SelectingColumns.Dsl]
 *
 * #### For example:
 *
 * `pivot.`{@get [OPERATION]}` { length `[and][ColumnsSelectionDsl.and]` age }`
 *
 * `pivot.`{@get [OPERATION]}`  {  `[cols][ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `pivot.`{@get [OPERATION]}`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * {@include [SetGroupByOperationArg]}
 * @param moveToTop Specifies whether nested grouping columns should be moved to the top level
 * or kept inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * Defaults to `true`.
 * @param [columns] The [Columns Selector][ColumnsSelector] that defines which columns are used
 * as keys for grouping.
 * @return A new [PivotGroupBy] that preserves the original [pivot] key columns
 * and uses the provided columns as [groupBy] keys.
 */
public fun <T> Pivot<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
    (this as PivotImpl<T>).toGroupedPivot(moveToTop, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: AnyColumnReference): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

/**
 * {@include [CommonGroupByForPivotDocs]}
 * @include [SelectingColumns.ColumnNames]
 *
 * #### For example:
 *
 * `df.`{@get [OPERATION]}`("length", "age")`
 *
 * {@include [SetGroupByOperationArg]}
 * @param [columns] The [Column names][String] that defines which columns are used
 * as keys for grouping.
 * @return A new [PivotGroupBy] that preserves the original [pivot] key columns
 * and uses the provided columns as [groupBy] keys.
 */
public fun <T> Pivot<T>.groupBy(vararg columns: String): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: KProperty<*>): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

/**
 * Groups the rows of this [Pivot] into a [PivotGroupBy]
 * based on the values of all columns except the pivot key columns.
 * For example, if a [DataFrame] has columns `"a"`, `"b"`, `"c"`, `"d"` and is pivoted by
 * `"a"` and `"c"`, then this [Pivot] will be grouped by the remaining columns `"b"` and `"d"`.
 *
 * @include [PivotGroupByDocs.CommonDescription]
 * @return A new [PivotGroupBy] that preserves the original [pivot] key columns
 * and uses the remaining columns as [groupBy] keys.
 */
public fun <T> Pivot<T>.groupByOther(): PivotGroupBy<T> {
    val impl = this as PivotImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { allExcept(pivotColumns) }
}

// endregion

/**
 * A specialized lambda that provides a [GroupedDataRow] both as the receiver and as the argument (`this` and `it`)
 * and produces a result of type [R].
 */
public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

/**
 * A specialized lambda that provides a [GroupedDataRow] both as the receiver and as the argument (`this` and `it`)
 * and returns a [Boolean] value used for filtering.
 */
public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

/**
 * A specialized form of [DataRow] representing a single row of a [GroupBy].
 * Each instance contains the key values and a reference to the corresponding [group].
 */
public interface GroupedDataRow<out T, out G> : DataRow<T> {

    /**
     * The [DataFrame] representing the group corresponding to the current key values.
     */
    public fun group(): DataFrame<G>
}

/**
 * The [DataFrame] representing the group corresponding to the current key values.
 */
public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G>
    get() = group()

/**
 * An alternative representation of a [GroupBy.Entry], holding a key–group pair.
 *
 * @property key The key represented as a [DataRow].
 * @property group The [DataFrame] containing the rows belonging to this group.
 */
public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

/**
 * A dataframe-like structure that contains all unique combinations of key values
 * along with the corresponding groups of rows (each represented as a [DataFrame]).
 *
 * Consists of two main parts:
 * * [groups] — represents the groups as a [FrameColumn], where each cell contains a [DataFrame]
 *   with the rows that belong to a specific group.
 * * [keys] — represents the grouping keys as a [DataFrame], containing one column for each key column.
 *   Each row in [keys] corresponds to a group in [groups].
 *
 * Together, the rows of [keys] and [groups] define one-to-one **key–group pairs**.
 *
 * @param G The schema of the groups (same as the schema of the original [DataFrame]).
 * @param T The schema of the grouping keys.
 */
public interface GroupBy<out T, out G> : Grouped<G> {

    /**
     * A [FrameColumn] representing all groups of rows.
     * Each cell contains a [DataFrame] with the subset of rows that share the same key values.
     */
    public val groups: FrameColumn<G>

    /**
     * A [DataFrame] representing the grouping keys.
     * Each column corresponds to a key column, and each row corresponds to a unique group.
     */
    public val keys: DataFrame<T>

    /**
     * Creates a new [GroupBy] by transforming each group’s [DataFrame]
     * using the provided [transform] function.
     *
     * @param [transform] A lambda that takes each group as a [DataFrame]
     * (available both as a receiver and as a parameter) and returns a transformed [DataFrame].
     * @return A new [GroupBy] instance containing the transformed groups.
     */
    public fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>): GroupBy<T, R>

    /**
     * Filters the rows of this [GroupBy] — that is, the key–group pairs — based on the specified [predicate].
     *
     * The [predicate] is a [GroupedRowFilter], which behaves similarly to a [RowFilter] used in [DataFrame.filter],
     * but also provides access to the [group][GroupedDataRow.group] in the current row.
     *
     * ### Example
     * ```kotlin
     * // Keep only key–group pairs where the "category" key equals "Engineer"
     * // or where the group contains at least 5 rows
     * gb.filter { category == "Engineer" || group.rowsCount() >= 5 }
     * ```
     *
     * @param [predicate] A [GroupedRowFilter] used to determine which groups should be retained.
     * @return A new [GroupBy] containing only the key–group pairs that satisfy the [predicate].
     */
    public fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G>

    /**
     * Converts this [GroupBy] into a [DataFrame].
     *
     * Each row of the resulting [DataFrame] represents a unique key–group pair:
     * a row from [keys] and its corresponding group of rows (as [DataFrame]).
     *
     * If [groupedColumnName] is provided, the groups will be stored
     * in a [FrameColumn] with that name; otherwise, a default name is used.
     *
     * @param groupedColumnName The name of the column in which to store grouped data;
     * if `null`, a default name will be used.
     * @return A new [DataFrame] that includes the grouping key columns together
     * with a [FrameColumn] containing the corresponding groups.
     */
    @Refine
    @Interpretable("GroupByToDataFrame")
    public fun toDataFrame(groupedColumnName: String? = null): DataFrame<T>

    /**
     * Represents a single key–group pair in a [GroupBy].
     *
     * @property key The key of the group, represented as a [DataRow].
     * @property group The [DataFrame] containing all rows that belong to the group.
     */
    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

/**
 * Represents a dataframe-like structure with grouped values, offering aggregation capabilities.
 */
public interface Grouped<out T> : Aggregatable<T>

/**
 * An intermediate class used in [`GroupBy` reducing][GroupByDocs.Reducing] operations.
 *
 * Serves as a transitional step between performing a reduction on groups
 * and specifying how the resulting reduced rows should be represented
 * in a new [DataFrame].
 *
 * Available transformation methods:
 * * [concat][ReducedGroupBy.concat] — concatenates all reduced rows into a single [DataFrame];
 * * [values][ReducedGroupBy.values] — creates a [DataFrame] with new rows by transforming each reduced row
 *   using [ColumnsForAggregateSelectionDsl];
 * * [into][ReducedGroupBy.into] — creates a new column with values computed using a [RowExpression] for each row,
 *   or a new [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   containing each group reduced to a single row.
 *
 * Each method returns a new [DataFrame] that includes the grouping key columns,
 * containing all unique grouping key values (or value combinations for multiple keys)
 * together with their corresponding reduced rows.
 *
 * See also: [`GroupBy grammar`][Grammar].
 *
 * For more information, refer to: {@include [DocumentationUrls.GroupByReducing]}
 */
public class ReducedGroupBy<T, G>(
    @PublishedApi internal val groupBy: GroupBy<T, G>,
    @PublishedApi internal val reducer: Selector<DataFrame<G>, DataRow<G>?>,
) {
    override fun toString(): String = "ReducedGroupBy(groupBy=$groupBy, reducer=$reducer)"
}

@PublishedApi
internal fun <T, G> GroupBy<T, G>.reduce(reducer: Selector<DataFrame<G>, DataRow<G>?>): ReducedGroupBy<T, G> =
    ReducedGroupBy(this, reducer)

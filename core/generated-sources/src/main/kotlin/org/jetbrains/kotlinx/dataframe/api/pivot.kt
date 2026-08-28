package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateBody
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateDslDocs
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
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.CSDslLink
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotGroupByImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotInAggregateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.PivotChainColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * Splits the rows of this [<code>DataFrame</code>][DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [<code>columns</code>][columns] of the original [<code>DataFrame</code>][DataFrame].
 *
 * Returns a [<code>Pivot</code>][Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [<code>column groups</code>][ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [<code>DataFrame</code>][DataFrame]).
 *
 * Works like [<code>DataFrame.groupBy</code>][DataFrame.groupBy] but groups rows horizontally.
 *
 * A [<code>Pivot</code>][Pivot] can then be:
 * * [<code>reduced</code>][Reducing] into a [<code>DataRow</code>][DataRow], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][Aggregation] into a [<code>DataRow</code>][DataRow], where each group is transformed into a new row of derived values;
 * * [<code>grouped</code>][Grouping] into a [<code>PivotGroupBy</code>][PivotGroupBy] structure, which combines [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 */
internal interface PivotDocs {

    /**
     * ## [<code>pivot</code>][pivot] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * ### Create [<code>Pivot</code>][Pivot]
     *
     * [<code>**`pivot`**</code>][pivot]**`(`**`inward: `[<code>`Boolean`</code>][Boolean]**` = true) {  `**`pivotColumns: `[<code>`PivotColumnsSelector`</code>][PivotColumnsSelector]**` }`**
     *
     * ### Reduce [<code>Pivot</code>][Pivot] into [<code>DataRow</code>][DataRow]
     *
     * [<code>Pivot</code>][Pivot]`.`[<code>**`minBy`**</code>][Pivot.minBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`maxBy`**</code>][Pivot.maxBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`first`**</code>][Pivot.first]`  [ `**` {  `**`rowCondition: `[<code>`RowFilter`</code>][RowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`last`**</code>][Pivot.last]`  [ `**`{  `**`rowCondition: `[<code>`RowFilter`</code>][RowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`medianBy`**</code>][Pivot.medianBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`percentileBy`**</code>][Pivot.percentileBy]**`(`**`percentile: `[<code>`Double`</code>][Double]**`)  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][ReducedPivot.with]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`values`**</code>][ReducedPivot.values]**`  {  `**`valueColumns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * ### Aggregate [<code>Pivot</code>][Pivot] into [<code>DataRow</code>][DataRow]
     *
     * [<code>Pivot</code>][Pivot]`.`[<code>**`count`**</code>][Pivot.count]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`frames`**</code>][Pivot.frames]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`with`**</code>][Pivot.with]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`values`**</code>][Pivot.values]**`  {  `**`valueColumns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`aggregate`**</code>][Pivot.aggregate]**`  {  `**`aggregations: `[<code>`AggregateDsl`</code>][AggregateDsl]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code><aggregation_statistic></code>][PivotDocs.AggregationStatistics]
     *
     * ### Group [<code>Pivot</code>][Pivot] into [<code>PivotGroupBy</code>][PivotGroupBy] and reduce / aggregate it
     *
     * [<code>Pivot</code>][Pivot]`.`[<code>**`groupBy`**</code>][Pivot.groupBy]**`  {  `**`columns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`groupByOther`**</code>][Pivot.groupByOther]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`default`**</code>][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code><pivot_groupBy_reducer></code>][PivotGroupByDocs.Reducing]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code><pivot_groupBy_aggregator></code>][PivotGroupByDocs.Aggregation]
     *
     * Check out [<code>PivotGroupBy Grammar</code>][PivotGroupByDocs.Grammar] for more information.
     */
    typealias Grammar = Nothing

    /**
     * ### [<code>Pivot</code>][Pivot] reducing
     *
     * Each [<code>Pivot</code>][Pivot] group can be collapsed into a single row and then concatenated
     * into a new [<code>DataRow</code>][DataRow] with these row values (or their derived representation)
     * with [<code>pivot</code>][pivot] keys as top-level columns or as [<code>column groups</code>][ColumnGroup].
     *
     * Reducing is a specific case of [<code>aggregation</code>][Aggregation].
     *
     * First, choose a [<code>Pivot</code>][Pivot] reducing method:
     * * [<code>first</code>][Pivot.first], [<code>last</code>][Pivot.last] — take the first or last row
     *   (optionally, the first or last one that satisfies a predicate) of each group;
     * * [<code>minBy</code>][Pivot.minBy] / [<code>maxBy</code>][Pivot.maxBy] — take the row with the minimum or maximum value
     *   of the given [<code>RowExpression</code>][RowExpression] evaluated on rows within each group;
     * * [<code>medianBy</code>][Pivot.medianBy] / [<code>percentileBy</code>][Pivot.percentileBy] — take the row at the position closest
     *   to the estimated median/percentile index of the [<code>RowExpression</code>][RowExpression]'s results calculated on rows within each group.
     *
     * These functions return a [<code>ReducedPivot</code>][ReducedPivot], which can then be transformed into a new [<code>DataFrame</code>][DataFrame]
     * containing a single combined row (either using the original reduced rows or their transformed versions)
     * through one of the following methods:
     * * [<code>values</code>][ReducedPivot.values] — creates a new row containing the values
     *   from the reduced rows in the selected columns and produces a [<code>DataRow</code>][DataRow] of
     *   these values;
     * * [<code>with</code>][ReducedPivot.with] — computes a new value for each reduced row using a [<code>RowExpression</code>][RowExpression],
     *   and produces a [<code>DataRow</code>][DataRow] containing these computed values.
     *
     * Each method returns a new [<code>DataRow</code>][DataRow] with [<code>pivot</code>][pivot] keys as top-level columns
     * (or as [<code>column groups</code>][ColumnGroup]) and values composed of the reduced results from each group.
     *
     * Check out [<code>`Pivot grammar`</code>][Grammar].
     *
     * For more information: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
     */
    typealias Reducing = Nothing

    /**
     * ### [<code>Pivot</code>][Pivot] aggregation
     *
     * Each [<code>Pivot</code>][Pivot] group can be aggregated — that is, transformed into a new value, [<code>DataRow</code>][DataRow], or [<code>DataFrame</code>][DataFrame] —
     * and then concatenated into a single [<code>DataRow</code>][DataRow] composed of these aggregated results,
     * with [<code>pivot</code>][pivot] keys as top-level columns or as [<code>column groups</code>][ColumnGroup].
     *
     * The following aggregation methods are available:
     * * [<code>frames</code>][Pivot.frames] — returns this [<code>Pivot</code>][Pivot] as a [<code>DataRow</code>][DataRow] with pivot keys as columns
     *   (or [<code>column groups</code>][ColumnGroup]) and corresponding groups stored as [<code>FrameColumn</code>][FrameColumn]s;
     * * [<code>values</code>][Pivot.values] — creates a [<code>DataRow</code>][DataRow] containing values collected into a single [<code>List</code>][List]
     *   from all rows of each group for the selected columns
     *   (values from [<code>column groups</code>][ColumnGroup] are collected into a [<code>DataFrame</code>][DataFrame]);
     * * [<code>count</code>][Pivot.count] — creates a [<code>DataRow</code>][DataRow] containing the pivot key columns and an additional column
     *   with the number of rows in each corresponding group;
     * * [<code>with</code>][Pivot.with] — creates a [<code>DataRow</code>][DataRow] containing values computed using a [<code>RowExpression</code>][RowExpression]
     *   across all rows of each group.
     *   Values of the [<code>DataRow</code>][DataRow] type are collected into a [<code>DataFrame</code>][DataFrame], and the resulting column is a [<code>FrameColumn</code>][FrameColumn].
     *   Values of other types are collected into a [<code>List</code>][List], and the resulting column is a [<code>DataColumn</code>][DataColumn] of [<code>List</code>][List];
     * * [<code>aggregate</code>][Pivot.aggregate] — performs a set of custom aggregations using [<code>AggregateDsl</code>][AggregateDsl],
     *   allowing computation of one or more derived values per group;
     * * [<code>Various aggregation statistics</code>][AggregationStatistics] — predefined shortcuts
     *   for common statistical aggregations such as [<code>sum</code>][Pivot.sum], [<code>mean</code>][Pivot.mean],
     *   [<code>median</code>][Pivot.median], and others.
     *
     * Each of these methods returns a new [<code>DataRow</code>][DataRow] with [<code>pivot</code>][pivot] keys as top-level columns
     * (or as [<code>column groups</code>][ColumnGroup]) and values representing the aggregated results of each group.
     *
     * Check out [<code>`Pivot grammar`</code>][Grammar].
     *
     * For more information: [See "`Pivot` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#aggregation)
     */
    typealias Aggregation = Nothing

    /**
     * ### [<code>Pivot</code>][Pivot] grouping
     *
     * [<code>Pivot</code>][Pivot] can be grouped with [<code>groupBy</code>][Pivot.groupBy] method. It will produce a [<code>PivotGroupBy</code>][PivotGroupBy].
     *
     * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] and [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
     * representing a matrix table with vertical [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
     * and horizontal [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
     * where each cell represents a group corresponding
     * to both the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
     *
     * Reversed order of `pivot` and `groupBy`
     * (i.e., [<code>DataFrame.pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [<code>Pivot.groupBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
     * will produce the same result.
     *
     * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
     * or [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * Check out [<code>PivotGroupBy Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
     *
     * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
     */
    typealias Grouping = Nothing

    /**
     * ### [<code>Pivot</code>][Pivot] aggregation statistics
     *
     * Provides predefined shortcuts for the most common statistical aggregation operations
     * that can be applied to each group within a [<code>Pivot</code>][Pivot].
     *
     * Each function computes a statistic across the rows of a group and returns the result as
     * a new row of computed values in the resulting [<code>DataFrame</code>][DataFrame].
     *
     * * [<code>count</code>][Pivot.count] — calculate the number of rows in each group
     *   (optionally counting only rows that satisfy the given predicate);
     * * [<code>max</code>][Pivot.max] / [<code>maxOf</code>][Pivot.maxOf] / [<code>maxFor</code>][Pivot.maxFor] —
     *   calculate the maximum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>min</code>][Pivot.min] / [<code>minOf</code>][Pivot.minOf] / [<code>minFor</code>][Pivot.minFor] —
     *   calculate the minimum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>sum</code>][Pivot.sum] / [<code>sumOf</code>][Pivot.sumOf] / [<code>sumFor</code>][Pivot.sumFor] —
     *   calculate the sum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>mean</code>][Pivot.mean] / [<code>meanOf</code>][Pivot.meanOf] / [<code>meanFor</code>][Pivot.meanFor] —
     *   calculate the mean (average) of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>std</code>][Pivot.std] / [<code>stdOf</code>][Pivot.stdOf] / [<code>stdFor</code>][Pivot.stdFor] —
     *   calculate the standard deviation of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>median</code>][Pivot.median] / [<code>medianOf</code>][Pivot.medianOf] / [<code>medianFor</code>][Pivot.medianFor] —
     *   calculate the median of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>percentile</code>][Pivot.percentile] / [<code>percentileOf</code>][Pivot.percentileOf] / [<code>percentileFor</code>][Pivot.percentileFor] —
     *   calculate a specified percentile of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group.
     *
     * For more information: [See "`pivot` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#pivot-statistics)
     */
    typealias AggregationStatistics = Nothing
}

/**
 * A specialized [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] that allows specifying [<code>pivot</code>][pivot] key ordering
 * using the [<code>then</code>][then] function.
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
     * Specifies the ordering of the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [<code>other</code>][other].
     *
     * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> ColumnsResolver<C>.then(other: ColumnsResolver<C>): ColumnSet<C> =
        PivotChainColumnSet(this, other)

    /**
     * Specifies the ordering of the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [<code>other</code>][other].
     *
     * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> String.then(other: ColumnsResolver<C>): ColumnSet<C> = toColumnOf<C>() then other

    /**
     * Specifies the ordering of the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [<code>other</code>][other].
     *
     * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
     */
    public infix fun <C> ColumnsResolver<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

    /**
     * Specifies the ordering of the [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] key columns.
     *
     * In the resulting [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot], the receiver column (or columns) will appear
     * one level above the keys from columns provided by [<code>other</code>][other].
     *
     * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
     *
     * @receiver pivot key column(s) that appear **above** in the hierarchy.
     * @param [other] pivot key column(s) that appear **below** (as child keys of the receiver
     * columns keys) in the hierarchy.
     * @return A special [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] representing the hierarchical pivot key ordering.
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
 * Splits the rows of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [<code>columns</code>][columns] of the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * Works like [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] but groups rows horizontally.
 *
 * A [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] can then be:
 * * [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing] into a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] into a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], where each group is transformed into a new row of derived values;
 * * [<code>grouped</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grouping] into a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 * ### This `pivot` Overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param columns The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [<code>Pivot</code>][Pivot] containing the unique values of the selected column as new columns
 * (or as [<code>column groups</code>][ColumnGroup] for multiple key columns),
 * with their corresponding groups of rows represented as [<code>DataFrame</code>][DataFrame]s.
 */
public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> =
    PivotImpl(this, columns, inward)

/**
 * Splits the rows of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] and groups them horizontally
 * into new columns based on values from one or several provided [<code>columns</code>][columns] of the original [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] — a dataframe-like structure that contains all unique combinations of key values
 * as columns (or [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]).
 *
 * Works like [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] but groups rows horizontally.
 *
 * A [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] can then be:
 * * [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Reducing] into a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Aggregation] into a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow], where each group is transformed into a new row of derived values;
 * * [<code>grouped</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grouping] into a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 * ### This `pivot` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * * [<code>pivot</code>][pivot] with a single key column produces a [<code>Pivot</code>][Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][pivot] with multiple keys combined using [<code>and</code>][and] produces a [<code>Pivot</code>][Pivot]
 *   with independent [<code>column groups</code>][ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 *
 * For pivoting by multiple keys combinations from different columns, use the [<code>pivot</code>][pivot] overload with [<code>PivotDsl</code>][PivotDsl].
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
 * @param columns The [<code>Column Names</code>][String] that defines which columns are used
 * as keys for pivoting.
 * @return A new [<code>Pivot</code>][Pivot] containing the unique values of the selected column as new columns
 * (or as [<code>column groups</code>][ColumnGroup] for multiple key columns),
 * with their corresponding groups of rows represented as [<code>DataFrame</code>][DataFrame]s.
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
 * Computes a **presence matrix**
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics))
 * for the values in the
 * specified [<code>columns</code>][columns] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the remaining columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [<code>groupByOther</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also:
 * * [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.pivotCounts], which performs a similar operation
 *   but counts the number of matching rows instead of checking for their presence
 *   to produce a count matrix.
 *
 * ### This `pivotMatches` Overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a [<code>Boolean</code>][Boolean] presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: PivotColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().matches()

/**
 * Computes a **presence matrix**
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics))
 * for the values in the
 * specified [<code>columns</code>][columns] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the remaining columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [<code>groupByOther</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also:
 * * [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.pivotCounts], which performs a similar operation
 *   but counts the number of matching rows instead of checking for their presence
 *   to produce a count matrix.
 *
 * ### This `pivotMatches` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The [<code>Column Names</code>][String] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
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
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the remaining columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][DataFrame.pivot], [<code>groupByOther</code>][Pivot.groupByOther],
 * and [<code>count</code>][PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [<code>Boolean</code>][Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 */
internal typealias DataFramePivotCountsCommonDocs = Nothing

/**
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the remaining columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [<code>groupByOther</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [<code>Boolean</code>][Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <T> DataFrame<T>.pivotCounts(inward: Boolean = true, columns: PivotColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().count()

/**
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the remaining columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot], [<code>groupByOther</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupByOther],
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then
 * to produce a [<code>Boolean</code>][Boolean] matrix.
 *
 * ### This `pivotCounts` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The [<code>Column Names</code>][String] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
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
 * Pivots the selected [<code>columns</code>][columns] of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups.
 * Returns a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 *
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] and [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * representing a matrix table with vertical [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
 * and horizontal [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
 * where each cell represents a group corresponding
 * to both the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
 *
 * Reversed order of `pivot` and `groupBy`
 * (i.e., [<code>DataFrame.pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [<code>Pivot.groupBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
 * will produce the same result.
 *
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
 * or [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>PivotGroupBy Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `pivot` Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are pivoted.
 * @return A new [<code>PivotGroupBy</code>][PivotGroupBy] that preserves the original [<code>groupBy</code>][groupBy] key columns
 * and pivots the provided columns.
 */
public fun <G> GroupBy<*, G>.pivot(inward: Boolean = true, columns: PivotColumnsSelector<G, *>): PivotGroupBy<G> =
    PivotGroupByImpl(this, columns, inward)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivot(vararg columns: AnyColumnReference, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

/**
 * Pivots the selected [<code>columns</code>][columns] of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups.
 * Returns a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 *
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] and [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * representing a matrix table with vertical [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
 * and horizontal [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
 * where each cell represents a group corresponding
 * to both the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
 *
 * Reversed order of `pivot` and `groupBy`
 * (i.e., [<code>DataFrame.pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [<code>Pivot.groupBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
 * will produce the same result.
 *
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
 * or [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>PivotGroupBy Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 *
 * Pivoted columns can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and pivot it:
 * df.pivot { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `pivot` Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`gb`</code>`.`[<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param [columns] The [<code>Column names</code>][String] that defines which columns are pivoted.
 * @return A new [<code>PivotGroupBy</code>][PivotGroupBy] that preserves the original [<code>groupBy</code>][groupBy] key columns
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
 * Computes a **presence matrix**
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics))
 * for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][GroupBy], returning a new [<code>DataFrame</code>][DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][GroupBy.pivot]
 * and [<code>matches</code>][PivotGroupBy.matches] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [<code>pivotCounts</code>][GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 */
internal typealias GroupByPivotMatchesCommonDocs = Nothing

/**
 * Computes a **presence matrix**
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics))
 * for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 *         pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 */
public fun <G> GroupBy<*, G>.pivotMatches(inward: Boolean = true, columns: PivotColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).matches()

/**
 * Computes a **presence matrix**
 * (similar to [one-hot encoding](https://en.wikipedia.org/wiki/One-hot#Machine_learning_and_statistics))
 * for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Boolean</code>][Boolean] value indicating whether a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts], which performs a similar operation
 * but counts the number of matching rows instead of checking for their presence.
 *
 * ### This `pivotMatches` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The [<code>Column Names</code>][String] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
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
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][GroupBy], returning a new [<code>DataFrame</code>][DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][GroupBy.pivot]
 * and [<code>count</code>][PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 */
internal typealias GroupByPivotCountsCommonDocs = Nothing

/**
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
 *         pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 */
public fun <G> GroupBy<*, G>.pivotCounts(inward: Boolean = true, columns: PivotColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).count()

/**
 * Computes a **count matrix** (similar to frequency encoding) for the values in the
 * specified [<code>columns</code>][columns] within each group of this [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy], returning a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] where:
 * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
 *   (they become [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   corresponding to value combinations when using [<code>then</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl.then],
 *   similar to [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]);
 * * **Rows** correspond to all unique combinations of values from the grouping columns;
 *   each combination is represented in dedicated key columns that store
 *   a distinct set of values for each row
 *   (similar to [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] in [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy]).
 * * **Cells** contain a [<code>Int</code>][Int] value indicating number a row with the corresponding
 *   combination of values (horizontal and vertical) exists in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot]
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count] operations into a single call.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches], which performs a similar operation
 * but check if there is any matching row instead of counting then.
 *
 * ### This `pivotCounts` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The [<code>Column Names</code>][String] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
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
 * Pivots the selected [<code>columns</code>][columns] within each group for further
 * [<code>pivot aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation].
 *
 * This function itself does not directly modify the result of [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate],
 * but instead creates an intermediate [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns produced by its [<code>aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] are then
 * inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate]
 * when those aggregation functions are executed (as usual aggregations).
 * Their structure depends on the specific
 * [<code>PivotGroupBy aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] used.
 *
 * See [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] and [<code>PivotGroupByDocs.Aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] for more information.
 *
 * For more information: [See "`pivot` inside aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-inside-aggregate)
 *
 * Check out [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 * and [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts] shortcuts.
 *
 * ### This `pivot` overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
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
 * @param [columns] The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A [<code>PivotGroupBy</code>][PivotGroupBy] for further [<code>aggregations</code>][PivotGroupByDocs.Aggregation].
 */
public fun <T> AggregateGroupedDsl<T>.pivot(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): PivotGroupBy<T> = PivotInAggregateImpl(this, columns, inward)

/**
 * Pivots the selected [<code>columns</code>][columns] within each group for further
 * [<code>pivot aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation].
 *
 * This function itself does not directly modify the result of [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate],
 * but instead creates an intermediate [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy].
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns produced by its [<code>aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] are then
 * inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate]
 * when those aggregation functions are executed (as usual aggregations).
 * Their structure depends on the specific
 * [<code>PivotGroupBy aggregations</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] used.
 *
 * See [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot] and [<code>PivotGroupByDocs.Aggregation</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] for more information.
 *
 * For more information: [See "`pivot` inside aggregation" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-inside-aggregate)
 *
 * Check out [<code>`PivotGroupBy` Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * See also [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches]
 * and [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts] shortcuts.
 *
 * ### This `pivot` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [columns] The [<code>Column Names</code>][String] that defines which columns are used as [<code>pivot</code>][pivot] keys for the operation.
 * @return A [<code>PivotGroupBy</code>][PivotGroupBy] for further [<code>aggregations</code>][PivotGroupByDocs.Aggregation].
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
 * Computes the [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivotMatches] statistic for the selected [<code>columns</code>][columns]
 * within each group and adds it to the [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches].
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [<code>GroupBy.pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches] for more details.
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts].
 *
 * ### This `pivotMatches` overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * @param columns The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 * This [<code>DataFrame</code>][DataFrame] is added to the [<code>aggregate</code>][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotMatches(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).matches()

/**
 * Computes the [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivotMatches] statistic for the selected [<code>columns</code>][columns]
 * within each group and adds it to the [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [<code>matches</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.matches].
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [<code>GroupBy.pivotMatches</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotMatches] for more details.
 *
 * For more information: [See `pivotMatches` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotmatches)
 *
 * See also: [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotCounts].
 *
 * ### This `pivotMatches` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param columns The [<code>Column Names</code>][String] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a Boolean presence matrix — with grouping key columns as rows,
 * pivot key values as columns, and `true`/`false` cells indicating existing combinations.
 * This [<code>DataFrame</code>][DataFrame] is added to the [<code>aggregate</code>][Grouped.aggregate] result.
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
 * Computes the [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivotCounts] statistic for the selected [<code>columns</code>][columns]
 * within each group and adds it to the [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count].
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [<code>GroupBy.pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts] for more details.
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches].
 *
 * ### This `pivotCounts` overload
 * Select or express pivot columns using the [<code>PivotDsl</code>][PivotDsl].
 *
 * [<code>PivotDsl</code>][org.jetbrains.kotlinx.dataframe.api.PivotDsl] defines how key columns are selected and structured in a [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot]:
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with a single key column produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] containing one column for each unique key
 *   (i.e., key column unique values) with the corresponding group;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys combined using [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.and] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with independent [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] for each key column, each having subcolumns
 *   with the keys corresponding to their unique values;
 * * [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] with multiple keys ordered using [<code>then</code>][then] produces a [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot]
 *   with nested [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], representing a hierarchical structure of
 *   keys combinations from the pivoted columns — i.e., one group per unique key combination.
 *
 * See [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
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
 * ### Example
 * ```kotlin
 * df.groupBy { name.firstName }.aggregate {
 *     // Compute number of for all unique values of "city"
 *     // across all "name.firstName" key values and adds it to the aggregation result
 *     pivotCounts { city }
 * }
 * ```
 *
 * @param [inward] Defines whether the generated columns are nested under a supercolumn:
 *   - `true` (default) — pivot key columns are nested under a supercolumn named after
 *     the original pivoted column (independently for multiple pivoted columns);
 *   - `false` — pivot key columns are not nested (i.e., placed at the top level);
 * @param columns The [<code>Pivot Columns Selector</code>][PivotColumnsSelector] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
 * pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 * This [<code>DataFrame</code>][DataFrame] is added to the [<code>aggregate</code>][Grouped.aggregate] result.
 */
public fun <T> AggregateGroupedDsl<T>.pivotCounts(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).count()

/**
 * Computes the [<code>pivotCounts</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivotCounts] statistic for the selected [<code>columns</code>][columns]
 * within each group and adds it to the [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate] result.
 *
 * This is a shortcut for combining [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot]
 * and [<code>count</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy.count].
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] columns are inserted into the final [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * returned by [<code>aggregate</code>][org.jetbrains.kotlinx.dataframe.api.Grouped.aggregate].
 * The resulting column name can be specified using [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.into].
 *
 * See [<code>GroupBy.pivotCounts</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivotCounts] for more details.
 *
 * For more information: [See `pivotCounts` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivotcounts)
 *
 * See also: [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivot], [<code>pivotMatches</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedDsl.pivotMatches].
 *
 * ### This `pivotCounts` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param columns The [<code>Column Names</code>][String] that defines which columns are used
 * as keys for pivoting and in which order.
 * @return A new [<code>DataFrame</code>][DataFrame] representing a counting matrix — with grouping key columns as rows,
 * pivot key values as columns, and the number of rows with the corresponding combinations in the cells.
 * This [<code>DataFrame</code>][DataFrame] is added to the [<code>aggregate</code>][Grouped.aggregate] result.
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
 * as columns (or [<code>column groups</code>][ColumnGroup] for multiple keys) with a single row
 * with the corresponding groups for each key combination (each represented as a [<code>DataFrame</code>][DataFrame]).
 *
 * Similar to [<code>GroupBy</code>][GroupBy] but contains horizontal groups.
 *
 * A [<code>Pivot</code>][Pivot] can be:
 * * [<code>reduced</code>][PivotDocs.Reducing] into a [<code>DataRow</code>][DataRow], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][PivotDocs.Aggregation] into a [<code>DataRow</code>][DataRow], where each group is transformed into a new row of derived values;
 * * [<code>grouped</code>][PivotDocs.Grouping] into a [<code>PivotGroupBy</code>][PivotGroupBy] structure, which combines [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][DataFrame].
 *
 * Check out [<code>`Pivot` Grammar</code>][PivotDocs.Grammar].
 *
 * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
 */
public interface Pivot<T> : Aggregatable<T>

/**
 * A specialized [<code>ColumnsSelector</code>][ColumnsSelector] used for selecting columns in a [<code>pivot</code>][pivot] operation.
 *
 * Provides [<code>PivotDsl</code>][PivotDsl] both as the receiver and the lambda parameter, and expects
 * a [<code>ColumnsResolver</code>][ColumnsResolver] as the return value.
 *
 * Enables defining the hierarchy of pivot columns using [<code>then</code>][PivotDsl.then].
 */
public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnsResolver<C>>

/**
 * An intermediate class used in [<code>`Pivot` reducing</code>][PivotDocs.Reducing] operations.
 *
 * Serves as a transitional step between performing a reduction on pivot groups
 * and specifying how the resulting reduced rows should be represented
 * in a resulting [<code>DataRow</code>][DataRow].
 *
 * Available transformation methods:
 * * [<code>values</code>][ReducedPivot.values] — creates a new row containing the values
 *   from the reduced rows in the selected columns and produces a [<code>DataRow</code>][DataRow] of
 *   these values;
 * * [<code>with</code>][ReducedPivot.with] — computes a new value for each reduced row using a [<code>RowExpression</code>][RowExpression],
 *   and produces a [<code>DataRow</code>][DataRow] containing these computed values.
 *
 * Each method returns a new [<code>DataRow</code>][DataRow] with [<code>pivot</code>][pivot] keys as top-level columns
 * (or as [<code>column groups</code>][ColumnGroup]) and values composed of the reduced results from each group.
 *
 * Check out [<code>`Pivot grammar`</code>][Grammar].
 *
 * For more information, refer to: [See "`Pivot` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#reducing)
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

    typealias GroupingColumns = Nothing

    /**
     * * **Columns** represent all unique values from the selected [<code>columns</code>][columns]
     *   (they become [<code>column groups</code>][ColumnGroup]
     *   corresponding to value combinations when using [<code>then</code>][PivotDsl.then],
     *   similar to [<code>pivot</code>][pivot]);
     * * **Rows** correspond to all unique combinations of values from the grouping columns;
     *   each combination is represented in dedicated key columns that store
     *   a distinct set of values for each row
     *   (similar to [<code>keys</code>][GroupBy.keys] in [<code>GroupBy</code>][GroupBy]).
     */
    typealias ResultingMatrixShortcutDescription = Nothing

    typealias Grammar = Nothing
    typealias Reducing = Nothing
    typealias Aggregation = Nothing
}

/**
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] is a dataframe-like structure that combines [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] and [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy],
 * representing a matrix table with vertical [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups (as columns)
 * and horizontal [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] groups (as rows),
 * where each cell represents a group corresponding
 * to both the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] and [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] key.
 *
 * Reversed order of `pivot` and `groupBy`
 * (i.e., [<code>DataFrame.pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] + [<code>Pivot.groupBy</code>][org.jetbrains.kotlinx.dataframe.api.Pivot.groupBy] or [<code>DataFrame.groupBy</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy] + [<code>GroupBy.pivot</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.pivot])
 * will produce the same result.
 *
 * [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] can be [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Reducing]
 * or [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Check out [<code>PivotGroupBy Grammar</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupByDocs.Grammar].
 *
 * For more information: [See "`pivot` + `groupBy`" on the documentation website.](https://kotlin.github.io/dataframe/pivot.html#pivot-groupby)
 */
public interface PivotGroupBy<out T> : Aggregatable<T> {

    /**
     * Aggregates this [<code>PivotGroupBy</code>][PivotGroupBy] using the provided statistics
     * inside the [<code>AggregateDsl</code>][AggregateDsl].
     *
     * Returns a new [<code>DataFrame</code>][DataFrame] with the [<code>groupBy</code>][groupBy] key columns
     * and the [<code>pivot</code>][pivot] keys as top-level columns on top level,
     * and the corresponding aggregated values in new nested columns.
     *
     * [<code>AggregateDsl</code>][AggregateDsl] allows to compute statistics on the columns within groups in [<code>PivotGroupBy</code>][PivotGroupBy]
     * and store the results as a new column using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into]. The given [<code>expression</code>][body] is applied to each group independently.
     *
     *
     * The resulting [<code>DataFrame</code>][DataFrame] has the same structure as the original
     * [<code>PivotGroupBy</code>][PivotGroupBy];
     * instead of the groups, there are new columns of aggregated values created with [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into].
     *
     * You can use any of [<code>DataFrame Aggregation Statistics</code>][org.jetbrains.kotlinx.dataframe.aggregation.DataFrameAggregationStatistics]
     * or any custom aggregation function.
     *
     * Aggregated values can be either simple values, [<code>data rows</code>][org.jetbrains.kotlinx.dataframe.DataRow] or even
     * [<code>data frames</code>][org.jetbrains.kotlinx.dataframe.DataFrame]. Including them in the result using [<code>into</code>][org.jetbrains.kotlinx.dataframe.aggregation.AggregateDsl.into] will lead
     * to creating [<code>value column</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn],
     * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] respectively
     * in the resulting [<code>DataFrame</code>][DataFrame] while preserving the original structure at higher levels.
     *
     *
     *
     *
     *
     *
     * Check out [<code>`PivotGroupBy` Grammar</code>][PivotGroupByDocs.Grammar] for more information.
     *
     * For more information: [See `pivot` on the documentation website.](https://kotlin.github.io/dataframe/pivot.html)
     *
     * #### Example
     * ```kotlin
     * df.pivot { city }.groupBy { name.firstName }.aggregate {
     *   // Сount rows within each firstName" × "city" combination group and store the result
     *   // into a new "total" column (a new sub-column under each pivot key column)
     *   count() into "total"
     *
     *   // Compute the maximum in "age" column within each group
     *   // and store it into a new "maxAge" column
     *   // Defaults to -1 for empty groups.
     *   max { age } default -1 into "maxAge"
     * }
     * ```
     *
     * @param body The aggregation logic defined using [<code>AggregateDsl</code>][AggregateDsl].
     * @return A new [<code>DataFrame</code>][DataFrame] with the results of the aggregation applied to each group.
     */
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

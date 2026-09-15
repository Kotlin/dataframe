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
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
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
 * Groups the rows of this [<code>DataFrame</code>][DataFrame] based on the values in one or more specified [<code>key columns</code>][cols].
 * Each unique value in a key column — or a unique tuple of values for multiple columns —
 * defines the group consisting of all rows where the column(s) contain that value combination.
 *
 * Returns a [<code>GroupBy</code>][GroupBy] — a dataframe-like structure that contains all unique combinations of key values
 * along with the corresponding groups of rows (each represented as a [<code>DataFrame</code>][DataFrame]) as rows.
 *
 * A [<code>GroupBy</code>][GroupBy] can then be:
 * * [<code>transformed</code>][Transformation] into a new [<code>GroupBy</code>][GroupBy];
 * * [<code>reduced</code>][Reducing] into a [<code>DataFrame</code>][DataFrame], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][Aggregation] into a [<code>DataFrame</code>][DataFrame], where each group is transformed into one or more rows of derived values;
 * * [<code>pivoted</code>][Pivoting] into a [<code>PivotGroupBy</code>][PivotGroupBy] structure, which combines [<code>pivot</code>][pivot] and [<code>groupBy</code>][groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][DataFrame].
 *
 * Grouping keys can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][GroupBySelectingOptions].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * Don't confuse this with [<code>group</code>][group], which groups column into
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * See also [<code>pivot</code>][DataFrame.pivot] that groups rows of [<code>DataFrame</code>][DataFrame] vertically.
 */
internal interface GroupByDocs {
    /**
     * ## [<code>groupBy</code>][groupBy] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * ### Create and transform [<code>GroupBy</code>][GroupBy]
     *
     * [<code>**`groupBy`**</code>][groupBy]**`(`**`moveToTop: `[<code>`Boolean`</code>][Boolean]**`  = true)  {  `**`columns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByGroup`**</code>][GroupBy.sortByGroup]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByGroupDesc`**</code>][GroupBy.sortByGroupDesc]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByCount`**</code>][GroupBy.sortByCount]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByCountAsc`**</code>][GroupBy.sortByCountAsc]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByKey`**</code>][GroupBy.sortByKey]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByKeyDesc`**</code>][GroupBy.sortByKeyDesc]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortBy`**</code>][GroupBy.sortBy]**`  {  `**`columns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`sortByDesc`**</code>][GroupBy.sortByDesc]**`  {  `**`columns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`updateGroups`**</code>][GroupBy.updateGroups]**`  {  `**`frameExpression`**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`filter`**</code>][GroupBy.filter]**`  {  `**`predicate: `[<code>`GroupedRowFilter`</code>][GroupedRowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`add`**</code>][GroupBy.add]**`(`**`column: `[<code>`DataColumn`</code>][DataColumn]**`)  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**`  }  `**`]`
     *
     * See [<code>GroupBy Transformations</code>][Transformation].
     *
     * ### Reduce [<code>GroupBy</code>][GroupBy] into [<code>DataFrame</code>][DataFrame]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * [<code>GroupBy</code>][GroupBy]`.`[<code>**`minBy`**</code>][GroupBy.minBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`maxBy`**</code>][GroupBy.maxBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`first`**</code>][GroupBy.first]`  [ `**`  {  `**`rowCondition: `[<code>`RowFilter`</code>][RowFilter]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`last`**</code>][GroupBy.last]`  [ `**`  {  `**`rowCondition: `[<code>`RowFilter`</code>][RowFilter]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`medianBy`**</code>][GroupBy.medianBy]**`  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`percentileBy`**</code>][GroupBy.percentileBy]**`(`**`percentile: `[<code>`Double`</code>][Double]**`)  {  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`concat`**</code>][ReducedGroupBy.concat]**`() `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`into`**</code>][ReducedGroupBy.into]**`(`**`column: `[<code>`String`</code>][String]**`) `**`  [ `**`{  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`values`**</code>][ReducedGroupBy.values]**`  {  `**`valueColumns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * See [<code>GroupBy Reducing</code>][Reducing].
     *
     * ### Aggregate [<code>GroupBy</code>][GroupBy] into [<code>DataFrame</code>][DataFrame]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * [<code>GroupBy</code>][GroupBy]`.`[<code>**`concat`**</code>][GroupBy.concat]**`() `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`concatWithKeys`**</code>][GroupBy.concatWithKeys]**`() `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`into`**</code>][GroupBy.into]**`(`**`column: `[<code>`String`</code>][String]**`) `**`  [  `**`{  `**`rowExpression: `[<code>`RowExpression`</code>][RowExpression]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`values`**</code>][Grouped.values]**`  {  `**`valueColumns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`count`**</code>][Grouped.count]**`() `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`countDistinct`**</code>][Grouped.countDistinct]**`() `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`aggregate`**</code>][Grouped.aggregate]**`  {  `**`aggregations: `[<code>`AggregateDsl`</code>][AggregateDsl]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code><aggregation_statistic></code>][AggregationStatistics]
     *
     *  See [<code>GroupBy Aggregations</code>][Aggregation].
     *
     * ### Pivot [<code>GroupBy</code>][GroupBy] into [<code>PivotGroupBy</code>][PivotGroupBy] and reduce / aggregate it
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * [<code>GroupBy</code>][GroupBy]`.`[<code>**`pivot`**</code>][GroupBy.pivot]**`  {  `**`columns: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `    [ `__`.`__[<code>**`default`**</code>][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code><pivot_groupBy_reducer></code>][PivotGroupByDocs.Reducing]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code><pivot_groupBy_groupBy></code>][PivotGroupByDocs.Aggregation]
     *
     * Check out [<code>PivotGroupBy Grammar</code>][PivotGroupByDocs.Grammar] for more information.
     */
    typealias Grammar = Nothing

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
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
     * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`("length", "age")`
     *
     *
     *
     */
    typealias GroupBySelectingOptions = Nothing

    /**
     * ### [<code>GroupBy</code>][GroupBy] aggregation statistics
     *
     * Provides predefined shortcuts for the most common statistical aggregation operations
     * that can be applied to each group within a [<code>GroupBy</code>][GroupBy].
     *
     * Each function computes a statistic across the rows of a group and returns the result as
     * a new column (or several columns) in the resulting [<code>DataFrame</code>][DataFrame].
     *
     * * [<code>count</code>][Grouped.count] — calculate the number of rows in each group
     *   (optionally counting only rows that satisfy the given predicate);
     * * [<code>`countDistinct`</code>][Grouped.countDistinct] — calculate the number of distinct rows in each group
     *   (or distinct combinations of values in selected columns);
     * * [<code>max</code>][Grouped.max] / [<code>maxOf</code>][Grouped.maxOf] / [<code>maxFor</code>][Grouped.maxFor] —
     *   calculate the maximum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>min</code>][Grouped.min] / [<code>minOf</code>][Grouped.minOf] / [<code>minFor</code>][Grouped.minFor] —
     *   calculate the minimum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>sum</code>][Grouped.sum] / [<code>sumOf</code>][Grouped.sumOf] / [<code>sumFor</code>][Grouped.sumFor] —
     *   calculate the sum of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>mean</code>][Grouped.mean] / [<code>meanOf</code>][Grouped.meanOf] / [<code>meanFor</code>][Grouped.meanFor] —
     *   calculate the mean (average) of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>std</code>][Grouped.std] / [<code>stdOf</code>][Grouped.stdOf] / [<code>stdFor</code>][Grouped.stdFor] —
     *   calculate the standard deviation of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>median</code>][Grouped.median] / [<code>medianOf</code>][Grouped.medianOf] / [<code>medianFor</code>][Grouped.medianFor] —
     *   calculate the median of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group;
     * * [<code>percentile</code>][Grouped.percentile] / [<code>percentileOf</code>][Grouped.percentileOf] / [<code>percentileFor</code>][Grouped.percentileFor] —
     *   calculate a specified percentile of all values on the selected columns / by a row expression /
     *   for each of the selected columns within each group.
     *
     * For more information: [See "`groupBy` statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html#groupby-statistics)
     */
    typealias AggregationStatistics = Nothing

    /**
     * ### [<code>GroupBy</code>][GroupBy] transformations
     *
     * A [<code>GroupBy</code>][GroupBy] can be transformed into a new [<code>GroupBy</code>][GroupBy] using one of the following methods:
     * * [<code>sortByGroup</code>][GroupBy.sortByGroup] / [<code>sortByGroupDesc</code>][GroupBy.sortByGroupDesc] — sorts the **order
     *   of groups** (and their corresponding keys) by values computed with a [<code>DataFrameExpression</code>][DataFrameExpression] applied to each group;
     * * [<code>sortByCount</code>][GroupBy.sortByCount] / [<code>sortByCountAsc</code>][GroupBy.sortByCountAsc] — sorts the **order
     *   of groups** (and their corresponding keys) by the number of rows they contain;
     * * [<code>sortByKey</code>][GroupBy.sortByKey] / [<code>sortByKeyDesc</code>][GroupBy.sortByKeyDesc] — sorts the **order
     *   of groups** (and their corresponding keys) by the grouping key values;
     * * [<code>sortBy</code>][GroupBy.sortBy] / [<code>sortByDesc</code>][GroupBy.sortByDesc] — sorts the **order of rows within each group**
     *   by one or more column values;
     * * [<code>updateGroups</code>][GroupBy.updateGroups] — transforms each group into a new one;
     * * [<code>filter</code>][GroupBy.filter] — filters out keys and corresponding groups that
     *   do not satisfy the given key-group predicate;
     * * [<code>add</code>][GroupBy.add] — adds a new column to each group.
     *
     * Each method returns a new [<code>GroupBy</code>][GroupBy] with updated group order or modified group content.
     *
     * Check out [<code>`GroupBy grammar`</code>][Grammar].
     *
     * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
     */
    typealias Transformation = Nothing

    /**
     * ### [<code>GroupBy</code>][GroupBy] reducing
     *
     * Each [<code>GroupBy</code>][GroupBy] group can be collapsed into a single row and then concatenated
     * into a new [<code>DataFrame</code>][DataFrame] composed of these rows.
     *
     * Reducing is a specific case of [<code>aggregation</code>][Aggregation].
     *
     * First, choose a [<code>GroupBy</code>][GroupBy] reducing method:
     * * [<code>first</code>][GroupBy.first], [<code>last</code>][GroupBy.last] — take the first or last row
     *   (optionally, the first or last one that satisfies a predicate) of each group;
     * * [<code>minBy</code>][GroupBy.minBy] / [<code>maxBy</code>][GroupBy.maxBy] — take the row with the minimum or maximum value
     *   of the given [<code>RowExpression</code>][RowExpression] calculated on rows within each group;
     * * [<code>medianBy</code>][GroupBy.medianBy] / [<code>percentileBy</code>][GroupBy.percentileBy] — take the row at the position closest
     *   to the estimated median/percentile index of the [<code>RowExpression</code>][RowExpression]'s results calculated on rows within each group.
     *
     * These functions return a [<code>ReducedGroupBy</code>][ReducedGroupBy], which can then be transformed into a new [<code>DataFrame</code>][DataFrame]
     * containing the reduced rows (either original or transformed) using one of the following methods:
     * * [<code>concat</code>][ReducedGroupBy.concat] — simply concatenates all reduced rows;
     * * [<code>values</code>][ReducedGroupBy.values] — creates a [<code>DataFrame</code>][DataFrame] containing the values
     *   from the reduced rows in the selected columns.
     * * [<code>into</code>][ReducedGroupBy.into] — creates a new column with values computed with [<code>RowExpression</code>][RowExpression] on each row,
     *   or a new [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *   containing each group reduced to a single row;
     *
     * Each method returns a new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns,
     * containing all unique grouping key values (or value combinations for multiple keys)
     * along with their corresponding reduced rows.
     *
     * Check out [<code>`GroupBy grammar`</code>][Grammar].
     *
     * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
     */
    typealias Reducing = Nothing

    /**
     * ### [<code>GroupBy</code>][GroupBy] aggregation
     *
     * Each [<code>GroupBy</code>][GroupBy] can be directly transformed into a new [<code>DataFrame</code>][DataFrame] by applying one or more
     * aggregation operations to its groups.
     *
     * Aggregation is a generalization of [<code>reducing</code>][Reducing].
     *
     * The following aggregation methods are available:
     * * [<code>concat</code>][GroupBy.concat] — concatenates all rows from all groups into a single [<code>DataFrame</code>][DataFrame],
     *   without preserving grouping keys;
     * * [<code>toDataFrame</code>][GroupBy.toDataFrame] — returns this [<code>GroupBy</code>][GroupBy] as [<code>DataFrame</code>][DataFrame] with the grouping keys and
     *  corresponding groups in [<code>FrameColumn</code>][FrameColumn].
     * * [<code>concatWithKeys</code>][GroupBy.concatWithKeys] — a variant of [<code>concat</code>][GroupBy.concat] that also includes
     *   grouping keys that were not present in the original [<code>DataFrame</code>][DataFrame];
     * * [<code>into</code>][GroupBy.into] — creates a new column containing a list of values computed with a [<code>RowExpression</code>][RowExpression]
     *   for each group, or a new [<code>frame column</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]
     *   containing the groups themselves;
     * * [<code>values</code>][Grouped.values] — creates a [<code>DataFrame</code>][DataFrame] containing values collected into a single [<code>List</code>][List]
     *   from all rows of each group for the selected columns.
     * * [<code>count</code>][Grouped.count] — creates a [<code>DataFrame</code>][DataFrame] containing the grouping key columns and an additional column
     *   with the number of rows in each corresponding group;
     * * [<code>countDistinct</code>][Grouped.countDistinct] — creates a [<code>DataFrame</code>][DataFrame] containing the grouping key columns
     *   and an additional column with the number of distinct rows in each corresponding group;
     * * [<code>aggregate</code>][Grouped.aggregate] — performs a set of custom aggregations using [<code>AggregateDsl</code>][AggregateDsl],
     *   allowing you to compute one or more derived values per group;
     * * [<code>Various aggregation statistics</code>][AggregationStatistics] — predefined shortcuts
     *   for common statistical aggregations such as [<code>sum</code>][Grouped.sum], [<code>mean</code>][Grouped.mean],
     *   [<code>median</code>][Grouped.median], and others.
     *
     * Each of these methods returns a new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns
     * (except for [<code>concat</code>][GroupBy.concat]) along with the columns of values aggregated
     * from the corresponding groups.
     *
     * Check out [<code>`GroupBy grammar`</code>][Grammar].
     *
     * For more information: [See "`GroupBy` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation)
     */
    typealias Aggregation = Nothing

    /**
     * ### [<code>GroupBy</code>][GroupBy] pivoting
     *
     * [<code>GroupBy</code>][GroupBy] can be pivoted with [<code>pivot</code>][GroupBy.pivot] method. It will produce a [<code>PivotGroupBy</code>][PivotGroupBy].
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
    typealias Pivoting = Nothing
}

/**
 * Groups the rows of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on the values in one or more specified [<code>key columns</code>][cols].
 * Each unique value in a key column — or a unique tuple of values for multiple columns —
 * defines the group consisting of all rows where the column(s) contain that value combination.
 *
 * Returns a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] — a dataframe-like structure that contains all unique combinations of key values
 * along with the corresponding groups of rows (each represented as a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]) as rows.
 *
 * A [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] can then be:
 * * [<code>transformed</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Transformation] into a new [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy];
 * * [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], where each group is transformed into one or more rows of derived values;
 * * [<code>pivoted</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Pivoting] into a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Grouping keys can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.GroupBySelectingOptions].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * Don't confuse this with [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group], which groups column into
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * See also [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] that groups rows of [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] vertically.
 * ### This `groupBy` Overload
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
 * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * @param [moveToTop] Specifies whether nested grouping columns should be moved to the top level
 * or kept inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * Defaults to `true`.
 * @param [cols] The [<code>Columns Selector</code>][ColumnsSelector] that defines which columns are used
 * as keys for grouping.
 * @return A new [<code>GroupBy</code>][GroupBy] containing the unique combinations of values from the provided [<code>key columns</code>][cols],
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
 * Groups the rows of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on the values in one or more specified [<code>key columns</code>][cols].
 * Each unique value in a key column — or a unique tuple of values for multiple columns —
 * defines the group consisting of all rows where the column(s) contain that value combination.
 *
 * Returns a [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] — a dataframe-like structure that contains all unique combinations of key values
 * along with the corresponding groups of rows (each represented as a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]) as rows.
 *
 * A [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] can then be:
 * * [<code>transformed</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Transformation] into a new [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy];
 * * [<code>reduced</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Reducing] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], where each group is collapsed into a single representative row;
 * * [<code>aggregated</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Aggregation] into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], where each group is transformed into one or more rows of derived values;
 * * [<code>pivoted</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Pivoting] into a [<code>PivotGroupBy</code>][org.jetbrains.kotlinx.dataframe.api.PivotGroupBy] structure, which combines [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot] and [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy] operations
 *   and then reduced or aggregated into a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Grouping keys can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.GroupByDocs.GroupBySelectingOptions].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * Don't confuse this with [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group], which groups column into
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * See also [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.DataFrame.pivot] that groups rows of [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] vertically.
 * ### This `groupBy` Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`("length", "age")`
 *
 *
 *
 *
 * @param [cols] The [<code>Column names</code>][String] that defines which columns are used
 * as keys for grouping.
 * @return A new [<code>GroupBy</code>][GroupBy] containing the unique combinations of values from the provided [<code>key columns</code>][cols],
 * together with their corresponding groups of rows.
 */
@Refine
@StringApiInterpretable(interpreter = "DataFrameGroupBy", stringArgument = "cols", targetArgument = "cols")
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupBy<T, T> = groupBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.groupBy(vararg cols: AnyColumnReference, moveToTop: Boolean = true): GroupBy<T, T> =
    groupBy(moveToTop) { cols.toColumnSet() }

// endregion

// region Pivot

/**
 * Groups the rows of this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups
 * based on the values in one or more specified [<code>key columns</code>][columns].
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
 * Grouping keys can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `groupBy` Overload
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
 * <code>`pivot`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`pivot`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`pivot`</code>`.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 * @param moveToTop Specifies whether nested grouping columns should be moved to the top level
 * or kept inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 * Defaults to `true`.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] that defines which columns are used
 * as keys for grouping.
 * @return A new [<code>PivotGroupBy</code>][PivotGroupBy] that preserves the original [<code>pivot</code>][pivot] key columns
 * and uses the provided columns as [<code>groupBy</code>][groupBy] keys.
 */
public fun <T> Pivot<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
    (this as PivotImpl<T>).toGroupedPivot(moveToTop, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: AnyColumnReference): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

/**
 * Groups the rows of this [<code>Pivot</code>][org.jetbrains.kotlinx.dataframe.api.Pivot] groups
 * based on the values in one or more specified [<code>key columns</code>][columns].
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
 * Grouping keys can also be created inline
 * (i.g. by creating a new column using [<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr] or simply renaming the old one
 * using [<code>named</code>][org.jetbrains.kotlinx.dataframe.api.named]):
 * ```kotlin
 * // Create a new column "newName" based on existing "oldName" values
 * // and use it as a grouping key:
 * df.groupBy { expr("newName") { oldName.drop(5) } }
 * ```
 * ### This `groupBy` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * `df.`[<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy]`("length", "age")`
 *
 *
 * @param [columns] The [<code>Column names</code>][String] that defines which columns are used
 * as keys for grouping.
 * @return A new [<code>PivotGroupBy</code>][PivotGroupBy] that preserves the original [<code>pivot</code>][pivot] key columns
 * and uses the provided columns as [<code>groupBy</code>][groupBy] keys.
 */
public fun <T> Pivot<T>.groupBy(vararg columns: String): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: KProperty<*>): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

/**
 * Groups the rows of this [<code>Pivot</code>][Pivot] into a [<code>PivotGroupBy</code>][PivotGroupBy]
 * based on the values of all columns except the pivot key columns.
 * For example, if a [<code>DataFrame</code>][DataFrame] has columns `"a"`, `"b"`, `"c"`, `"d"` and is pivoted by
 * `"a"` and `"c"`, then this [<code>Pivot</code>][Pivot] will be grouped by the remaining columns `"b"` and `"d"`.
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
 * @return A new [<code>PivotGroupBy</code>][PivotGroupBy] that preserves the original [<code>pivot</code>][pivot] key columns
 * and uses the remaining columns as [<code>groupBy</code>][groupBy] keys.
 */
public fun <T> Pivot<T>.groupByOther(): PivotGroupBy<T> {
    val impl = this as PivotImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { allExcept(pivotColumns) }
}

// endregion

/**
 * A specialized lambda that provides a [<code>GroupedDataRow</code>][GroupedDataRow] both as the receiver and as the argument (`this` and `it`)
 * and produces a result of type [<code>R</code>][R].
 */
public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

/**
 * A specialized lambda that provides a [<code>GroupedDataRow</code>][GroupedDataRow] both as the receiver and as the argument (`this` and `it`)
 * and returns a [<code>Boolean</code>][Boolean] value used for filtering.
 */
public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

/**
 * A specialized form of [<code>DataRow</code>][DataRow] representing a single row of a [<code>GroupBy</code>][GroupBy].
 * Each instance contains the key values and a reference to the corresponding [<code>group</code>][group].
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 */
public interface GroupedDataRow<out T, out G> : DataRow<T> {

    /**
     * The [<code>DataFrame</code>][DataFrame] representing the group corresponding to the current key values.
     *
     * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
     */
    public fun group(): DataFrame<G>
}

/**
 * The [<code>DataFrame</code>][DataFrame] representing the group corresponding to the current key values.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 */
public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G>
    get() = group()

/**
 * An alternative representation of a [<code>GroupBy.Entry</code>][GroupBy.Entry], holding a key–group pair.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * @property key The key represented as a [<code>DataRow</code>][DataRow].
 * @property group The [<code>DataFrame</code>][DataFrame] containing the rows belonging to this group.
 */
public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

/**
 * A dataframe-like structure that contains all unique combinations of key-values
 * along with the corresponding groups of rows (each represented as a [<code>DataFrame</code>][DataFrame]).
 *
 * Consists of two main parts:
 * * [<code>groups</code>][groups] — represents the groups as a [<code>FrameColumn</code>][FrameColumn], where each cell contains a [<code>DataFrame</code>][DataFrame]
 *   with the rows that belong to a specific group.
 * * [<code>keys</code>][keys] — represents the grouping keys as a [<code>DataFrame</code>][DataFrame], containing one column for each key column.
 *   Each row in [<code>keys</code>][keys] corresponds to a group in [<code>groups</code>][groups].
 *
 * Together, the rows of [<code>keys</code>][keys] and [<code>groups</code>][groups] define one-to-one **key–group pairs**.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 *
 * @param G The schema of the groups (same as the schema of the original [<code>DataFrame</code>][DataFrame]).
 * @param T The schema of the grouping keys.
 */
public interface GroupBy<out T, out G> : Grouped<G> {

    /**
     * A [<code>FrameColumn</code>][FrameColumn] representing all groups of rows.
     * Each cell contains a [<code>DataFrame</code>][DataFrame] with the subset of rows that share the same key values.
     *
     * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
     */
    public val groups: FrameColumn<G>

    /**
     * A [<code>DataFrame</code>][DataFrame] representing the grouping keys.
     * Each column corresponds to a key column, and each row corresponds to a unique group.
     *
     * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
     */
    public val keys: DataFrame<T>

    /**
     * Creates a new [<code>GroupBy</code>][GroupBy] by transforming each group’s [<code>DataFrame</code>][DataFrame]
     * using the provided [<code>transform</code>][transform] function.
     *
     * Check out [<code>`GroupBy grammar`</code>][Grammar].
     *
     * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
     *
     * __NOTE:__ This operation removes key-column status from each column in the group.
     * In other words, each column in the group is treated as a new column,
     * and not omitted when [<code>`.values()`</code>][Grouped.values] or other aggregations are called.
     *
     * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
     *
     * @param [transform] A lambda that takes each group as a [<code>DataFrame</code>][DataFrame]
     * (available both as a receiver and as a parameter) and returns a transformed [<code>DataFrame</code>][DataFrame].
     * @return A new [<code>GroupBy</code>][GroupBy] instance containing the transformed groups.
     */
    public fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>): GroupBy<T, R>

    /**
     * Filters the rows of this [<code>GroupBy</code>][GroupBy] — that is, the key–group pairs — based on the specified [<code>predicate</code>][predicate].
     *
     * The [<code>predicate</code>][predicate] is a [<code>GroupedRowFilter</code>][GroupedRowFilter], which behaves similarly to a [<code>RowFilter</code>][RowFilter] used in [<code>DataFrame.filter</code>][DataFrame.filter],
     * but also provides access to the [<code>group</code>][GroupedDataRow.group] in the current row.
     *
     * Check out [<code>`GroupBy grammar`</code>][Grammar].
     *
     * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
     *
     * ### Example
     * ```kotlin
     * // Keep only key–group pairs where the "category" key equals "Engineer"
     * // or where the group contains at least 5 rows
     * gb.filter { category == "Engineer" || group.rowsCount() >= 5 }
     * ```
     *
     * __NOTE:__ This operation removes key-column status from each column in the group.
     * In other words, each column in the group is treated as a new column,
     * and not omitted when [<code>`.values()`</code>][Grouped.values] or other aggregations are called.
     *
     * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
     *
     * @param [predicate] A [<code>GroupedRowFilter</code>][GroupedRowFilter] used to determine which groups should be retained.
     * @return A new [<code>GroupBy</code>][GroupBy] containing only the key–group pairs that satisfy the [<code>predicate</code>][predicate].
     */
    public fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G>

    /**
     * Converts this [<code>GroupBy</code>][GroupBy] into a [<code>DataFrame</code>][DataFrame].
     *
     * Each row of the resulting [<code>DataFrame</code>][DataFrame] represents a unique key–group pair:
     * a row from [<code>keys</code>][keys] and its corresponding group of rows (as [<code>DataFrame</code>][DataFrame]).
     *
     * If [<code>groupedColumnName</code>][groupedColumnName] is provided, the groups will be stored
     * in a [<code>FrameColumn</code>][FrameColumn] with that name; otherwise, a default name "group" is used.
     *
     * For more information: [See "`GroupBy` Aggregation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#aggregation)
     *
     * @param groupedColumnName The name of the column in which to store grouped data;
     * if `null`, a default name will be used.
     * @return A new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns together
     * with a [<code>FrameColumn</code>][FrameColumn] containing the corresponding groups.
     */
    @Refine
    @Interpretable("GroupByToDataFrame")
    public fun toDataFrame(groupedColumnName: String? = null): DataFrame<T>

    /**
     * Represents a single key–group pair in a [<code>GroupBy</code>][GroupBy].
     *
     * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
     *
     * @property key The key of the group, represented as a [<code>DataRow</code>][DataRow].
     * @property group The [<code>DataFrame</code>][DataFrame] containing all rows that belong to the group.
     */
    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

/**
 * Represents a dataframe-like structure with grouped values, offering aggregation capabilities.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html)
 */
public interface Grouped<out T> : Aggregatable<T>

/**
 * An intermediate class used in [<code>`GroupBy` reducing</code>][GroupByDocs.Reducing] operations.
 *
 * Serves as a transitional step between performing a reduction on groups
 * and specifying how the resulting reduced rows should be represented
 * in a new [<code>DataFrame</code>][DataFrame].
 *
 * Available transformation methods:
 * * [<code>concat</code>][ReducedGroupBy.concat] — concatenates all reduced rows into a single [<code>DataFrame</code>][DataFrame];
 * * [<code>values</code>][ReducedGroupBy.values] — creates a [<code>DataFrame</code>][DataFrame] with new rows by transforming each reduced row
 *   using [<code>ColumnsForAggregateSelectionDsl</code>][ColumnsForAggregateSelectionDsl];
 * * [<code>into</code>][ReducedGroupBy.into] — creates a new column with values computed using a [<code>RowExpression</code>][RowExpression] for each row,
 *   or a new [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
 *   containing each group reduced to a single row.
 *
 * Each method returns a new [<code>DataFrame</code>][DataFrame] that includes the grouping key columns,
 * containing all unique grouping key values (or value combinations for multiple keys)
 * together with their corresponding reduced rows.
 *
 * See also: [<code>`GroupBy grammar`</code>][Grammar].
 *
 * For more information: [See "`GroupBy` Reducing" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#reducing)
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

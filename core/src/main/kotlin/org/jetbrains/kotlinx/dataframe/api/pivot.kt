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
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
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
 * as columns (or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s) with a single row
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
     * ### Convert [Pivot] into [PivotGroupBy] and then reduce / aggregate
     *
     * {@include [Indent]}
     * [Pivot][Pivot]`.`[**`groupBy`**][Pivot.groupBy]**`  {  `**`indexColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`groupByOther`**][Pivot.groupByOther]**`() `**
     *
     * {@include [Indent]}
     * `    \[ `__`.`__[**`default`**][PivotGroupBy.default]**`(`**`defaultValue`**`) `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`minBy`**][PivotGroupBy.minBy]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`maxBy`**][PivotGroupBy.maxBy]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`first`**][PivotGroupBy.first]`  \[ `**` {  `**`rowCondition: `[`RowFilter`][RowFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`last`**][PivotGroupBy.last]`  \[ `**`{  `**`rowCondition: `[`RowFilter`][RowFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * `| `__`.`__[**`medianBy`**][PivotGroupBy.medianBy]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`percentileBy`**][PivotGroupBy.percentileBy]**`(`**`percentile: `[`Double`][Double]**`)  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`with`**][PivotGroupBy.with]**`  {  `**`rowExpression: `[`RowExpression`][RowExpression]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`values`**][PivotGroupBy.values]**`  {  `**`valueColumns: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`count`**][PivotGroupBy.count]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`matches`**][PivotGroupBy.matches]**`  {  `**`predicate: `[`RowFilter`][RowFilter]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`frames`**][PivotGroupBy.frames]**`() `**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`aggregate`**][PivotGroupBy.aggregate]**`  {  `**`aggregations: `[`AggregateDsl`][AggregateDsl]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[<aggregation_statistic>][PivotDocs.AggregationStatistics]
     */
    interface Grammar

    /**
     * ### [Pivot] common description
     *
     * A pivot reorganizes data **horizontally**:
     * * pivot key values → **new columns**;
     * * index columns (from [groupBy][Pivot.groupBy] / [groupByOther][Pivot.groupByOther]) → **rows**;
     * * each cell is produced either by a **reducer** (single selected row → single value)
     *   or an **aggregator** (all rows in the cell → one or more values).
     *
     * If a [default][PivotGroupBy.default] value is set, missing cells are filled with it.
     */
    interface CommonDescription

    /**
     * ### [Pivot] reducing
     *
     * Produces a [DataFrame] with **exactly one value per pivot cell**.
     *
     * Available reducers (both for [Pivot] and [PivotGroupBy]):
     * * [minBy] / [maxBy] — select the row with the minimum/maximum value of a column;
     * * [first] / [last] — take the first/last row (optionally with a [RowFilter]);
     * * [medianBy] / [percentileBy] — select the row at median / a given percentile;
     * * [with] — compute the cell value via a [RowExpression] from the selected row;
     * * [values] — copy one or more columns from the selected row into cells.
     */
    interface Reducing

    /**
     * ### [Pivot] aggregation
     *
     * Produces a [DataFrame] with **one or more values per pivot cell** by combining **all rows** in the cell.
     *
     * Available aggregators (both for [Pivot] and [PivotGroupBy]):
     * * [count] — number of rows in the cell;
     * * [matches] — number of rows satisfying a predicate;
     * * [frames] — collect rows as a [frame column][org.jetbrains.kotlinx.dataframe.columns.FrameColumn];
     * * [with] — compute a value using a [RowExpression] over all rows in the cell;
     * * [values] — project one or more columns as aggregated cell values;
     * * [aggregate] — custom multi-aggregation via [AggregateDsl];
     * * [Various aggregation statistics][PivotDocs.AggregationStatistics] — predefined shortcuts
     *   such as sum/mean/median/std/percentile, etc.
     */
    interface Aggregation


    interface Grouping

    /**
     * ### [Pivot] aggregation statistics
     * * [count][Pivot.count]
     * * [max][Pivot.max]/[maxOf][Pivot.maxOf]/[maxFor][Pivot.maxFor]
     * * [min][Pivot.min]/[minOf][Pivot.minOf]/[minFor][Pivot.minFor]
     * * [sum][Pivot.sum]/[sumOf][Pivot.sumOf]/[sumFor][Pivot.sumFor]
     * * [mean][Pivot.mean]/[meanOf][Pivot.meanOf]/[meanFor][Pivot.meanFor]
     * * [std][Pivot.std]/[stdOf][Pivot.stdOf]/[stdFor][Pivot.stdFor]
     * * [median][Pivot.median]/[medianOf][Pivot.medianOf]/[medianFor][Pivot.medianFor]
     * * [percentile][Pivot.percentile]/[percentileOf][Pivot.percentileOf]/[percentileFor][Pivot.percentileFor]
     */
    interface AggregationStatistics

    /**
     * {@comment Version of SelectingColumns with correctly filled examples for pivot keys and index columns}
     * @include [SelectingColumns] {@include [SetPivotOperationArg]}
     */
    interface PivotSelectingOptions
}


public interface PivotDsl<out T> : ColumnsSelectionDsl<T> {

    public infix fun <C> ColumnsResolver<C>.then(other: ColumnsResolver<C>): ColumnSet<C> =
        PivotChainColumnSet(this, other)

    public infix fun <C> String.then(other: ColumnsResolver<C>): ColumnSet<C> = toColumnOf<C>() then other

    public infix fun <C> ColumnsResolver<C>.then(other: String): ColumnSet<C> = this then other.toColumnOf()

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

public fun <T> DataFrame<T>.pivot(inward: Boolean? = null, columns: PivotColumnsSelector<T, *>): Pivot<T> =
    PivotImpl(this, columns, inward)

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

public fun <T> DataFrame<T>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().matches()

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

public fun <T> DataFrame<T>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<T, *>): DataFrame<T> =
    pivot(inward, columns).groupByOther().count()

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

public fun <G> GroupBy<*, G>.pivot(inward: Boolean = true, columns: ColumnsSelector<G, *>): PivotGroupBy<G> =
    PivotGroupByImpl(this, columns, inward)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivot(vararg columns: AnyColumnReference, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

public fun <G> GroupBy<*, G>.pivot(vararg columns: String, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <G> GroupBy<*, G>.pivot(vararg columns: KProperty<*>, inward: Boolean = true): PivotGroupBy<G> =
    pivot(inward) { columns.toColumnSet() }

// endregion

// region pivotMatches

public fun <G> GroupBy<*, G>.pivotMatches(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).matches()

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

public fun <G> GroupBy<*, G>.pivotCounts(inward: Boolean = true, columns: ColumnsSelector<G, *>): DataFrame<G> =
    pivot(inward, columns).count()

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

public fun <T> AggregateGroupedDsl<T>.pivot(
    inward: Boolean = true,
    columns: PivotColumnsSelector<T, *>,
): PivotGroupBy<T> = PivotInAggregateImpl(this, columns, inward)

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

public fun <T> AggregateGroupedDsl<T>.pivotMatches(
    inward: Boolean = true,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).matches()

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

public fun <T> AggregateGroupedDsl<T>.pivotCounts(
    inward: Boolean = true,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = pivot(inward, columns).matches()

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

public interface Pivot<T> : Aggregatable<T>

public typealias PivotColumnsSelector<T, C> = Selector<PivotDsl<T>, ColumnsResolver<C>>

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

internal interface PivotGroupByDocs {

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
     * or [aggregated][PivotGroupByDocs.Aggregation].
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

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.BuildConfig
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.api.GroupByEntryImpl
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 *
 * @param cols key columns; Column for grouping can be created inplace
 *
 * `df.groupBy { expr("columnName") { "someColumn"<Int>() + 15 } }`
 *
 * is equivalent to
 *
 * `df.add("columnName") { "someColumn"<Int>() + 15 }.groupBy("columnName")`
 */
@Refine
@Interpretable("DataFrameGroupBy")
public fun <T> DataFrame<T>.groupBy(moveToTop: Boolean = true, cols: ColumnsSelector<T, *>): GroupBy<T, T> =
    groupByImpl(moveToTop, cols)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupBy<T, T> = groupBy { cols.toColumnSet() }

public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupBy<T, T> = groupBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.groupBy(vararg cols: AnyColumnReference, moveToTop: Boolean = true): GroupBy<T, T> =
    groupBy(moveToTop) { cols.toColumnSet() }

// endregion

// region Pivot

public fun <T> Pivot<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> =
    (this as PivotImpl<T>).toGroupedPivot(moveToTop, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: AnyColumnReference): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

public fun <T> Pivot<T>.groupBy(vararg columns: String): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> Pivot<T>.groupBy(vararg columns: KProperty<*>): PivotGroupBy<T> = groupBy { columns.toColumnSet() }

public fun <T> Pivot<T>.groupByOther(): PivotGroupBy<T> {
    val impl = this as PivotImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { allExcept(pivotColumns) }
}

// endregion

@Deprecated("Replaced by GroupByEntrySelector")
public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

@Deprecated("Replaced by GroupByEntryFilter")
public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

@Deprecated("Replaced by GroupByEntry")
public interface GroupedDataRow<out T, out G> : DataRow<T> {

    public fun group(): DataFrame<G>
}

public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G> get() = group()

/**
 * Represents a single combination of keys+group in a [GroupBy] instance.
 *
 * `this` is a [DataRow] representing the keys of the current group, while the [group()][group]
 * function points to the group that corresponds to the keys of this entry.
 *
 * For example:
 * ```kotlin
 * df.groupBy { name and age }.forEachEntry { // this|it: GroupByEntry<T, G> ->
 *   println("There are \${group().rowsCount()} instances of \$name")
 * }
 * ```
 */
public interface GroupByEntry<out T, out G> : DataRow<T> {

    /** Returns the [DataFrame] representing the group that corresponds to the keys of this entry. */
    public fun group(): DataFrame<G>

    // TODO?
    public fun keys(): Map<String, Any?> = this.toMap()
}

public typealias GroupByEntrySelector<T, G, R> = GroupByEntry<T, G>.(GroupByEntry<T, G>) -> R
public typealias GroupByEntryFilter<T, G> = GroupByEntrySelector<T, G, Boolean>

@Deprecated("Replaced by GroupByEntry")
public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

public interface GroupBy<out T, out G> : Grouped<G> {

    public val groups: FrameColumn<G>

    public val keys: DataFrame<T>

    public fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>): GroupBy<T, R>

    @Deprecated("Replaced by filterEntries")
    public fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G>

    @Refine
    @Interpretable("GroupByToDataFrame")
    public fun toDataFrame(groupedColumnName: String? = null): DataFrame<T>

    @Deprecated("")
    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

public interface Grouped<out T> : Aggregatable<T>

public class ReducedGroupBy<T, G>(
    @PublishedApi internal val groupBy: GroupBy<T, G>,
    @PublishedApi internal val reducer: Selector<DataFrame<G>, DataRow<G>?>,
) {
    override fun toString(): String = "ReducedGroupBy(groupBy=$groupBy, reducer=$reducer)"
}

@PublishedApi
internal fun <T, G> GroupBy<T, G>.reduce(reducer: Selector<DataFrame<G>, DataRow<G>?>): ReducedGroupBy<T, G> =
    ReducedGroupBy(this, reducer)

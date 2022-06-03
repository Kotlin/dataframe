package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.aggregation.Aggregatable
import org.jetbrains.kotlinx.dataframe.aggregation.AggregateGroupedBody
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.aggregation.PivotImpl
import org.jetbrains.kotlinx.dataframe.impl.api.getPivotColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.groupBy(moveToTop: Boolean = true, cols: ColumnsSelector<T, *>): GroupBy<T, T> = groupByImpl(moveToTop, cols)
public fun <T> DataFrame<T>.groupBy(cols: Iterable<Column>): GroupBy<T, T> = groupBy { cols.toColumnSet() }
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupBy<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupBy<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: Column, moveToTop: Boolean = true): GroupBy<T, T> = groupBy(moveToTop) { cols.toColumns() }

// endregion

// region Pivot

public fun <T> Pivot<T>.groupBy(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>): PivotGroupBy<T> = (this as PivotImpl<T>).toGroupedPivot(moveToTop, columns)
public fun <T> Pivot<T>.groupBy(vararg columns: Column): PivotGroupBy<T> = groupBy { columns.toColumns() }
public fun <T> Pivot<T>.groupBy(vararg columns: String): PivotGroupBy<T> = groupBy { columns.toColumns() }
public fun <T> Pivot<T>.groupBy(vararg columns: KProperty<*>): PivotGroupBy<T> = groupBy { columns.toColumns() }

public fun <T> Pivot<T>.groupByOther(): PivotGroupBy<T> {
    val impl = this as PivotImpl<T>
    val pivotColumns = df.getPivotColumnPaths(columns).toColumnSet()
    return impl.toGroupedPivot(moveToTop = false) { except(pivotColumns) }
}

// endregion

public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

public interface GroupedDataRow<out T, out G> : DataRow<T> {

    public fun group(): DataFrame<G>
}

public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G> get() = group()

public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

public interface GroupBy<out T, out G> : Grouped<G> {

    public val groups: FrameColumn<G>

    public val keys: DataFrame<T>

    public fun toDataFrame(groupedColumnName: String? = null): DataFrame<T>

    public fun <R> updateGroups(transform: Selector<DataFrame<G>, DataFrame<R>>): GroupBy<T, R>

    public fun filter(predicate: GroupedRowFilter<T, G>): GroupBy<T, G>

    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

public interface Grouped<out T> : Aggregatable<T> {

    public fun <R> aggregate(body: AggregateGroupedBody<T, R>): DataFrame<T>
}

public data class ReducedGroupBy<T, G>(
    @PublishedApi internal val groupBy: GroupBy<T, G>,
    @PublishedApi internal val reducer: Selector<DataFrame<G>, DataRow<G>?>
)

internal fun <T, G> GroupBy<T, G>.reduce(reducer: Selector<DataFrame<G>, DataRow<G>?>) = ReducedGroupBy(this, reducer)

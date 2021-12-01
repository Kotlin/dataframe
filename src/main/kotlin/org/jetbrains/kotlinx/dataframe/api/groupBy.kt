package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.groupBy(moveToTop: Boolean = true, cols: ColumnsSelector<T, *>): GroupBy<T, T> = groupByImpl(moveToTop, cols)
public fun <T> DataFrame<T>.groupBy(cols: Iterable<Column>): GroupBy<T, T> = groupBy { cols.toColumnSet() }
public fun <T> DataFrame<T>.groupBy(vararg cols: KProperty<*>): GroupBy<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: String): GroupBy<T, T> = groupBy { cols.toColumns() }
public fun <T> DataFrame<T>.groupBy(vararg cols: Column, moveToTop: Boolean = true): GroupBy<T, T> = groupBy(moveToTop) { cols.toColumns() }

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

    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>?)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

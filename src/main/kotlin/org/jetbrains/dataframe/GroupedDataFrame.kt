package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.ColumnReference
import org.jetbrains.dataframe.columns.FrameColumn
import org.jetbrains.dataframe.impl.GroupedDataFrameImpl
import org.jetbrains.dataframe.impl.columns.asTable
import org.jetbrains.dataframe.impl.columns.isTable
import org.jetbrains.dataframe.impl.columns.typed

public typealias GroupKey = List<Any?>

public interface GroupedDataRow<out T, out G> : DataRow<T> {

    public fun group(): DataFrame<G>

    public fun groupOrNull(): DataFrame<G>?
}

public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G> get() = group()

public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

public interface GroupedDataFrame<out T, out G> : GroupByAggregations<G> {

    public val groups: FrameColumn<G>

    public val keys: DataFrame<T>

    public fun plain(): DataFrame<T>

    public fun ungroup(): DataFrame<G> = groups.union().typed()

    public operator fun get(vararg values: Any?): DataFrame<T> = get(values.toList())
    public operator fun get(key: GroupKey): DataFrame<T>

    public fun <R> mapGroups(transform: Selector<DataFrame<G>?, DataFrame<R>?>): GroupedDataFrame<T, R>

    public fun filter(predicate: GroupedRowFilter<T, G>): GroupedDataFrame<T, G>

    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>?)

    public companion object
}

internal fun <T, G> DataFrame<T>.toGrouped(groupedColumnName: String): GroupedDataFrame<T, G> =
    GroupedDataFrameImpl(this, this[groupedColumnName] as FrameColumn<G>) { none() }

internal fun <T, G> DataFrame<T>.toGrouped(groupedColumn: ColumnReference<DataFrame<G>?>): GroupedDataFrame<T, G> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumn.name()).typed()) { none() }

internal fun <T> DataFrame<T>.toGrouped(): GroupedDataFrame<T, T> {
    val groupCol = columns().single { it.isTable() }.asTable() as FrameColumn<T>
    return toGrouped { groupCol }
}

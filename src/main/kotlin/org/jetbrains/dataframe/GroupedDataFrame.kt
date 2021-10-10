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

public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

public interface GroupedDataFrame<out T, out G> : Grouped<G> {

    public val groups: FrameColumn<G>

    public val keys: DataFrame<T>

    public fun asDataFrame(groupedColumnName: String? = null): DataFrame<T>

    public fun union(): DataFrame<G> = groups.union().typed()

    public operator fun get(vararg values: Any?): DataFrame<T> = get(values.toList())

    public operator fun get(key: GroupKey): DataFrame<T>

    public fun <R> mapGroups(transform: Selector<DataFrame<G>?, DataFrame<R>?>): GroupedDataFrame<T, R>

    public fun filter(predicate: GroupedRowFilter<T, G>): GroupedDataFrame<T, G>

    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>?)

    public companion object
}

internal fun <T> DataFrame<T>.asGrouped(groupedColumnName: String): GroupedDataFrame<T, T> =
    GroupedDataFrameImpl(this, this[groupedColumnName] as FrameColumn<T>) { none() }

internal fun <T, G> DataFrame<T>.asGrouped(groupedColumn: ColumnReference<DataFrame<G>?>): GroupedDataFrame<T, G> =
    GroupedDataFrameImpl(this, frameColumn(groupedColumn.name()).typed()) { none() }

internal fun <T> DataFrame<T>.asGrouped(): GroupedDataFrame<T, T> {
    val groupCol = columns().single { it.isTable() }.asTable() as FrameColumn<T>
    return asGrouped { groupCol }
}

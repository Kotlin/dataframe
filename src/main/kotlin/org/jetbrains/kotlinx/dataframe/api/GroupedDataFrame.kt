package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.typed

public typealias GroupKey = List<Any?>

public typealias GroupedRowSelector<T, G, R> = GroupedDataRow<T, G>.(GroupedDataRow<T, G>) -> R

public typealias GroupedRowFilter<T, G> = GroupedRowSelector<T, G, Boolean>

public interface GroupedDataRow<out T, out G> : DataRow<T> {

    public fun group(): DataFrame<G>

    public fun groupOrNull(): DataFrame<G>?
}

public val <T, G> GroupedDataRow<T, G>.group: DataFrame<G> get() = group()

public data class GroupWithKey<T, G>(val key: DataRow<T>, val group: DataFrame<G>)

public interface GroupedDataFrame<out T, out G> : Grouped<G> {

    public val groups: FrameColumn<G>

    public val keys: DataFrame<T>

    public fun toDataFrame(groupedColumnName: String? = null): DataFrame<T>

    public fun union(): DataFrame<G> = groups.union().typed()

    public operator fun get(vararg values: Any?): DataFrame<T> = get(values.toList())

    public operator fun get(key: GroupKey): DataFrame<T>

    public fun <R> mapGroups(transform: Selector<DataFrame<G>?, DataFrame<R>?>): GroupedDataFrame<T, R>

    public fun filter(predicate: GroupedRowFilter<T, G>): GroupedDataFrame<T, G>

    public data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>?)

    public companion object {
        internal val groupedColumnAccessor = column<AnyFrame>("group")
    }
}

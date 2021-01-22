package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.FrameColumn
import org.jetbrains.dataframe.impl.GroupedDataFrameImpl

typealias GroupKey = List<Any?>

interface GroupedDataFrame<out T, out G> {

    val groups : FrameColumn<G>

    val keys : DataFrame<T>

    fun plain(): DataFrame<T>

    fun ungroup() = groups.union().typed<G>()

    operator fun get(vararg values: Any?) = get(values.toList())
    operator fun get(key: GroupKey): DataFrame<T>

    fun <R> updateGroups(transform: DataFrame<G>.() -> DataFrame<R>): GroupedDataFrame<T, R>

    data class Entry<T, G>(val key: DataRow<T>, val group: DataFrame<G>)
}

internal fun <T,G> DataFrame<T>.toGrouped(groupedColumnName: String): GroupedDataFrame<T,G> = GroupedDataFrameImpl(this, this[groupedColumnName] as FrameColumn<G>)

internal fun <T> DataFrame<T>.toGrouped(): GroupedDataFrame<T,T> {

    val groupCol = columns().single {it.isTable()}.asTable() as FrameColumn<T>
    return toGrouped {groupCol}
}

fun <T,G,R> GroupedDataFrame<T,G>.map(body: (key: DataRow<T>, group: DataFrame<G>) -> R) =
        keys.mapIndexed { index, row ->
            val group = groups[index]
            body(row, group)
        }

typealias Reducer<T, R> = DataFrame<T>.(DataFrame<T>) -> R

inline fun <T, G, reified R : Comparable<R>> GroupedDataFrame<T, G>.median(columnName: String = "median", noinline selector: RowSelector<G, R>) = aggregate { median(selector) into columnName }
inline fun <T, G, reified R : Number> GroupedDataFrame<T, G>.mean(columnName: String = "mean", noinline selector: RowSelector<G, R?>) = aggregate { mean(selector) into columnName }


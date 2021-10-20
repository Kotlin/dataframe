package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.api.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region map

public fun <T, G, R> GroupedDataFrame<T, G>.mapNotNullGroups(transform: DataFrame<G>.() -> DataFrame<R>?): GroupedDataFrame<T, R> = mapGroups { if (it == null) null else transform(it) }

public fun <T, G, R> GroupedDataFrame<T, G>.map(body: Selector<GroupWithKey<T, G>, R>): List<R> = keys.mapIndexedNotNull { index, row ->
    val group = groups[index]
    if (group == null) null
    else {
        val g = GroupWithKey(row, group)
        body(g, g)
    }
}

public fun <T, G> GroupedDataFrame<T, G>.mapToRows(body: Selector<GroupWithKey<T, G>, DataRow<G>?>): DataFrame<G> =
    map(body).concat()

public fun <T, G> GroupedDataFrame<T, G>.mapToFrames(body: Selector<GroupWithKey<T, G>, DataFrame<G>?>): FrameColumn<G> =
    map(body).toFrameColumn(groups.name)

// endregion

// region sort

public fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: String): GroupedDataFrame<T, G> = sortBy { cols.toColumns() }
public fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: ColumnReference<Comparable<*>?>): GroupedDataFrame<T, G> = sortBy { cols.toColumns() }
public fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: KProperty<Comparable<*>?>): GroupedDataFrame<T, G> = sortBy { cols.toColumns() }
public fun <T, G, C> GroupedDataFrame<T, G>.sortBy(selector: SortColumnsSelector<G, C>): GroupedDataFrame<T, G> = sortByImpl(selector)

public fun <T, G> GroupedDataFrame<T, G>.sortByDesc(vararg cols: String): GroupedDataFrame<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G> GroupedDataFrame<T, G>.sortByDesc(vararg cols: ColumnReference<Comparable<*>?>): GroupedDataFrame<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G> GroupedDataFrame<T, G>.sortByDesc(vararg cols: KProperty<Comparable<*>?>): GroupedDataFrame<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G, C> GroupedDataFrame<T, G>.sortByDesc(selector: SortColumnsSelector<G, C>): GroupedDataFrame<T, G> {
    val set = selector.toColumns()
    return sortByImpl { set.desc }
}

private fun <T, G, C> GroupedDataFrame<T, G>.createColumnFromGroupExpression(
    receiver: ColumnSelectionDsl<T>,
    default: C? = null,
    selector: DataFrameSelector<G, C>
): DataColumn<C?> {
    return receiver.exprWithActualType { row ->
        val group: DataFrame<G>? = row[groups]
        if (group == null) default
        else selector(group, group)
    }
}

public fun <T, G, C> GroupedDataFrame<T, G>.sortByGroup(
    nullsLast: Boolean = false,
    default: C? = null,
    selector: DataFrameSelector<G, C>
): GroupedDataFrame<T, G> = toDataFrame().sortBy {
    createColumnFromGroupExpression(this, default, selector).nullsLast(nullsLast)
}.asGroupedDataFrame(groups)

public fun <T, G, C> GroupedDataFrame<T, G>.sortByGroupDesc(
    nullsLast: Boolean = false,
    default: C? = null,
    selector: DataFrameSelector<G, C>
): GroupedDataFrame<T, G> = toDataFrame().sortBy {
    createColumnFromGroupExpression(this, default, selector).desc.nullsLast(nullsLast)
}.asGroupedDataFrame(groups)

public fun <T, G> GroupedDataFrame<T, G>.sortByCountAsc(): GroupedDataFrame<T, G> = sortByGroup(default = 0) { nrow() }
public fun <T, G> GroupedDataFrame<T, G>.sortByCount(): GroupedDataFrame<T, G> = sortByGroupDesc(default = 0) { nrow() }

public fun <T, G> GroupedDataFrame<T, G>.sortByKeyDesc(nullsLast: Boolean = false): GroupedDataFrame<T, G> = toDataFrame()
    .sortBy { keys.columns().toColumnSet().desc.nullsLast(nullsLast) }.asGroupedDataFrame(groups)
public fun <T, G> GroupedDataFrame<T, G>.sortByKey(nullsLast: Boolean = false): GroupedDataFrame<T, G> = toDataFrame()
    .sortBy { keys.columns().toColumnSet().nullsLast(nullsLast) }.asGroupedDataFrame(groups)

// endregion

// region forEach

public fun <T, G> GroupedDataFrame<T, G>.forEach(body: (GroupedDataFrame.Entry<T, G>) -> Unit): Unit = forEach { key, group ->
    body(
        GroupedDataFrame.Entry(key, group)
    )
}
public fun <T, G> GroupedDataFrame<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>?) -> Unit): Unit =
    keys.forEachIndexed { index, row ->
        val group = groups[index]
        body(row, group)
    }

// endregion

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty

// region map

internal fun <T, G, R> GroupBy<T, G>.map(body: Selector<GroupWithKey<T, G>, R>): List<R> = keys.rows().mapIndexedNotNull { index, row ->
    val group = groups[index]
    val g = GroupWithKey(row, group)
    body(g, g)
}

public fun <T, G> GroupBy<T, G>.mapToRows(body: Selector<GroupWithKey<T, G>, DataRow<G>?>): DataFrame<G> =
    map(body).concat()

public fun <T, G> GroupBy<T, G>.mapToFrames(body: Selector<GroupWithKey<T, G>, DataFrame<G>>): FrameColumn<G> =
    DataColumn.createFrameColumn(groups.name, map(body))

// endregion

// region sort

public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: String): GroupBy<T, G> = sortBy { cols.toColumns() }
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: ColumnReference<Comparable<*>?>): GroupBy<T, G> = sortBy { cols.toColumns() }
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> = sortBy { cols.toColumns() }
public fun <T, G, C> GroupBy<T, G>.sortBy(selector: SortColumnsSelector<G, C>): GroupBy<T, G> = sortByImpl(selector)

public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: String): GroupBy<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: ColumnReference<Comparable<*>?>): GroupBy<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> = sortByDesc { cols.toColumns() }
public fun <T, G, C> GroupBy<T, G>.sortByDesc(selector: SortColumnsSelector<G, C>): GroupBy<T, G> {
    val set = selector.toColumns()
    return sortByImpl { set.desc() }
}

private fun <T, G, C> GroupBy<T, G>.createColumnFromGroupExpression(
    receiver: ColumnsSelectionDsl<T>,
    expression: DataFrameExpression<G, C>
): DataColumn<C?> {
    return receiver.exprWithActualType { row ->
        val group = row[groups]
        expression(group, group)
    }
}

public fun <T, G, C> GroupBy<T, G>.sortByGroup(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>
): GroupBy<T, G> = toDataFrame().sortBy {
    createColumnFromGroupExpression(this, expression).nullsLast(nullsLast)
}.asGroupBy(groups)

public fun <T, G, C> GroupBy<T, G>.sortByGroupDesc(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>
): GroupBy<T, G> = toDataFrame().sortBy {
    createColumnFromGroupExpression(this, expression).desc().nullsLast(nullsLast)
}.asGroupBy(groups)

public fun <T, G> GroupBy<T, G>.sortByCountAsc(): GroupBy<T, G> = sortByGroup { nrow() }
public fun <T, G> GroupBy<T, G>.sortByCount(): GroupBy<T, G> = sortByGroupDesc { nrow() }

public fun <T, G> GroupBy<T, G>.sortByKeyDesc(nullsLast: Boolean = false): GroupBy<T, G> = toDataFrame()
    .sortBy { keys.columns().toColumnSet().desc().nullsLast(nullsLast) }.asGroupBy(groups)
public fun <T, G> GroupBy<T, G>.sortByKey(nullsLast: Boolean = false): GroupBy<T, G> = toDataFrame()
    .sortBy { keys.columns().toColumnSet().nullsLast(nullsLast) }.asGroupBy(groups)

// endregion

// region forEach

public fun <T, G> GroupBy<T, G>.forEach(body: (GroupBy.Entry<T, G>) -> Unit): Unit = forEach { key, group ->
    body(
        GroupBy.Entry(key, group)
    )
}
public fun <T, G> GroupBy<T, G>.forEach(body: (key: DataRow<T>, group: DataFrame<G>?) -> Unit): Unit =
    keys.forEachRow { row ->
        val group = groups[row.index()]
        body(row, group)
    }

// endregion

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.kind
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region remove

public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> = removeImpl(columns).df
public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(vararg columns: Column): DataFrame<T> = remove { columns.toColumns() }
public fun <T> DataFrame<T>.remove(columns: Iterable<Column>): DataFrame<T> = remove { columns.toColumnSet() }

public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)
public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(column: Column): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(columns: Iterable<Column>): DataFrame<T> = remove(columns)

// region group

public data class GroupClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public fun <T, C> DataFrame<T>.group(columns: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, columns)
public fun <T> DataFrame<T>.group(vararg columns: String): GroupClause<T, Any?> = group { columns.toColumns() }
public fun <T> DataFrame<T>.group(vararg columns: Column): GroupClause<T, Any?> = group { columns.toColumns() }
public fun <T> DataFrame<T>.group(vararg columns: KProperty<*>): GroupClause<T, Any?> = group { columns.toColumns() }

@JvmName("intoString")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String): DataFrame<T> = df.move(columns).under { column(it).toColumnAccessor() }

@JvmName("intoColumn")
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = df.move(columns).under(column)
public infix fun <T, C> GroupClause<T, C>.into(column: String): DataFrame<T> = into(columnGroup().named(column))
public infix fun <T, C> GroupClause<T, C>.into(column: ColumnGroupAccessor): DataFrame<T> = df.move(columns).under(column)
public infix fun <T, C> GroupClause<T, C>.into(column: KProperty<*>): DataFrame<T> = into(column.name)

// endregion

// region ungroup

public fun <T, C> DataFrame<T>.ungroup(columns: ColumnsSelector<T, C>): DataFrame<T> {
    return move { columns.toColumns().children() }
        .into { it.path.removeAt(it.path.size - 2).toPath() }
}

public fun <T> DataFrame<T>.ungroup(vararg columns: String): DataFrame<T> = ungroup { columns.toColumns() }
public fun <T> DataFrame<T>.ungroup(vararg columns: Column): DataFrame<T> = ungroup { columns.toColumns() }
public fun <T> DataFrame<T>.ungroup(vararg columns: KProperty<*>): DataFrame<T> = ungroup { columns.toColumns() }

// endregion

// region flatten

public fun <T> DataFrame<T>.flatten(): DataFrame<T> = flatten { all() }

public fun <T, C> DataFrame<T>.flatten(
    columns: ColumnsSelector<T, C>
): DataFrame<T> = flattenImpl(columns)

// endregion

// region select

public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> = get(columns).toDataFrame().cast()
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select(columns.map { it.name })
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select(columns.asIterable())
public fun <T> DataFrame<T>.select(vararg columns: Column): DataFrame<T> = select { columns.toColumns() }
@JvmName("selectT")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> = columns.map { get(it) }.toDataFrame().cast()
public fun <T> DataFrame<T>.select(columns: Iterable<Column>): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region addRowNumber

public fun <T> DataFrame<T>.addRowNumber(column: ColumnReference<Int>): DataFrame<T> = addRowNumber(column.name())

public fun <T> DataFrame<T>.addRowNumber(columnName: String = "id"): DataFrame<T> =
    dataFrameOf(columns() + indexColumn(columnName, nrow())).cast()

public fun AnyCol.addRowNumber(columnName: String = "id"): AnyFrame =
    dataFrameOf(listOf(indexColumn(columnName, size), this))

internal fun indexColumn(columnName: String, size: Int): AnyCol = column(columnName, (0 until size).toList())

// endregion

// region sortColumnsBy

public fun <T, C : Comparable<C>> DataFrame<T>.sortColumnsBy(dfs: Boolean = false, selector: (AnyCol) -> C): DataFrame<T> {
    var cols = columns()
    if (dfs) cols = cols.map {
        when (it.kind) {
            ColumnKind.Value -> it
            ColumnKind.Frame -> it.asFrameColumn().map { it.sortColumnsBy(true, selector) }
            ColumnKind.Group -> it.asColumnGroup().df.sortColumnsBy(true, selector).toColumnGroup(it.name())
        } as AnyCol
    }
    return cols.sortedBy { it.name() }.toDataFrame().cast()
}

// endregion

// region xs

public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> = xs(*keyValues) { allDfs().take(keyValues.size) }

public fun <T, C> DataFrame<T>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): DataFrame<T> = xsImpl(keyColumns, false, *keyValues)

// endregion

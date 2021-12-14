package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.reorderImpl
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region remove

public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> = removeImpl(columns = columns).df
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
public infix fun <T, C> GroupClause<T, C>.into(column: KProperty<*>): DataFrame<T> = into(column.columnName)

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
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select(columns.map { it.columnName })
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select(columns.asIterable())
public fun <T> DataFrame<T>.select(vararg columns: Column): DataFrame<T> = select { columns.toColumns() }
@JvmName("selectT")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> = columns.map { get(it) }.toDataFrame().cast()
public fun <T> DataFrame<T>.select(columns: Iterable<Column>): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region addId

public fun <T> DataFrame<T>.addId(column: ColumnAccessor<Int>): DataFrame<T> = insert(column) { index() }.at(0)

public fun <T> DataFrame<T>.addId(columnName: String = "id"): DataFrame<T> =
    insert(columnName) { index() }.at(0)

public fun AnyCol.addId(columnName: String = "id"): AnyFrame =
    toDataFrame().addId(columnName)

// endregion

// region reorder

public data class Reorder<T, C>(
    internal val df: DataFrame<T>,
    internal val columns: ColumnsSelector<T, C>,
    internal val inFrameColumns: Boolean
) {
    public fun <R> cast(): Reorder<T, R> = this as Reorder<T, R>
}

public fun <T, C> DataFrame<T>.reorder(selector: ColumnsSelector<T, C>): Reorder<T, C> = Reorder(this, selector, false)
public fun <T, C> DataFrame<T>.reorder(vararg columns: ColumnReference<C>): Reorder<T, C> = reorder { columns.toColumns() }
public fun <T, C> DataFrame<T>.reorder(vararg columns: KProperty<C>): Reorder<T, C> = reorder { columns.toColumns() }
public fun <T> DataFrame<T>.reorder(vararg columns: String): Reorder<T, *> = reorder { columns.toColumns() }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.by(expression: Selector<DataColumn<C>, V>): DataFrame<T> = reorderImpl(false, expression)

public fun <T, C> Reorder<T, C>.byName(desc: Boolean = false): DataFrame<T> = if (desc) byDesc { it.name } else by { it.name }

public fun <T, C, V : Comparable<V>> Reorder<T, C>.byDesc(expression: Selector<DataColumn<C>, V>): DataFrame<T> = reorderImpl(true, expression)

public fun <T, V : Comparable<V>> DataFrame<T>.reorderColumnsBy(
    dfs: Boolean = true,
    desc: Boolean = false,
    expression: Selector<AnyCol, V>
): DataFrame<T> = Reorder(this, { if (dfs) allDfs(true) else all() }, dfs).reorderImpl(desc, expression)

public fun <T> DataFrame<T>.reorderColumnsByName(dfs: Boolean = true, desc: Boolean = false): DataFrame<T> = reorderColumnsBy(dfs, desc) { name() }

// endregion

// region xs

public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> = xs(*keyValues) { allDfs().take(keyValues.size) }

public fun <T, C> DataFrame<T>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): DataFrame<T> = xsImpl(keyColumns, false, *keyValues)

// endregion

package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.afterOrBefore
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.moveImpl
import org.jetbrains.kotlinx.dataframe.impl.api.moveTo
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.newColumn
import org.jetbrains.kotlinx.dataframe.pathOf
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region add

public fun <T> DataFrame<T>.add(cols: Iterable<AnyCol>): DataFrame<T> = this + cols
public fun <T> DataFrame<T>.add(other: AnyFrame): DataFrame<T> = add(other.columns())

public interface AddDataRow<out T> : DataRow<T> {
    public fun <C> AnyRow.added(): C
}

internal class AddDataRowImpl<T>(index: Int, owner: DataFrame<T>, private val container: List<*>) :
    DataRowImpl<T>(index, owner),
    AddDataRow<T> {

    override fun <C> AnyRow.added() = container[index] as C
}

public typealias AddExpression<T, C> = AddDataRow<T>.(AddDataRow<T>) -> C

public inline fun <reified R, T> DataFrame<T>.add(name: String, noinline expression: AddExpression<T, R>): DataFrame<T> =
    (this + newColumn(name, expression))

public inline fun <reified R, T> DataFrame<T>.add(property: KProperty<R>, noinline expression: RowExpression<T, R>): DataFrame<T> =
    (this + newColumn(property.name, expression))

public inline fun <reified R, T> DataFrame<T>.add(column: ColumnAccessor<R>, noinline expression: AddExpression<T, R>): DataFrame<T> = add(column.path(), expression)

public inline fun <reified R, T> DataFrame<T>.add(path: ColumnPath, noinline expression: AddExpression<T, R>): DataFrame<T> {
    val col = newColumn(path.name(), expression)
    if (path.size == 1) return this + col
    return insert(path, col)
}

public fun <T> DataFrame<T>.add(body: AddDsl<T>.() -> Unit): DataFrame<T> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(this@add.columns() + dsl.columns).cast()
}

public fun <T> DataFrame<T>.add(vararg columns: AnyCol): DataFrame<T> = dataFrameOf(columns() + columns).cast()

public inline fun <reified R, T, G> GroupedDataFrame<T, G>.add(name: String, noinline expression: RowExpression<G, R>): GroupedDataFrame<T, G> =
    mapNotNullGroups { add(name, expression) }

public operator fun <T> DataFrame<T>.plus(body: AddDsl<T>.() -> Unit): DataFrame<T> = add(body)

public class AddDsl<T>(@PublishedApi internal val df: DataFrame<T>) : ColumnsContainer<T> by df, ColumnSelectionDsl<T> {

    internal val columns = mutableListOf<AnyCol>()

    public fun add(column: Column): Boolean = columns.add(column.resolveSingle(df)!!.data)

    public operator fun Column.unaryPlus(): Boolean = add(this)

    public operator fun String.unaryPlus(): Boolean = add(df[this])

    @PublishedApi
    internal inline fun <reified R> add(name: String, noinline expression: RowExpression<T, R>): Boolean = add(df.newColumn(name, expression))

    public inline infix fun <reified R> ColumnAccessor<R>.from(noinline expression: RowExpression<T, R>): Boolean = name().from(expression)

    public inline infix fun <reified R> ColumnAccessor<R>.from(column: ColumnReference<R>): Boolean = name().from(column)

    public inline infix fun <reified R> String.from(noinline expression: RowExpression<T, R>): Boolean = add(this, expression)

    public infix fun String.from(column: Column): Boolean = add(column.rename(this))

    public infix fun Column.into(name: String): Boolean = add(rename(name))

    public infix fun <C> ColumnReference<C>.into(column: ColumnAccessor<C>): Boolean = into(column.name())
}

// endregion

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

// endregion

// region insert

public fun <T> DataFrame<T>.insert(path: ColumnPath, column: AnyCol): DataFrame<T> =
    insertImpl(this, listOf(ColumnToInsert(path, column)))

public fun <T> DataFrame<T>.insert(column: AnyCol): InsertClause<T> = InsertClause(this, column)

public inline fun <T, reified R> DataFrame<T>.insert(name: String, noinline expression: RowExpression<T, R>): InsertClause<T> = insert(newColumn(name, expression))

public inline fun <T, reified R> DataFrame<T>.insert(
    column: ColumnAccessor<R>,
    noinline expression: RowExpression<T, R>
): InsertClause<T> = insert(column.name(), expression)

public data class InsertClause<T>(val df: DataFrame<T>, val column: AnyCol)

public fun <T> InsertClause<T>.under(column: ColumnSelector<T, *>): DataFrame<T> = under(df.getColumnPath(column))
public fun <T> InsertClause<T>.under(columnPath: ColumnPath): DataFrame<T> = df.insert(columnPath + column.name, column)
public fun <T> InsertClause<T>.under(column: ColumnAccessor<*>): DataFrame<T> = under(column.path())

public fun <T> InsertClause<T>.after(column: ColumnSelector<T, *>): DataFrame<T> = after(df.getColumnPath(column))
public fun <T> InsertClause<T>.after(column: String): DataFrame<T> = df.add(this.column).move(this.column).after(column)
public fun <T> InsertClause<T>.after(column: ColumnAccessor<*>): DataFrame<T> = after(column.path())
public fun <T> InsertClause<T>.after(columnPath: ColumnPath): DataFrame<T> {
    val dstPath = ColumnPath(columnPath.removeAt(columnPath.size - 1) + column.name())
    return df.insert(dstPath, column).move { dstPath }.after { columnPath }
}

public fun <T> InsertClause<T>.at(position: Int): DataFrame<T> = df.add(column).move(column).to(position)

// endregion

// region replace

public fun <T, C> DataFrame<T>.replace(columns: ColumnsSelector<T, C>): ReplaceCause<T, C> = ReplaceCause(this, columns)
public fun <T> DataFrame<T>.replace(vararg columns: String): ReplaceCause<T, Any?> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(vararg columns: ColumnReference<C>): ReplaceCause<T, C> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(vararg columns: KProperty<C>): ReplaceCause<T, C> = replace { columns.toColumns() }
public fun <T, C> DataFrame<T>.replace(columns: Iterable<ColumnReference<C>>): ReplaceCause<T, C> = replace { columns.toColumnSet() }

public fun <T> DataFrame<T>.replaceAll(vararg valuePairs: Pair<Any?, Any?>, columns: ColumnsSelector<T, *> = { dfs() }): DataFrame<T> {
    val map = valuePairs.toMap()
    return update(columns).withExpression { map[it] ?: it }
}

public data class ReplaceCause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public fun <T, C> ReplaceCause<T, C>.with(vararg columns: AnyCol): DataFrame<T> = with(columns.toList())

public fun <T, C> ReplaceCause<T, C>.with(newColumns: List<AnyCol>): DataFrame<T> {
    var index = 0
    return with {
        require(index < newColumns.size) { "Insufficient number of new columns in 'replace': ${newColumns.size} instead of ${df[columns].size}" }
        newColumns[index++]
    }
}

public fun <T, C> ReplaceCause<T, C>.with(transform: ColumnsContainer<T>.(DataColumn<C>) -> AnyCol): DataFrame<T> {
    val removeResult = df.removeImpl(columns)
    val toInsert = removeResult.removedColumns.map {
        val newCol = transform(df, it.data.column as DataColumn<C>)
        ColumnToInsert(it.pathFromRoot().dropLast(1) + newCol.name, newCol, it)
    }
    return removeResult.df.insertImpl(toInsert)
}

// endregion

// region move

public fun <T, C> DataFrame<T>.move(columns: ColumnsSelector<T, C>): MoveClause<T, C> = MoveClause(this, columns)
public fun <T> DataFrame<T>.move(vararg cols: String): MoveClause<T, Any?> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(vararg cols: ColumnReference<C>): MoveClause<T, C> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(vararg cols: KProperty<C>): MoveClause<T, C> = move { cols.toColumns() }

public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).to(newColumnIndex)
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: String): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: Column): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: KProperty<*>): DataFrame<T> = moveTo(newColumnIndex) { columns.toColumns() }

public fun <T> DataFrame<T>.moveToLeft(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toLeft()
public fun <T> DataFrame<T>.moveToLeft(vararg columns: String): DataFrame<T> = moveToLeft { columns.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg columns: Column): DataFrame<T> = moveToLeft { columns.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg columns: KProperty<*>): DataFrame<T> = moveToLeft { columns.toColumns() }

public fun <T> DataFrame<T>.moveToRight(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toRight()
public fun <T> DataFrame<T>.moveToRight(vararg columns: String): DataFrame<T> = moveToRight { columns.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg columns: Column): DataFrame<T> = moveToRight { columns.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg columns: KProperty<*>): DataFrame<T> = moveToRight { columns.toColumns() }

public fun <T, C> MoveClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = moveImpl(
    under = false,
    column
)

public fun <T, C> MoveClause<T, C>.into(column: String): DataFrame<T> = pathOf(column).let { path -> into { path } }

public fun <T, C> MoveClause<T, C>.intoIndexed(
    newPathExpression: ColumnsSelectionDsl<T>.(ColumnWithPath<C>, Int) -> Column
): DataFrame<T> {
    var counter = 0
    return into { col ->
        newPathExpression(this, col, counter++)
    }
}

public fun <T, C> MoveClause<T, C>.under(column: String): DataFrame<T> = pathOf(column).let { path -> under { path } }
public fun <T, C> MoveClause<T, C>.under(column: ColumnGroupAccessor): DataFrame<T> = column.path().let { path -> under { path } }
public fun <T, C> MoveClause<T, C>.under(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> Column): DataFrame<T> = moveImpl(
    under = true,
    column
)

public fun <T, C> MoveClause<T, C>.to(columnIndex: Int): DataFrame<T> = moveTo(columnIndex)

public fun <T, C> MoveClause<T, C>.toTop(
    newColumnName: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String = { it.name() }
): DataFrame<T> =
    into { newColumnName(it).toColumnAccessor() }

public fun <T, C> MoveClause<T, C>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterOrBefore(column, true)
public fun <T, C> MoveClause<T, C>.after(column: String): DataFrame<T> = after { column.toColumnAccessor() }
public fun <T, C> MoveClause<T, C>.after(column: Column): DataFrame<T> = after { column }
public fun <T, C> MoveClause<T, C>.after(column: KProperty<*>): DataFrame<T> = after { column.toColumnAccessor() }

// TODO: implement 'before'
/*
fun <T, C> MoveColsClause<T, C>.before(columnPath: ColumnPath) = before { columnPath.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: Column) = before { column }
fun <T, C> MoveColsClause<T, C>.before(column: KProperty<*>) = before { column.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: String) = before { column.toColumnDef() }
fun <T, C> MoveColsClause<T, C>.before(column: ColumnSelector<T, *>) = afterOrBefore(column, false)
*/

public fun <T, C> MoveClause<T, C>.toLeft(): DataFrame<T> = to(0)
public fun <T, C> MoveClause<T, C>.toRight(): DataFrame<T> = to(df.ncol())

public class MoveClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>)

// endregion

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

// region rename

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> = rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
    .into(*mappings.map { it.second }.toTypedArray())

public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(cols: Iterable<ColumnReference<C>>): RenameClause<T, C> = rename { cols.toColumnSet() }

public data class RenameClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = df.move(columns).intoIndexed { col, index ->
    col.path.drop(1) + newNames[index]
}

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> = df.move(columns).into {
    it.path.dropLast(1) + transform(it)
}

// endregion

// region flatten

internal val defaultFlattenSeparator: CharSequence = "_"

public fun <T> DataFrame<T>.flatten(separator: CharSequence = defaultFlattenSeparator): DataFrame<T> = flatten(separator) { all() }

public fun <T, C> DataFrame<T>.flatten(
    separator: CharSequence = defaultFlattenSeparator,
    columns: ColumnsSelector<T, C>
): DataFrame<T> = flattenImpl(separator, columns)

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
            ColumnKind.Frame -> it.asFrameColumn().map { it?.sortColumnsBy(true, selector) }
            ColumnKind.Group -> it.asColumnGroup().df.sortColumnsBy(true, selector).toColumnGroup(it.name())
        } as AnyCol
    }
    return cols.sortedBy { it.name() }.toDataFrame().cast()
}

// endregion

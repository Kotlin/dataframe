package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameBase
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowSelector
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.MoveDslImpl
import org.jetbrains.kotlinx.dataframe.impl.api.afterOrBefore
import org.jetbrains.kotlinx.dataframe.impl.api.flattenImpl
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.moveInto
import org.jetbrains.kotlinx.dataframe.impl.api.moveTo
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.impl.toColumnPath
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.newColumn
import org.jetbrains.kotlinx.dataframe.pathOf
import org.jetbrains.kotlinx.dataframe.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.typed
import kotlin.reflect.KProperty

// region add

public fun <T> DataFrame<T>.add(cols: Iterable<AnyCol>): DataFrame<T> = this + cols

public fun <T> DataFrame<T>.add(other: AnyFrame): DataFrame<T> = add(other.columns())

public fun <T> DataFrame<T>.add(column: AnyCol): DataFrame<T> = this + column

public fun <T> DataFrame<T>.add(name: String, data: AnyCol): DataFrame<T> = dataFrameOf(columns() + data.rename(name)).typed<T>()

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

public inline fun <reified R, T> DataFrame<T>.add(property: KProperty<R>, noinline expression: RowSelector<T, R>): DataFrame<T> =
    (this + newColumn(property.name, expression))

public inline fun <reified R, T, G> GroupedDataFrame<T, G>.add(name: String, noinline expression: RowSelector<G, R>): GroupedDataFrame<T, G> =
    mapNotNullGroups { add(name, expression) }

public inline fun <reified R, T> DataFrame<T>.add(column: ColumnAccessor<R>, noinline expression: AddExpression<T, R>): DataFrame<T> {
    val col = newColumn(column.name(), expression)
    val path = column.path()
    if (path.size == 1) return this + col
    return insert(path, col)
}

public fun <T> DataFrame<T>.add(body: AddDsl<T>.() -> Unit): DataFrame<T> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(this@add.columns() + dsl.columns).typed()
}

public operator fun <T> DataFrame<T>.plus(body: AddDsl<T>.() -> Unit): DataFrame<T> = add(body)

public class AddDsl<T>(@PublishedApi internal val df: DataFrame<T>) : DataFrameBase<T> by df {

    internal val columns = mutableListOf<AnyCol>()

    public fun add(column: AnyCol): Boolean = columns.add(column)

    public inline fun <reified R> add(name: String, noinline expression: RowSelector<T, R>): Boolean = add(df.newColumn(name, expression))

    public inline fun <reified R> add(column: ColumnReference<R>, noinline expression: RowSelector<T, R>): Boolean = add(df.newColumn(column.name(), expression))

    public inline infix fun <reified R> ColumnReference<R>.from(noinline expression: RowSelector<T, R>): Boolean = add(df.newColumn(name(), expression))

    public inline operator fun <reified R> ColumnReference<R>.invoke(noinline expression: RowSelector<T, R>): Boolean =
        from(expression)

    public inline infix fun <reified R> String.from(noinline expression: RowSelector<T, R>): Boolean = add(this, expression)

    public inline operator fun <reified R> String.invoke(noinline expression: RowSelector<T, R>): Boolean = from(expression)

    public operator fun String.invoke(column: AnyCol): Boolean = add(column.rename(this))

    public inline operator fun <reified R> ColumnReference<R>.invoke(column: DataColumn<R>): Boolean = name()(column)

    public infix fun AnyCol.into(name: String): Boolean = add(rename(name))
}

// endregion

// region remove

public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(column: Column): DataFrame<T> = remove(column)
public infix operator fun <T> DataFrame<T>.minus(cols: Iterable<Column>): DataFrame<T> = remove(cols)
public infix operator fun <T> DataFrame<T>.minus(cols: ColumnsSelector<T, *>): DataFrame<T> = remove(cols)

public fun <T> DataFrame<T>.remove(selector: ColumnsSelector<T, *>): DataFrame<T> = removeImpl(selector).df
public fun <T> DataFrame<T>.remove(vararg cols: KProperty<*>): DataFrame<T> = remove { cols.toColumns() }
public fun <T> DataFrame<T>.remove(vararg cols: String): DataFrame<T> = remove { cols.toColumns() }
public fun <T> DataFrame<T>.remove(vararg cols: Column): DataFrame<T> = remove { cols.toColumns() }
public fun <T> DataFrame<T>.remove(cols: Iterable<Column>): DataFrame<T> = remove { cols.toColumnSet() }

// endregion

// region insert

public fun <T> DataFrame<T>.insert(path: ColumnPath, column: AnyCol): DataFrame<T> =
    insertImpl(this, listOf(ColumnToInsert(path, column)))

public fun <T> DataFrame<T>.insert(column: AnyCol): InsertClause<T> = InsertClause(this, column)

public inline fun <T, reified R> DataFrame<T>.insert(noinline expression: RowSelector<T, R>): InsertClause<T> = insert("", expression)

public inline fun <T, reified R> DataFrame<T>.insert(name: String, noinline expression: RowSelector<T, R>): InsertClause<T> = insert(newColumn(name, expression))

public data class InsertClause<T>(val df: DataFrame<T>, val column: AnyCol)

public fun <T> InsertClause<T>.into(path: ColumnPath): DataFrame<T> = df.insert(path, column.rename(path.last()))
public fun <T> InsertClause<T>.into(reference: ColumnAccessor<*>): DataFrame<T> = into(reference.path())

public fun <T> InsertClause<T>.under(path: ColumnPath): DataFrame<T> = df.insert(path + column.name, column)
public fun <T> InsertClause<T>.under(selector: ColumnSelector<T, *>): DataFrame<T> = under(df.getColumnPath(selector))

public fun <T> InsertClause<T>.after(name: String): DataFrame<T> = df.add(column).move(column).after(name)
public fun <T> InsertClause<T>.after(selector: ColumnSelector<T, *>): DataFrame<T> = after(df.getColumnPath(selector))

public fun <T> InsertClause<T>.at(position: Int): DataFrame<T> = df.add(column).move(column).to(position)

public fun <T> InsertClause<T>.after(path: ColumnPath): DataFrame<T> {
    val colPath = ColumnPath(path.removeAt(path.size - 1) + column.name())
    return df.insert(colPath, column).move(colPath).after(path)
}

// endregion

// region replace

public fun <T, C> DataFrame<T>.replace(selector: ColumnsSelector<T, C>): ReplaceCause<T, C> = ReplaceCause(this, selector)
public fun <T, C> DataFrame<T>.replace(vararg cols: ColumnReference<C>): ReplaceCause<T, C> = replace { cols.toColumns() }
public fun <T, C> DataFrame<T>.replace(vararg cols: KProperty<C>): ReplaceCause<T, C> = replace { cols.toColumns() }
public fun <T> DataFrame<T>.replace(vararg cols: String): ReplaceCause<T, Any?> = replace { cols.toColumns() }
public fun <T, C> DataFrame<T>.replace(cols: Iterable<ColumnReference<C>>): ReplaceCause<T, C> = replace { cols.toColumnSet() }

public fun <T> DataFrame<T>.replaceAll(vararg valuePairs: Pair<Any?, Any?>, selector: ColumnsSelector<T, *> = { dfs() }): DataFrame<T> {
    val map = valuePairs.toMap()
    return update(selector).withExpression { map[it] ?: it }
}

public data class ReplaceCause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

public fun <T, C> ReplaceCause<T, C>.with(vararg columns: AnyCol): DataFrame<T> = with(columns.toList())

public fun <T, C> ReplaceCause<T, C>.with(newColumns: List<AnyCol>): DataFrame<T> {
    var index = 0
    return with {
        require(index < newColumns.size) { "Insufficient number of new columns in 'replace': ${newColumns.size} instead of ${df[selector].size}" }
        newColumns[index++]
    }
}

public fun <T, C> ReplaceCause<T, C>.with(transform: DataFrameBase<T>.(DataColumn<C>) -> AnyCol): DataFrame<T> {
    val removeResult = df.removeImpl(selector)
    val toInsert = removeResult.removedColumns.map {
        val newCol = transform(df, it.data.column as DataColumn<C>)
        ColumnToInsert(it.pathFromRoot().dropLast(1) + newCol.name, newCol, it)
    }
    return removeResult.df.insertImpl(toInsert)
}

// endregion

// region move

public fun <T, C> DataFrame<T>.move(cols: Iterable<ColumnReference<C>>): MoveClause<T, C> = move { cols.toColumnSet() }
public fun <T, C> DataFrame<T>.move(vararg cols: ColumnReference<C>): MoveClause<T, C> = move { cols.toColumns() }
public fun <T> DataFrame<T>.move(vararg cols: String): MoveClause<T, Any?> = move { cols.toColumns() }
public fun <T> DataFrame<T>.move(vararg cols: ColumnPath): MoveClause<T, Any?> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(vararg cols: KProperty<C>): MoveClause<T, C> = move { cols.toColumns() }
public fun <T, C> DataFrame<T>.move(selector: ColumnsSelector<T, C>): MoveClause<T, C> = MoveClause(this, selector)

public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, selector: ColumnsSelector<T, *>): DataFrame<T> = move(selector).to(newColumnIndex)
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, cols: Iterable<Column>): DataFrame<T> = moveTo(newColumnIndex) { cols.toColumnSet() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: String): DataFrame<T> = moveTo(newColumnIndex) { cols.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: KProperty<*>): DataFrame<T> = moveTo(newColumnIndex) { cols.toColumns() }
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg cols: Column): DataFrame<T> = moveTo(newColumnIndex) { cols.toColumns() }

public fun <T> DataFrame<T>.moveToLeft(selector: ColumnsSelector<T, *>): DataFrame<T> = move(selector).toLeft()
public fun <T> DataFrame<T>.moveToLeft(cols: Iterable<Column>): DataFrame<T> = moveToLeft { cols.toColumnSet() }
public fun <T> DataFrame<T>.moveToLeft(vararg cols: String): DataFrame<T> = moveToLeft { cols.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg cols: Column): DataFrame<T> = moveToLeft { cols.toColumns() }
public fun <T> DataFrame<T>.moveToLeft(vararg cols: KProperty<*>): DataFrame<T> = moveToLeft { cols.toColumns() }

public fun <T> DataFrame<T>.moveToRight(cols: ColumnsSelector<T, *>): DataFrame<T> = move(cols).toRight()
public fun <T> DataFrame<T>.moveToRight(cols: Iterable<Column>): DataFrame<T> = moveToRight { cols.toColumnSet() }
public fun <T> DataFrame<T>.moveToRight(vararg cols: String): DataFrame<T> = moveToRight { cols.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg cols: Column): DataFrame<T> = moveToRight { cols.toColumns() }
public fun <T> DataFrame<T>.moveToRight(vararg cols: KProperty<*>): DataFrame<T> = moveToRight { cols.toColumns() }

public interface MoveDsl<T> : DataFrameBase<T> {

    public fun path(vararg columns: String): ColumnPath = pathOf(*columns)

    public fun SingleColumn<*>.addPath(columnPath: ColumnPath): ColumnPath

    public operator fun SingleColumn<*>.plus(column: String): ColumnPath = addPath(pathOf(column))
}

public fun <T, C> MoveClause<T, C>.under(parentPath: MoveDsl<T>.(ColumnWithPath<C>) -> ColumnPath): DataFrame<T> {
    val receiver = MoveDslImpl(df)
    return into { parentPath(receiver, it) + it.name }
}

public fun <T, C> MoveClause<T, C>.toTop(
    groupNameExpression: MoveDsl<T>.(ColumnWithPath<C>) -> String = { it.name() }
): DataFrame<T> =
    into { pathOf(groupNameExpression(it)) }

public fun <T, C> MoveClause<T, C>.intoIndexed(
    newPathExpression: MoveDsl<T>.(ColumnWithPath<C>, Int) -> ColumnPath
): DataFrame<T> {
    var counter = 0
    return into { col ->
        newPathExpression(this, col, counter++)
    }
}

public fun <T, C> MoveClause<T, C>.into(newPathExpression: MoveDsl<T>.(ColumnWithPath<C>) -> ColumnPath): DataFrame<T> = moveInto(newPathExpression)

public fun <T, C> MoveClause<T, C>.into(vararg path: String): DataFrame<T> = into(path.toColumnPath())
public fun <T, C> MoveClause<T, C>.into(path: ColumnPath): DataFrame<T> = into { path }

public fun <T, C> MoveClause<T, C>.under(vararg path: String): DataFrame<T> = under(path.toColumnPath())
public fun <T, C> MoveClause<T, C>.under(path: ColumnPath): DataFrame<T> = under { path }
public fun <T, C> MoveClause<T, C>.under(groupRef: ColumnGroupReference): DataFrame<T> = under(groupRef.path())

public fun <T, C> MoveClause<T, C>.to(columnIndex: Int): DataFrame<T> = moveTo(columnIndex)

public fun <T, C> MoveClause<T, C>.after(columnPath: ColumnPath): DataFrame<T> = after { columnPath.toColumnAccessor() }
public fun <T, C> MoveClause<T, C>.after(column: Column): DataFrame<T> = after { column }
public fun <T, C> MoveClause<T, C>.after(column: KProperty<*>): DataFrame<T> = after { column.toColumnAccessor() }
public fun <T, C> MoveClause<T, C>.after(column: String): DataFrame<T> = after { column.toColumnAccessor() }
public fun <T, C> MoveClause<T, C>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterOrBefore(column, true)

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

public class MoveClause<T, C>(internal val df: DataFrame<T>, internal val selector: ColumnsSelector<T, C>)

// endregion

// region group

public data class GroupClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

public fun <T> DataFrame<T>.group(cols: Iterable<Column>): GroupClause<T, Any?> = group { cols.toColumnSet() }
public fun <T> DataFrame<T>.group(vararg cols: KProperty<*>): GroupClause<T, Any?> = group { cols.toColumns() }
public fun <T> DataFrame<T>.group(vararg cols: String): GroupClause<T, Any?> = group { cols.toColumns() }
public fun <T> DataFrame<T>.group(vararg cols: Column): GroupClause<T, Any?> = group { cols.toColumns() }
public fun <T, C> DataFrame<T>.group(cols: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, cols)

public infix fun <T, C> GroupClause<T, C>.into(groupName: String): DataFrame<T> = into { groupName }
public infix fun <T, C> GroupClause<T, C>.into(groupRef: ColumnGroupReference): DataFrame<T> = df.move(selector).under(groupRef)
public infix fun <T, C> GroupClause<T, C>.into(groupName: ColumnWithPath<C>.(ColumnWithPath<C>) -> String): DataFrame<T> = df.move(selector).under { path(groupName(it, it)) }

// endregion

// region ungroup

public fun <T> DataFrame<T>.ungroup(vararg columns: KProperty<*>): DataFrame<T> = ungroup { columns.toColumns() }
public fun <T> DataFrame<T>.ungroup(vararg columns: String): DataFrame<T> = ungroup { columns.toColumns() }
public fun <T> DataFrame<T>.ungroup(vararg columns: Column): DataFrame<T> = ungroup { columns.toColumns() }

public fun <T, C> DataFrame<T>.ungroup(selector: ColumnsSelector<T, C>): DataFrame<T> {
    return move { selector.toColumns().children() }
        .into { it.path.removeAt(it.path.size - 2).toColumnPath() }
}

// endregion

// region rename

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> = rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
    .into(*mappings.map { it.second }.toTypedArray())

public fun <T, C> DataFrame<T>.rename(selector: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, selector)
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumns() }
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumns() }
public fun <T, C> DataFrame<T>.rename(cols: Iterable<ColumnReference<C>>): RenameClause<T, C> = rename { cols.toColumnSet() }

public data class RenameClause<T, C>(val df: DataFrame<T>, val selector: ColumnsSelector<T, C>)

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = df.move(selector).intoIndexed { col, index ->
    col.path.drop(1) + newNames[index]
}

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> = df.move(selector).into {
    it.path.dropLast(1) + transform(it)
}

// endregion

// region flatten

internal val defaultFlattenSeparator: CharSequence = "_"

public fun <T> DataFrame<T>.flatten(separator: CharSequence = defaultFlattenSeparator): DataFrame<T> = flatten(separator) { all() }

public fun <T, C> DataFrame<T>.flatten(
    separator: CharSequence = defaultFlattenSeparator,
    selector: ColumnsSelector<T, C>
): DataFrame<T> = flattenImpl(separator, selector)

// endregion

// region select

public fun <T> DataFrame<T>.select(selector: ColumnsSelector<T, *>): DataFrame<T> = get(selector).toDataFrame()
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select(columns.map { it.name })
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select(columns.asIterable())
public fun <T> DataFrame<T>.select(vararg columns: Column): DataFrame<T> = select { columns.toColumns() }
@JvmName("selectT")
public fun <T> DataFrame<T>.select(columns: Iterable<String>): DataFrame<T> = columns.map { get(it) }.toDataFrame()
public fun <T> DataFrame<T>.select(columns: Iterable<Column>): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region addRowNumber

public fun <T> DataFrame<T>.addRowNumber(column: ColumnReference<Int>): DataFrame<T> = addRowNumber(column.name())

public fun <T> DataFrame<T>.addRowNumber(columnName: String = "id"): DataFrame<T> =
    dataFrameOf(columns() + indexColumn(columnName, nrow())).typed()

public fun AnyCol.addRowNumber(columnName: String = "id"): AnyFrame =
    dataFrameOf(listOf(indexColumn(columnName, size), this))

internal fun indexColumn(columnName: String, size: Int): AnyCol = column(columnName, (0 until size).toList())

// endregion

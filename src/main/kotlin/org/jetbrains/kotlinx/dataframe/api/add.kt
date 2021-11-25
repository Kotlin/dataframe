package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.DataRowImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.resolveSingle
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.newColumn
import kotlin.reflect.KProperty

public fun <T> DataFrame<T>.add(cols: Iterable<AnyCol>): DataFrame<T> = this + cols
public fun <T> DataFrame<T>.add(vararg other: AnyFrame): DataFrame<T> = add(other.flatMap { it.columns() })

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

public inline fun <reified R, T, G> GroupBy<T, G>.add(name: String, noinline expression: RowExpression<G, R>): GroupBy<T, G> =
    mapGroups { add(name, expression) }

public inline fun <reified R, T, G> GroupBy<T, G>.add(
    column: ColumnAccessor<G>,
    noinline expression: RowExpression<G, R>
): GroupBy<T, G> =
    add(column.name(), expression)

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

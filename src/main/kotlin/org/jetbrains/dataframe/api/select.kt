package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnSet
import kotlin.reflect.KProperty

fun <T> DataFrame<T>.select(selector: ColumnsSelector<T, *>): DataFrame<T> = new(getColumns(selector))
fun <T> DataFrame<T>.select(vararg columns: KProperty<*>) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: String) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: Column) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(columns: Iterable<Column>) = select { columns.toColumnSet() }

interface SelectReceiver<out T> : ColumnsSelectorReceiver<T> {

    infix fun <C> ColumnSet<C>.and(other: ColumnSet<C>) = ColumnGroup(this, other)

    fun <C> ColumnSet<C>.except(vararg other: ColumnSet<*>) = except(other.toList().toColumnSet())

    infix fun <C> ColumnSet<C>.except(other: ColumnSet<*>): ColumnSet<*> = createColumnSet { resolve(it).allColumnsExcept(other.resolve(it)) }

    infix fun <C> ColumnSet<C>.except(selector: ColumnsSelector<T, *>) = except(selector.toColumns())

    operator fun <C> ColumnSelector<T, C>.invoke() = this(this@SelectReceiver, this@SelectReceiver)

    operator fun <C> ColumnReference<C>.invoke(newName: String) = rename(newName)
    infix fun <C> DataColumn<C>.into(newName: String) = (this as ColumnReference<C>).rename(newName)

    infix fun String.and(other: String) = toColumnDef() and other.toColumnDef()
    infix fun <C> String.and(other: ColumnSet<C>) = toColumnDef() and other
    infix fun <C> KProperty<C>.and(other: ColumnSet<C>) = toColumnDef() and other
    infix fun <C> ColumnSet<C>.and(other: KProperty<C>) = this and other.toColumnDef()
    infix fun <C> KProperty<C>.and(other: KProperty<C>) = toColumnDef() and other.toColumnDef()
    infix fun <C> ColumnSet<C>.and(other: String) = this and other.toColumnDef()

    operator fun <C> ColumnSet<C>.plus(other: ColumnSet<C>) = this and other
}
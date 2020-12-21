package org.jetbrains.dataframe

import kotlin.reflect.KProperty

fun <T> DataFrame<T>.select(selector: ColumnsSelector<T, *>): DataFrame<T> = new(getColumns(selector))
fun <T> DataFrame<T>.select(vararg columns: KProperty<*>) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: String) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(vararg columns: Column) = select { columns.toColumns() }
fun <T> DataFrame<T>.select(columns: Iterable<Column>) = select { columns.toColumns() }

interface SelectReceiver<out T> : ColumnsSelectorReceiver<T> {

    infix fun <C> ColumnSet<C>.and(other: ColumnSet<C>) = ColumnGroup(this, other)

    operator fun <C> ColumnData<C>.invoke(newName: String) = rename(newName)

    fun <C> ColumnSet<C>.allExcept(vararg other: ColumnSet<*>) = allExcept(other.toList().toColumns())

    fun <C> ColumnSet<C>.allExcept(other: ColumnSet<*>): ColumnSet<*> = createColumnSet { resolve(it).allColumnsExcept(other.resolve(it)) }

    fun <C> ColumnSet<C>.allExcept(selector: ColumnsSelector<T, *>) = allExcept(selector.toColumns())

    operator fun <C> ColumnSelector<T, C>.invoke() = this(this@SelectReceiver, this@SelectReceiver)

    operator fun <C> ColumnDef<C>.invoke(newName: String) = rename(newName)

    infix fun String.and(other: String) = toColumnDef() and other.toColumnDef()

    infix fun <C> String.and(other: ColumnSet<C>) = toColumnDef() and other

    infix fun <C> KProperty<*>.and(other: ColumnSet<C>) = toColumnDef() and other
    infix fun <C> ColumnSet<C>.and(other: KProperty<C>) = this and other.toColumnDef()
    infix fun KProperty<*>.and(other: KProperty<*>) = toColumnDef() and other.toColumnDef()

    infix fun <C> ColumnSet<C>.and(other: String) = this and other.toColumnDef()
    operator fun <C> ColumnSet<C>.plus(other: ColumnSet<C>) = this and other
}
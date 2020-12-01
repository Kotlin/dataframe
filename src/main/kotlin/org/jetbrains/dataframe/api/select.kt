package org.jetbrains.dataframe

import kotlin.reflect.KProperty

fun <T, C> DataFrame<T>.select(selector: ColumnsSelector<T, C>) = select(getColumns(selector))
fun <T> DataFrame<T>.select(vararg columns: KProperty<*>) = select(getColumns(columns))
fun <T> DataFrame<T>.select(vararg columns: String) = select(getColumns(columns))
fun <T> DataFrame<T>.select(vararg columns: Column) = select(columns.toList())
fun <T, C> DataFrame<T>.select(columns: Iterable<ColumnDef<C>>): DataFrame<T> = new(columns.map { this[it] })
interface DataFrameWithColumnsForSelect<out T> : DataFrameWithColumns<T> {

    infix fun <C> ColumnSet<C>.and(other: ColumnSet<C>) = ColumnGroup(this, other)

    operator fun <C> ColumnData<C>.invoke(newName: String) = rename(newName)

    fun allExcept(vararg other: ColumnSet<*>) = allExcept(other.toList())

    fun allExcept(other: List<ColumnSet<*>>) = AllExceptColumn(ColumnGroup(other)) as ColumnSet<Any?>

    fun allExcept(selector: ColumnsSelector<T, *>) = allExcept(selector(this, this))

    operator fun <C> ColumnSelector<T, C>.invoke() = this(this@DataFrameWithColumnsForSelect, this@DataFrameWithColumnsForSelect)

    operator fun <C> ColumnDef<C>.invoke(newName: String) = rename(newName)

    infix fun String.and(other: String) = toColumnDef() and other.toColumnDef()

    infix fun <C> String.and(other: ColumnSet<C>) = toColumnDef() and other

    infix fun <C> KProperty<*>.and(other: ColumnSet<C>) = toColumnName() and other
    infix fun <C> ColumnSet<C>.and(other: KProperty<C>) = this and other.toColumnName()
    infix fun KProperty<*>.and(other: KProperty<*>) = toColumnName() and other.toColumnName()

    infix fun <C> ColumnSet<C>.and(other: String) = this and other.toColumnDef()
    operator fun <C> ColumnSet<C>.plus(other: ColumnSet<C>) = this and other
}
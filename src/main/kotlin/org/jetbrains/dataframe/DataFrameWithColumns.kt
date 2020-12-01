package org.jetbrains.dataframe

import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface DataFrameWithColumns<out T> : DataFrame<T> {

    fun cols(vararg col: DataCol) = ColumnGroup(col.toList())

    fun cols(vararg col: String) = ColumnGroup(col.map { it.toColumnDef() })

    fun DataFrameBase<*>.group(name: String) = this.get(name) as GroupedColumn<*>

    fun DataFrameBase<*>.cols(predicate: (DataCol) -> Boolean) = ColumnGroup(this.columns().filter(predicate))

    fun <C> DataFrameBase<*>.colsOfType(type: KType, filter: (ColumnData<C>) -> Boolean = { true }) = this.columns().filter { it.type.isSubtypeOf(type) && (type.isMarkedNullable || !it.hasNulls) && filter(it.typed()) }.map { it.typed<C>() }.toColumnSet()

    fun DataFrameBase<*>.colsDfs(predicate: (DataCol) -> Boolean = { true }) = ColumnGroup(this.columns().dfs().filter(predicate))

    fun DataFrameBase<*>.all() = ColumnGroup(this.columns())

    fun GroupedColumn<*>.all() = ColumnGroup(this.columns())

    fun GroupedColumnDef.all() = ColumnsBySelectorImpl(this) { columns() }

    fun DataCol.parent(): GroupedColumn<*>? = when(this){
        is ColumnWithParent<*> -> parent
        else -> null
    }

    fun DataCol.depth(): Int = parent()?.depth()?.plus(1) ?: 0

    val cols: List<DataCol> get() = columns()

    operator fun List<DataCol>.get(range: IntRange) = ColumnGroup(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumnDef()

    fun <C> String.cast() = NamedColumnImpl<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumnDef()

    fun <C> col(colName: String) = colName.cast<C>()
}
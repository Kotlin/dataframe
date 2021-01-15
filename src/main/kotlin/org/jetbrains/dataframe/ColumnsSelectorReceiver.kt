package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import org.jetbrains.dataframe.api.columns.ColumnSet
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.GroupedColumn
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface ColumnsSelectorReceiver<out T> : DataFrameBase<T> {

    fun cols(vararg col: DataCol) = ColumnGroup(col.toList())

    fun cols(vararg col: String) = ColumnGroup(col.map { it.toColumnDef() })

    fun DataFrameBase<*>.group(name: String) = this.get(name) as GroupedColumn<*>

    fun DataFrameBase<*>.cols(predicate: (DataCol) -> Boolean) = ColumnGroup(this.columns().filter(predicate))

    fun <C> DataFrameBase<*>.colsOfType(type: KType, filter: (ColumnData<C>) -> Boolean = { true }) = this.columns().filter { it.type.isSubtypeOf(type) && (type.isMarkedNullable || !it.hasNulls) && filter(it.typed()) }.map { it.typed<C>() }.toColumnSet()

    fun DataFrameBase<*>.all() = ColumnGroup(this.columns())

    fun DataFrameBase<*>.colGroups(filter: (GroupedColumn<*>) -> Boolean = { true }): ColumnSet<DataRow<*>> = this.columns().filter { it.isGrouped() && filter(it.asGrouped()) }.map { it.asGrouped() }.toColumnSet()

    fun <C> ColumnSet<C>.filter(predicate: Predicate<ColumnData<C>>) = createColumnSet { resolve(it).filter { predicate(it.data) } }

    fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean = { true }) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().dfs().filter(predicate) } }

    fun <C> ColumnSet<C>.children(predicate: (DataCol) -> Boolean = {true} ) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().filter { predicate(it.data) } } }

    fun GroupedColumnDef.children() = createColumnSet { resolve(it).single().children() }

    val cols: List<DataCol> get() = columns()

    operator fun List<DataCol>.get(range: IntRange) = ColumnGroup(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumnDef()

    fun <C> String.cast() = ColumnDefinition<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumnDef()

    fun <C> col(colName: String) = colName.cast<C>()
}
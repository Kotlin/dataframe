package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnSet
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.MapColumn
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface ColumnsSelectorReceiver<out T> : DataFrameBase<T> {

    fun cols(vararg col: AnyCol) = ColumnGroup(col.toList())

    fun cols(vararg col: String) = ColumnGroup(col.map { it.toColumnDef() })

    fun cols(range: IntRange) = ColumnGroup(columns().subList(range.start, range.endInclusive+1))

    fun DataFrameBase<*>.group(name: String) = this.get(name) as MapColumn<*>

    fun DataFrameBase<*>.cols(predicate: (AnyCol) -> Boolean) = ColumnGroup(this.columns().filter(predicate))

    fun <C> DataFrameBase<*>.colsOfType(type: KType, filter: (DataColumn<C>) -> Boolean = { true }) = this.columns().filter { it.type.isSubtypeOf(type) && (type.isMarkedNullable || !it.hasNulls) && filter(it.typed()) }.map { it.typed<C>() }.toColumnSet()

    fun DataFrameBase<*>.all() = ColumnGroup(this.columns())

    fun DataFrameBase<*>.colGroups(filter: (MapColumn<*>) -> Boolean = { true }): ColumnSet<AnyRow> = this.columns().filter { it.isGroup() && filter(it.asGroup()) }.map { it.asGroup() }.toColumnSet()

    fun <C> ColumnSet<C>.filter(predicate: Predicate<DataColumn<C>>) = createColumnSet { resolve(it).filter { predicate(it.data) } }

    fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean = { true }) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().dfs().filter(predicate) } }

    fun <C> ColumnSet<C>.children(predicate: (AnyCol) -> Boolean = {true} ) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().filter { predicate(it.data) } } }

    fun MapColumnReference.children() = createColumnSet { resolve(it).single().children() }

    val cols: List<AnyCol> get() = columns()

    operator fun List<AnyCol>.get(range: IntRange) = ColumnGroup(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumnDef()

    fun <C> String.cast() = ColumnDefinition<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumnDef()

    fun <C> col(colName: String) = colName.cast<C>()
}
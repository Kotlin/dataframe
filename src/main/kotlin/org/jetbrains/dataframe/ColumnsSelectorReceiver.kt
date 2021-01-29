package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnSet
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.MapColumn
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface ColumnsSelectorReceiver<out T> : DataFrameBase<T> {

    fun cols(firstCol: AnyCol, vararg otherCols: AnyCol) = Columns(listOf(firstCol) + otherCols)

    fun DataFrameBase<*>.cols() = this.all()

    fun cols(firstCol: String, vararg otherCols: String) = Columns((listOf(firstCol) + otherCols).map { it.toColumnDef() })

    fun DataFrameBase<*>.cols(range: IntRange) = Columns(this.columns().subList(range.start, range.endInclusive+1))

    fun DataFrameBase<*>.group(name: String) = this.get(name) as MapColumn<*>

    fun DataFrameBase<*>.cols(predicate: (AnyCol) -> Boolean) = Columns(this.columns().filter(predicate))

    fun <C> DataFrameBase<*>.colsOfType(type: KType, filter: (DataColumn<C>) -> Boolean = { true }) = this.columns().filter { it.type.isSubtypeOf(type) && (type.isMarkedNullable || !it.hasNulls) && filter(it.typed()) }.map { it.typed<C>() }.toColumnSet()

    fun DataFrameBase<*>.all() = Columns(this.columns())

    fun DataFrameBase<*>.colGroups(filter: (MapColumn<*>) -> Boolean = { true }): ColumnSet<AnyRow> = this.columns().filter { it.isGroup() && filter(it.asGroup()) }.map { it.asGroup() }.toColumnSet()

    fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean = { true }) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().dfs().filter(predicate) } }

    fun <C> ColumnSet<C>.children(predicate: (AnyCol) -> Boolean = {true} ) = createColumnSet { resolve(it).filter { it.isGrouped() }.flatMap { it.children().filter { predicate(it.data) } } }

    fun MapColumnReference.children() = createColumnSet { resolve(it).single().children() }

    operator fun List<AnyCol>.get(range: IntRange) = Columns(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumnDef()

    fun <C> String.cast() = ColumnDefinition<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumnDef()

    fun <C> col(colName: String) = colName.cast<C>()

    fun <C> ColumnSet<C>.drop(n: Int) = transform { it.drop(n) }
    fun <C> ColumnSet<C>.take(n: Int) = transform { it.take(n) }
    fun <C> ColumnSet<C>.dropLast(n: Int) = transform { it.dropLast(n) }
    fun <C> ColumnSet<C>.takeLast(n: Int) = transform { it.takeLast(n) }
    fun <C> ColumnSet<C>.takeWhile(predicate: Predicate<ColumnWithPath<C>>) = transform { it.takeWhile(predicate) }
    fun <C> ColumnSet<C>.takeLastWhile(predicate: Predicate<ColumnWithPath<C>>) = transform { it.takeLastWhile(predicate) }
    fun <C> ColumnSet<C>.filter(predicate: Predicate<ColumnWithPath<C>>) = transform { it.filter(predicate) }
}
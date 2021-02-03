package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.api.columns.ColumnSet
import org.jetbrains.dataframe.api.columns.ColumnWithPath
import org.jetbrains.dataframe.api.columns.MapColumn
import org.jetbrains.dataframe.api.columns.isSubtypeOf
import kotlin.reflect.KProperty
import kotlin.reflect.KType

interface ColumnsSelectorReceiver<out T> : DataFrameBase<T> {

    fun DataFrameBase<*>.first(numCols: Int) = cols().take(numCols)

    fun DataFrameBase<*>.last(numCols: Int) = cols().takeLast(numCols)

    fun DataFrameBase<*>.group(name: String) = this.get(name) as MapColumn<*>

    fun <C> ColumnSet<*>.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>) = (listOf(firstCol) + otherCols).let { refs ->
        transform { it.flatMap { col -> refs.mapNotNull { col.getChild(it) } } }
    }

    fun ColumnSet<*>.cols(firstCol: String, vararg otherCols: String) = (listOf(firstCol) + otherCols).let { names ->
        transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
    }

    fun ColumnSet<*>.cols(range: IntRange) = transform { it.flatMap { it.children().subList(range.start, range.endInclusive+1) } }
    fun ColumnSet<*>.cols(predicate: (AnyCol) -> Boolean = {true}) = colsInternal(predicate)

    fun <C> ColumnSet<C>.dfs(predicate: (ColumnWithPath<*>) -> Boolean = {true}) = dfsInternal(predicate)

    fun DataFrameBase<*>.all() = Columns(this.columns())

    fun DataFrameBase<*>.colGroups(filter: (MapColumn<*>) -> Boolean = { true }): ColumnSet<AnyRow> = this.columns().filter { it.isGroup() && filter(it.asGroup()) }.map { it.asGroup() }.toColumnSet()

    fun <C> ColumnSet<C>.children(predicate: (AnyCol) -> Boolean = {true} ) = transform { it.flatMap { it.children().filter { predicate(it.data) } } }

    fun MapColumnReference.children() = transform { it.single().children() }

    operator fun List<AnyCol>.get(range: IntRange) = Columns(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumnDef()

    fun <C> String.cast() = ColumnDefinition<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumnDef()

    fun DataFrameBase<*>.col(index: Int) = column(index)
    fun ColumnSet<*>.col(index: Int) = transform { it.mapNotNull { it.getChild(index) } }

    fun DataFrameBase<*>.col(colName: String) = getColumn<Any?>(colName)
    fun ColumnSet<*>.col(colName: String) = transform { it.mapNotNull { it.getChild(colName) } }

    fun <C> ColumnSet<C>.drop(n: Int) = transform { it.drop(n) }
    fun <C> ColumnSet<C>.take(n: Int) = transform { it.take(n) }
    fun <C> ColumnSet<C>.dropLast(n: Int) = transform { it.dropLast(n) }
    fun <C> ColumnSet<C>.takeLast(n: Int) = transform { it.takeLast(n) }
    fun <C> ColumnSet<C>.takeWhile(predicate: Predicate<ColumnWithPath<C>>) = transform { it.takeWhile(predicate) }
    fun <C> ColumnSet<C>.takeLastWhile(predicate: Predicate<ColumnWithPath<C>>) = transform { it.takeLastWhile(predicate) }
    fun <C> ColumnSet<C>.filter(predicate: Predicate<ColumnWithPath<C>>) = transform { it.filter(predicate) }

    fun <C> DataColumn<C>.rename(newName: String) = (this as ColumnReference<C>).rename(newName)
}

internal fun ColumnSet<*>.colsInternal(predicate: (AnyCol) -> Boolean) = transform { it.flatMap { it.children().filter { predicate(it.data) } } }
internal fun ColumnSet<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean) = transform { it.filter { it.isGroup() }.flatMap { it.children().dfs().filter(predicate) } }

fun <C> ColumnSet<*>.dfsOf(type: KType, predicate: (ColumnWithPath<C>) -> Boolean = { true }) = dfsInternal { it.data.isSubtypeOf(type) && predicate(it.typed()) }
inline fun <reified C> ColumnSet<*>.dfsOf(noinline filter: (ColumnWithPath<C>) -> Boolean = { true }) = dfsOf(getType<C>(), filter)

fun <C> ColumnSet<*>.colsOf(type: KType, filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<C> = colsInternal { it.isSubtypeOf(type) && filter(it.typed()) } as ColumnSet<C>
inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }) = colsOf(getType<C>(), filter)

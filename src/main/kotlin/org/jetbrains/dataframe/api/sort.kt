package org.jetbrains.dataframe

import kotlin.reflect.KProperty
import kotlin.reflect.full.isSubtypeOf

fun <T> DataFrame<T>.sortBy(columns: List<SortColumnDescriptor>): DataFrame<T> {

    val compChain = columns.map {
        val column = this[it.column]
        when (it.direction) {
            SortDirection.Asc -> column.createComparator(it.nullsLast)
            SortDirection.Desc -> column.createComparator(it.nullsLast).reversed()
        }
    }.reduce { a, b -> a.then(b) }

    val permutation = (0 until nrow).sortedWith(compChain)

    return getRows(permutation)
}

fun <T> DataFrame<T>.sortBy(columns: Iterable<ColumnDef<Comparable<*>?>>) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Asc) })
fun <T> DataFrame<T>.sortBy(vararg columns: ColumnDef<Comparable<*>?>) = sortBy(columns.toList())
fun <T> DataFrame<T>.sortBy(vararg columns: String) = sortBy(getColumns(columns) as List<ColumnDef<Comparable<*>>>)
fun <T> DataFrame<T>.sortBy(vararg columns: KProperty<Comparable<*>?>) = sortBy(getColumns(columns))
fun <T> DataFrame<T>.sortBy(selector: SortColumnSelector<T, Comparable<*>?>) = sortBy(getSortColumns(selector))

internal fun DataCol.createComparator(nullsLast: Boolean): java.util.Comparator<Int> {

    if (!type.isSubtypeOf(getType<Comparable<*>?>()))
        throw UnsupportedOperationException()

    return Comparator<Any?> { left, right ->
        (left as Comparable<Any?>).compareTo(right)
    }.let { if (nullsLast) nullsLast(it) else nullsFirst(it) }
            .let { Comparator { left, right -> it.compare(get(left), get(right)) } }
}

class DataFrameWithColumnsForSortImpl<T>(df: DataFrame<T>) : DataFrame<T> by df, DataFrameWithColumnsForSort<T>typealias SortColumnSelector<T, C> = DataFrameWithColumnsForSort<T>.(DataFrameWithColumnsForSort<T>) -> ColumnSet<C>

internal fun <C> ColumnSet<C>.extractSortColumns(): List<SortColumnDescriptor> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractSortColumns() }
    is ReversedColumn -> column.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction.reversed(), it.nullsLast) }
    is ColumnDef<C> -> listOf(SortColumnDescriptor(this, SortDirection.Asc))
    is NullsLast<C> -> columns.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction, nullsLast = true) }
    else -> throw Exception()
}

internal fun <T, C> DataFrame<T>.getSortColumns(selector: SortColumnSelector<T, C>) = DataFrameWithColumnsForSortImpl(this).let { selector(it, it).extractSortColumns() }
enum class SortDirection { Asc, Desc }

fun SortDirection.reversed() = when (this) {
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}

class SortColumnDescriptor(val column: Column, val direction: SortDirection, val nullsLast: Boolean = false)
interface DataFrameWithColumnsForSort<out T> : DataFrameWithColumns<T> {

    val <C> ColumnDef<C>.desc: ColumnSet<C> get() = ReversedColumn(this)
    val String.desc: ColumnSet<Comparable<*>?> get() = ReversedColumn(cast<Comparable<*>>())
    val <C> KProperty<C>.desc: ColumnSet<C> get() = ReversedColumn(toColumnName())

    val <C> ColumnSet<C?>.nullsLast: ColumnSet<C?> get() = NullsLast(this)
    val String.nullsLast: ColumnSet<Comparable<*>?> get() = NullsLast(cast<Comparable<*>>())
    val <C> KProperty<C?>.nullsLast: ColumnSet<C?> get() = NullsLast(toColumnName())

    infix fun <C> ColumnSet<C>.then(other: ColumnSet<C>) = ColumnGroup(this, other)
    infix fun <C> ColumnSet<C>.then(other: String) = this then other.toColumnDef()
    infix fun <C> ColumnSet<C>.then(other: KProperty<*>) = this then other.toColumnName()
    infix fun <C> KProperty<C>.then(other: ColumnSet<C>) = toColumnName() then other
    infix fun KProperty<*>.then(other: KProperty<*>) = toColumnName() then other.toColumnName()
    infix fun <C> String.then(other: ColumnSet<C>) = toColumnDef() then other
    infix fun String.then(other: String) = toColumnDef() then other.toColumnDef()
}

fun <T> DataFrame<T>.sortByDesc(selector: SortColumnSelector<T, Comparable<*>?>) = sortBy(getSortColumns(selector).map { SortColumnDescriptor(it.column, SortDirection.Desc) })
fun <T> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<*>?>) = sortByDesc(getColumns(columns))
fun <T> DataFrame<T>.sortByDesc(vararg columns: String) = sortByDesc(getColumns(columns) as List<ColumnDef<Comparable<*>>>)
fun <T> DataFrame<T>.sortByDesc(vararg columns: ColumnDef<Comparable<*>?>) = sortByDesc(columns.toList())
fun <T> DataFrame<T>.sortByDesc(columns: Iterable<ColumnDef<Comparable<*>?>>) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Desc) })
fun <T,G> GroupedDataFrame<T, G>.sortBy(vararg columns: String) = sortBy(columns.map { SortColumnDescriptor(it.toColumnDef(), SortDirection.Desc) })
fun <T,G> GroupedDataFrame<T, G>.sortBy(vararg columns: ColumnDef<Comparable<*>?>) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Desc) })
fun <T,G> GroupedDataFrame<T, G>.sortBy(selector: SortColumnSelector<G, Comparable<*>?>) = sortBy(getSortColumns(selector))
fun <T,G> GroupedDataFrame<T, G>.sortKeysBy(selector: SortColumnSelector<T, Comparable<*>?>) = sortBy(asPlain().getSortColumns(selector))
fun <T,G> GroupedDataFrame<T, G>.sortBy(columns: List<SortColumnDescriptor>): GroupedDataFrame<T, G> {

    val keyColumns = columns.filter { keys.tryGetColumn(it.column) != null }
    val groupColumns = columns.filter { keys.tryGetColumn(it.column) == null }
    var result = asPlain()
    if(!groupColumns.isEmpty())
        result = result.update { groups }.with { it.sortBy(groupColumns) }
    if(!keyColumns.isEmpty())
        result = result.sortBy(keyColumns)
    return result.toGrouped { groups }
}

internal fun <T, G> GroupedDataFrame<T, G>.getSortColumns(selector: SortColumnSelector<G, Comparable<*>?>) =
        DataFrameWithColumnsForSortImpl(groups.values.first())
                .let { selector(it, it).extractSortColumns() }
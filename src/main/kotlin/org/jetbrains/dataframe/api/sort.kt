package org.jetbrains.dataframe

import java.lang.Exception
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface SortReceiver<out T> : ColumnsSelectorReceiver<T> {

    val <C> ColumnSet<C>.desc: ColumnSet<C> get() = addFlag(SortFlag.Reversed)
    val String.desc: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().desc
    val <C> KProperty<C>.desc: ColumnSet<C> get() = toColumnDef().desc

    val <C> ColumnSet<C?>.nullsLast: ColumnSet<C?> get() = addFlag(SortFlag.NullsLast)
    val String.nullsLast: ColumnSet<Comparable<*>?> get() = cast<Comparable<*>>().nullsLast
    val <C> KProperty<C?>.nullsLast: ColumnSet<C?> get() = toColumnDef().nullsLast

    infix fun <C> ColumnSet<C>.then(other: ColumnSet<C>) = ColumnGroup(this, other)
    infix fun <C> ColumnSet<C>.then(other: String) = this then other.toColumnDef()
    infix fun <C> ColumnSet<C>.then(other: KProperty<*>) = this then other.toColumnDef()
    infix fun <C> KProperty<C>.then(other: ColumnSet<C>) = toColumnDef() then other
    infix fun KProperty<*>.then(other: KProperty<*>) = toColumnDef() then other.toColumnDef()
    infix fun <C> String.then(other: ColumnSet<C>) = toColumnDef() then other
    infix fun String.then(other: String) = toColumnDef() then other.toColumnDef()
}

typealias SortColumnsSelector<T, C> = Selector<SortReceiver<T>, ColumnSet<C>>

fun <T> DataFrame<T>.sortBy(selector: SortColumnsSelector<T, Comparable<*>?>) = doSortBy(selector, UnresolvedColumnsPolicy.Fail)
fun <T> DataFrame<T>.sortBy(cols: Iterable<ColumnDef<Comparable<*>?>>) = sortBy { cols.toColumns() }
fun <T> DataFrame<T>.sortBy(vararg cols: ColumnDef<Comparable<*>?>) = sortBy { cols.toColumns() }
fun <T> DataFrame<T>.sortBy(vararg cols: String) = sortBy { cols.toColumns() as ColumnSet<Comparable<*>?> }
fun <T> DataFrame<T>.sortBy(vararg cols: KProperty<Comparable<*>?>) = sortBy { cols.toColumns() }

fun <T> DataFrame<T>.sortByDesc(selector: SortColumnsSelector<T, Comparable<*>?>): DataFrame<T> {
    val set = selector.toColumns()
    return doSortBy({ set.desc })
}

fun <T> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<*>?>) = sortByDesc { columns.toColumns() }
fun <T> DataFrame<T>.sortByDesc(vararg columns: String) = sortByDesc { columns.toColumns() as ColumnSet<Comparable<*>?> }
fun <T> DataFrame<T>.sortByDesc(vararg columns: ColumnDef<Comparable<*>?>) = sortByDesc { columns.toColumns() }
fun <T> DataFrame<T>.sortByDesc(columns: Iterable<ColumnDef<Comparable<*>?>>) = sortByDesc { columns.toColumns() }

fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: String) = sortBy { cols.toColumns() as ColumnSet<Comparable<*>?> }
fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: ColumnDef<Comparable<*>?>) = sortBy { cols.toColumns() }
fun <T, G> GroupedDataFrame<T, G>.sortBy(vararg cols: KProperty<Comparable<*>?>) = sortBy { cols.toColumns() }
fun <T, G> GroupedDataFrame<T, G>.sortBy(selector: SortColumnsSelector<G, Comparable<*>?>) = doSortBy(selector)

internal fun <T, C> DataFrame<T>.doSortBy(selector: SortColumnsSelector<T, C>, unresolvedColumnsPolicy: UnresolvedColumnsPolicy = UnresolvedColumnsPolicy.Fail): DataFrame<T> {

    val columns = extractSortColumns(selector, unresolvedColumnsPolicy)

    val compChain = columns.map {
        when (it.direction) {
            SortDirection.Asc -> it.column.createComparator(it.nullsLast)
            SortDirection.Desc -> it.column.createComparator(it.nullsLast).reversed()
        }
    }.reduce { a, b -> a.then(b) }

    val permutation = (0 until nrow).sortedWith(compChain)

    return this[permutation]
}

internal fun DataCol.createComparator(nullsLast: Boolean): java.util.Comparator<Int> {

    if (!type.isSubtypeOf(getType<Comparable<*>?>()))
        throw UnsupportedOperationException()

    return Comparator<Any?> { left, right ->
        (left as Comparable<Any?>).compareTo(right)
    }.let { if (nullsLast) nullsLast(it) else nullsFirst(it) }
            .let { Comparator { left, right -> it.compare(get(left), get(right)) } }
}

internal open class MissingValueColumn<T> : ColumnData<T>{

    override val name: String
        get() = throw UnsupportedOperationException()
    override val values: Iterable<T>
        get() = throw UnsupportedOperationException()
    override val ndistinct: Int
        get() = throw UnsupportedOperationException()
    override val type: KType
        get() = throw UnsupportedOperationException()
    override val size: Int
        get() = throw UnsupportedOperationException()

    override fun kind() = throw UnsupportedOperationException()

    override fun get(index: Int) = throw UnsupportedOperationException()

    override fun defaultValue() = throw UnsupportedOperationException()

    override fun get(range: IntRange) = throw UnsupportedOperationException()

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override fun get(indices: Iterable<Int>) = throw UnsupportedOperationException()

    override fun get(mask: BooleanArray) = throw UnsupportedOperationException()

    override fun distinct(): ColumnData<T> = throw UnsupportedOperationException()

    override fun toSet() = throw UnsupportedOperationException()

    override fun resolve(context: ColumnResolutionContext) = emptyList<ColumnWithPath<T>>()
}

internal class MissingGroupColumn<T> : MissingValueColumn<DataFrameRow<T>>(), GroupedColumn<T> {

    override fun <R> get(column: ColumnDef<R>) = MissingValueColumn<R>()

    override fun <R> get(column: ColumnDef<DataFrameRow<R>>) = MissingGroupColumn<R>()

    override fun <R> get(column: ColumnDef<DataFrame<R>>) = MissingTableColumn<R>()

    override val df: DataFrame<T>
        get() = throw UnsupportedOperationException()

    override fun tryGetColumn(columnName: String): DataCol? {
        return null
    }

    override fun getColumn(columnIndex: Int): DataCol {
        return MissingValueColumn<Any?>()
    }

    override fun columns(): List<DataCol> {
        return emptyList()
    }

    override val ncol: Int
        get() = 0

}

internal class MissingTableColumn<T>: MissingValueColumn<DataFrame<T>>(), TableColumn<T> {
    override val df: DataFrame<T>
        get() = throw UnsupportedOperationException()
}

internal open class DataFrameReceiver<T>(val source: DataFrameBase<T>, private val allowMissingColumns: Boolean): DataFrameBase<T> by source {

    override fun getColumn(columnIndex: Int): DataCol {
        if(allowMissingColumns && columnIndex < 0 || columnIndex >= ncol) return MissingValueColumn<Any?>()
        return source.getColumn(columnIndex)
    }

    override operator fun get(columnName: String) = getColumnChecked(columnName) ?: MissingValueColumn<Any?>()

    fun <R> getColumnChecked(columnName: String): ColumnData<R>? {
        val col = source.tryGetColumn(columnName)
        if(col == null) {
            if(allowMissingColumns) return null
            throw Exception("Column not found: '$columnName'")
        }
        if(col.isGrouped()) {
            return GroupedColumnWithParent(null, col.asGrouped()) as ColumnData<R>
        }
        return col as ColumnData<R>
    }

    override operator fun <R> get(column: ColumnDef<R>): ColumnData<R> = getColumnChecked(column.name) ?: MissingValueColumn()
    override operator fun <R> get(column: ColumnDef<DataFrameRow<R>>): GroupedColumn<R> = (getColumnChecked(column.name) ?: MissingGroupColumn<R>()) as GroupedColumn<R>
    override operator fun <R> get(column: ColumnDef<DataFrame<R>>): TableColumn<R> = (getColumnChecked(column.name) ?: MissingTableColumn<R>()) as TableColumn<R>
}

internal class SortReceiverImpl<T>(df: DataFrameBase<T>, allowMissingColumns: Boolean) : DataFrameReceiver<T>(df, allowMissingColumns), SortReceiver<T>

internal fun <T, C> DataFrame<T>.extractSortColumns(selector: SortColumnsSelector<T, C>, unresolvedColumnsPolicy: UnresolvedColumnsPolicy): List<SortColumnDescriptor<*>> {
    return selector.toColumns().resolve(ColumnResolutionContext(this, unresolvedColumnsPolicy))
            .map {
                when (val col = it.data) {
                    is SortColumnDescriptor<*> -> col
                    else -> SortColumnDescriptor(col)
                }
            }
}

enum class SortDirection { Asc, Desc }

fun SortDirection.reversed() = when (this) {
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}

class SortColumnDescriptor<C>(val column: ColumnData<C>, val direction: SortDirection = SortDirection.Asc, val nullsLast: Boolean = false) : ColumnData<C> by column

internal fun <T, G> GroupedDataFrame<T, G>.doSortBy(selector: SortColumnsSelector<G, *>): GroupedDataFrame<T, G> {

    return asPlain()
            .update { groups }
            .with { it.doSortBy(selector, UnresolvedColumnsPolicy.Skip) }
            .doSortBy(selector as SortColumnsSelector<T, *>, UnresolvedColumnsPolicy.Skip)
            .toGrouped { it.getTable(groups.name).typed() }
}

internal enum class SortFlag { Reversed, NullsLast }

internal fun <C> ColumnSet<C>.addFlag(flag: SortFlag) = ColumnsWithSortFlag(this, flag)

internal fun <C> ColumnWithPath<C>.addFlag(flag: SortFlag): ColumnWithPath<C> {
    val col = data
    return when (col) {
        is SortColumnDescriptor -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col.column, col.direction.reversed(), col.nullsLast)
                SortFlag.NullsLast -> SortColumnDescriptor(col.column, col.direction, true)
            }
        }
        else -> {
            when (flag) {
                SortFlag.Reversed -> SortColumnDescriptor(col, SortDirection.Desc)
                SortFlag.NullsLast -> SortColumnDescriptor(col, SortDirection.Asc, true)
            }
        }
    }.addPath(path)
}

internal class ColumnsWithSortFlag<C>(val column: ColumnSet<C>, val flag: SortFlag) : ColumnSet<C> {
    override fun resolve(context: ColumnResolutionContext) = column.resolve(context).map { it.addFlag(flag) }
}
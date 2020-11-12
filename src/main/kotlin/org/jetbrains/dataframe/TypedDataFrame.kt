package org.jetbrains.dataframe

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

interface TypedDataFrameWithColumns<out T> : TypedDataFrame<T> {

    fun cols(vararg col: DataCol) = ColumnGroup(col.toList())

    fun cols(vararg col: String) = ColumnGroup(col.map { it.toColumn() })

    fun cols(predicate: (DataCol) -> Boolean) = ColumnGroup(columns.filter(predicate))

    val cols: List<DataCol> get() = columns

    operator fun List<DataCol>.get(range: IntRange) = ColumnGroup(subList(range.first, range.last + 1))

    operator fun String.invoke() = toColumn()

    fun <C> String.cast() = NamedColumnImpl<C>(this)

    fun <C> col(property: KProperty<C>) = property.toColumn()

    fun <C> col(colName: String) = colName.cast<C>()
}

interface TypedDataFrameWithColumnsForSelect<out T> : TypedDataFrameWithColumns<T> {

    val allColumns: ColumnGroup<*>

    infix fun <C> ColumnSet<C>.and(other: ColumnSet<C>) = ColumnGroup(this, other)

    operator fun <C> ColumnData<C>.invoke(newName: String) = rename(newName)

    operator fun <C> ColumnDef<C>.invoke(newName: String) = rename(newName)

    infix fun String.and(other: String) = toColumn() and other.toColumn()

    infix fun <C> String.and(other: ColumnSet<C>) = toColumn() and other

    infix fun <C> KProperty<*>.and(other: ColumnSet<C>) = toColumnName() and other
    infix fun <C> ColumnSet<C>.and(other: KProperty<C>) = this and other.toColumnName()
    infix fun KProperty<*>.and(other: KProperty<*>) = toColumnName() and other.toColumnName()

    infix fun <C> ColumnSet<C>.and(other: String) = this and other.toColumn()
    operator fun <C> ColumnSet<C>.plus(other: ColumnSet<C>) = this and other
}

interface TypedDataFrameWithColumnsForSort<out T> : TypedDataFrameWithColumns<T> {

    val <C> ColumnDef<C>.desc: ColumnSet<C> get() = ReversedColumn(this)
    val String.desc: ColumnSet<Comparable<*>?> get() = ReversedColumn(cast<Comparable<*>>())
    val <C> KProperty<C>.desc: ColumnSet<C> get() = ReversedColumn(toColumnName())

    val <C> ColumnSet<C?>.nullsLast: ColumnSet<C?> get() = NullsLast(this)
    val String.nullsLast: ColumnSet<Comparable<*>?> get() = NullsLast(cast<Comparable<*>>())
    val <C> KProperty<C?>.nullsLast: ColumnSet<C?> get() = NullsLast(toColumnName())

    infix fun <C> ColumnSet<C>.then(other: ColumnSet<C>) = ColumnGroup(this, other)
    infix fun <C> ColumnSet<C>.then(other: String) = this then other.toColumn()
    infix fun <C> ColumnSet<C>.then(other: KProperty<*>) = this then other.toColumnName()
    infix fun <C> KProperty<C>.then(other: ColumnSet<C>) = toColumnName() then other
    infix fun KProperty<*>.then(other: KProperty<*>) = toColumnName() then other.toColumnName()
    infix fun <C> String.then(other: ColumnSet<C>) = toColumn() then other
    infix fun String.then(other: String) = toColumn() then other.toColumn()
}

interface TypedDataFrameForSpread<out T> : TypedDataFrame<T> {

    fun <C, R> ColumnDef<C>.map(transform: (C) -> R): ColumnData<R> = get(this).let { ConvertedColumnImpl(it, it.mapValues(transform)) }
}

open class TypedDataFrameWithColumnsForSelectImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumnsForSelect<T> {

    override val allColumns get() = ColumnGroup(columns)
}

class TypedDataFrameForSpreadImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameForSpread<T>

class TypedDataFrameWithColumnsForSortImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumnsForSort<T>

data class DataFrameSize(val ncol: Int, val nrow: Int) {
    override fun toString() = "$nrow x $ncol"
}

typealias DataFrameSelector<T, R> = TypedDataFrame<T>.(TypedDataFrame<T>) -> R

typealias ColumnsSelector<T, C> = TypedDataFrameWithColumnsForSelect<T>.(TypedDataFrameWithColumnsForSelect<T>) -> ColumnSet<C>

typealias ColumnSelector<T, C> = TypedDataFrameWithColumnsForSelect<T>.(TypedDataFrameWithColumnsForSelect<T>) -> ColumnDef<C>

typealias SpreadColumnSelector<T, C> = TypedDataFrameForSpread<T>.(TypedDataFrameForSpread<T>) -> ColumnDef<C>

typealias SortColumnSelector<T, C> = TypedDataFrameWithColumnsForSort<T>.(TypedDataFrameWithColumnsForSort<T>) -> ColumnSet<C>

internal fun <T, C> TypedDataFrame<T>.extractColumns(set: ColumnSet<C>): List<ColumnDef<C>> = when (set) {
    is ColumnGroup<C> -> set.columns.flatMap { extractColumns(it) }
    is ColumnDef<C> -> listOf(set)
    else -> throw Exception()
}

internal fun <C> ColumnSet<C>.extractSortColumns(): List<SortColumnDescriptor> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractSortColumns() }
    is ReversedColumn -> column.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction.reversed(), it.nullsLast) }
    is ColumnDef<C> -> listOf(SortColumnDescriptor(this, SortDirection.Asc))
    is NullsLast<C> -> column.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction, nullsLast = true) }
    else -> throw Exception()
}

internal fun <T, C> TypedDataFrame<T>.getColumns(selector: ColumnsSelector<T, C>) = TypedDataFrameWithColumnsForSelectImpl(this).let { extractColumns(selector(it, it)) }

internal fun <T, C> TypedDataFrame<T>.getColumnsWithData(selector: ColumnsSelector<T, C>) = getColumns(selector).map {this[it]}

internal fun <T, C> TypedDataFrame<T>.getGroupColumns(selector: ColumnsSelector<T, TypedDataFrameRow<C>>) = getColumnsWithData(selector).map {it as GroupedColumn<C> }

internal fun <T, C> TypedDataFrame<T>.getColumn(selector: ColumnSelector<T, C>) = TypedDataFrameWithColumnsForSelectImpl(this).let { selector(it, it) }.let { this[it] }

@JvmName("getColumnForSpread")
internal fun <T, C> TypedDataFrame<T>.getColumn(selector: SpreadColumnSelector<T, C>) = TypedDataFrameForSpreadImpl(this).let { selector(it, it) }.let {
    when (it) {
        is ConvertedColumn<C> -> it
        else -> this[it]
    }
}

internal fun <T, C> TypedDataFrame<T>.getSortColumns(selector: SortColumnSelector<T, C>) = TypedDataFrameWithColumnsForSortImpl(this).let { selector(it, it).extractSortColumns() }

internal fun <T> TypedDataFrame<T>.getColumns(columnNames: Array<out String>) = columnNames.map { this[it] }

internal fun <T, C> TypedDataFrame<T>.getColumns(columnNames: Array<out KProperty<C>>) = columnNames.map { this[it.name] as ColumnDef<C> }

internal fun <T> TypedDataFrame<T>.getColumns(columnNames: List<String>): List<DataCol> = columnNames.map { this[it] }

enum class SortDirection { Asc, Desc }

fun SortDirection.reversed() = when (this) {
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}

class SortColumnDescriptor(val column: Column, val direction: SortDirection, val nullsLast: Boolean = false)

internal fun <T> TypedDataFrame<T>.new(columns: Iterable<DataCol>) = dataFrameOf(columns).typed<T>()

interface DataFrameBase<out T> {

    operator fun get(columnName: String): ColumnData<*>
    operator fun <R> get(column: ColumnDef<R>): ColumnData<R>
    operator fun <R> get(column: ColumnDef<TypedDataFrameRow<R>>): GroupedColumn<R>
    val ncol: Int
}

interface TypedDataFrame<out T> : DataFrameBase<T> {

    companion object

    val nrow: Int
    override val ncol: Int get() = columns.size
    val columns: List<DataCol>
    val rows: Iterable<TypedDataFrameRow<T>>

    fun columnNames() = columns.map { it.name }

    operator fun get(index: Int): TypedDataFrameRow<T> = TypedDataFrameRowImpl(index, this)
    override operator fun get(columnName: String) = tryGetColumn(columnName) ?: throw Exception("Column not found") // TODO
    override operator fun <R> get(column: ColumnDef<R>): ColumnData<R> = tryGetColumn(column)!!
    override operator fun <R> get(column: ColumnDef<TypedDataFrameRow<R>>): GroupedColumn<R> = get<TypedDataFrameRow<R>>(column) as GroupedColumn<R>

    operator fun <R> get(property: KProperty<R>) = get(property.name) as ColumnData<R>
    operator fun get(indices: Iterable<Int>) = getRows(indices)
    operator fun get(mask: BooleanArray) = getRows(mask)
    operator fun get(range: IntRange) = getRows(range)

    operator fun plus(col: DataCol) = dataFrameOf(columns + col).typed<T>()
    operator fun plus(col: Iterable<DataCol>) = new(columns + col)
    operator fun plus(stub: AddRowNumberStub) = addRowNumber(stub.columnName)

    fun getRows(indices: Iterable<Int>): TypedDataFrame<T>
    fun getRows(mask: BooleanArray): TypedDataFrame<T>
    fun getRows(range: IntRange): TypedDataFrame<T>

    fun getColumnIndex(name: String): Int
    fun getColumnIndex(col: DataCol) = getColumnIndex(col.name)

    fun <R> tryGetColumn(column: ColumnDef<R>): ColumnData<R>? = when(column) {
        is RenamedColumnDef<R> -> tryGetColumn(column.source)?.doRename(column.name)
        is ColumnWithParent<*> -> (tryGetColumn(column.parent) as? GroupedColumn<*>)?.get(column.name) as? ColumnData<R>
        else -> tryGetColumn(column.name) as? ColumnData<R>
    }
    fun tryGetColumn(name: String) = getColumnIndex(name).let { if (it != -1) columns[it] else null }

    fun tryGetColumnGroup(name: String) = tryGetColumn(name) as? GroupedColumn<*>
    fun getColumnGroup(name: String) = tryGetColumnGroup(name)!!

    fun <C> select(columns: Iterable<ColumnDef<C>>) = new(columns.map { this[it] })
    fun select(vararg columns: Column) = select(columns.toList())
    fun select(vararg columns: String) = select(getColumns(columns))
    fun select(vararg columns: KProperty<*>) = select(getColumns(columns))
    fun <C> select(selector: ColumnsSelector<T, C>) = select(getColumns(selector))
    operator fun get(col1: Column, col2: Column, vararg other: Column) = select(listOf(col1, col2) + other)
    operator fun get(col1: String, col2: String, vararg other: String) = select(getColumns(listOf(col1, col2) + other))

    fun sortBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T>
    fun sortBy(columns: Iterable<ColumnDef<Comparable<*>?>>) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Asc) })
    fun sortBy(vararg columns: ColumnDef<Comparable<*>?>) = sortBy(columns.toList())
    fun sortBy(vararg columns: String) = sortBy(getColumns(columns) as List<ColumnDef<Comparable<*>>>)
    fun sortBy(vararg columns: KProperty<Comparable<*>?>) = sortBy(getColumns(columns))
    fun sortBy(selector: SortColumnSelector<T, Comparable<*>?>) = sortBy(getSortColumns(selector))

    fun sortByDesc(columns: Iterable<ColumnDef<Comparable<*>?>>) = sortBy(columns.map { SortColumnDescriptor(it, SortDirection.Desc) })
    fun sortByDesc(vararg columns: ColumnDef<Comparable<*>?>) = sortByDesc(columns.toList())
    fun sortByDesc(vararg columns: String) = sortByDesc(getColumns(columns) as List<ColumnDef<Comparable<*>>>)
    fun sortByDesc(vararg columns: KProperty<Comparable<*>?>) = sortByDesc(getColumns(columns))
    fun sortByDesc(selector: SortColumnSelector<T, Comparable<*>?>) = sortBy(getSortColumns(selector).map { SortColumnDescriptor(it.column, SortDirection.Desc) })

    fun remove(cols: Iterable<Column>) = cols.map { it.name }.toSet().let { exclude -> new(columns.filter { !exclude.contains(it.name) }) }
    fun remove(vararg cols: Column) = remove(cols.toList())
    fun remove(vararg columns: String) = remove(getColumns(columns))
    fun remove(vararg columns: KProperty<*>) = remove(getColumns(columns))
    fun remove(selector: ColumnsSelector<T, *>) = remove(getColumns(selector))

    infix operator fun minus(cols: ColumnsSelector<T, *>) = remove(cols)
    infix operator fun minus(cols: Iterable<Column>) = remove(cols)
    infix operator fun minus(column: Column) = remove(column)
    infix operator fun minus(column: String) = remove(column)

    fun groupBy(cols: Iterable<Column>): GroupedDataFrame<T>
    fun groupBy(vararg cols: Column) = groupBy(cols.toList())
    fun groupBy(vararg cols: String) = groupBy(getColumns(cols))
    fun groupBy(vararg cols: KProperty<*>) = groupBy(getColumns(cols))
    fun groupBy(cols: ColumnsSelector<T, *>) = groupBy(getColumns(cols))

    fun addRow(vararg values: Any?): TypedDataFrame<T>
    fun filter(predicate: RowFilter<T>): TypedDataFrame<T>

    fun nullToZero(cols: Iterable<ColumnDef<Number?>>) = cols.fold(this) { df, col -> df.nullColumnToZero(df[col] as ColumnDef<Number?>) }
    fun nullToZero(vararg cols: ColumnDef<Number?>) = nullToZero(cols.toList())
    fun nullToZero(vararg cols: String) = nullToZero(getColumns(cols) as List<ColumnDef<Number?>>)
    fun nullToZero(cols: ColumnsSelector<T, Number?>) = nullToZero(getColumns(cols))

    fun filterNotNull(cols: Iterable<Column>) = filter { cols.all { col -> this[col.name] != null } }
    fun filterNotNull(vararg cols: Column) = filterNotNull(cols.toList())
    fun filterNotNull(vararg cols: String) = filterNotNull(getColumns(cols))
    fun filterNotNull(vararg cols: KProperty<*>) = filterNotNull(getColumns(cols))
    fun filterNotNull(cols: ColumnsSelector<T, *>) = filterNotNull(getColumns(cols))

    fun filterNotNullAny(cols: Iterable<Column>) = filter { cols.any { col -> this[col.name] != null } }
    fun filterNotNullAny(vararg cols: Column) = filterNotNullAny(cols.toList())
    fun filterNotNullAny(vararg cols: String) = filterNotNullAny(getColumns(cols))
    fun filterNotNullAny(vararg cols: KProperty<*>) = filterNotNullAny(getColumns(cols))
    fun filterNotNullAny(cols: ColumnsSelector<T, *>) = filterNotNullAny(getColumns(cols))

    fun filterNotNullAny() = filter { values.any { it != null } }
    fun filterNotNull() = filter { values.all { it != null } }

    fun <D : Comparable<D>> min(selector: RowSelector<T, D?>): D? = rows.asSequence().map { selector(it, it) }.filterNotNull().min()
    fun <D : Comparable<D>> min(col: ColumnDef<D?>): D? = get(col).values.asSequence().filterNotNull().min()

    fun <D : Comparable<D>> max(selector: RowSelector<T, D?>): D? = rows.asSequence().map { selector(it, it) }.filterNotNull().max()
    fun <D : Comparable<D>> max(col: ColumnDef<D?>): D? = get(col).values.asSequence().filterNotNull().max()

    fun <D : Comparable<D>> maxBy(selector: RowSelector<T, D>) = rows.maxBy { selector(it, it) }
    fun <D : Comparable<D>> maxBy(col: ColumnDef<D>) = rows.maxBy { col(it) }
    fun <D : Comparable<D>> maxBy(col: String) = rows.maxBy { it[col] as D }

    fun <D : Comparable<D>> minBy(selector: RowSelector<T, D>) = rows.minBy { selector(it, it) }
    fun <D : Comparable<D>> minBy(col: ColumnDef<D>) = rows.minBy { col(it) }
    fun <D : Comparable<D>> minBy(col: String) = rows.minBy { it[col] as D }

    fun insertCol(index: Int, col: ColumnData<*>) = (columns.subList(0, index) + listOf(col) + columns.subList(index, columns.size)).asDataFrame<T>()

    fun all(predicate: RowFilter<T>): Boolean = rows.all { predicate(it, it) }
    fun any(predicate: RowFilter<T>): Boolean = rows.any { predicate(it, it) }

    fun count(predicate: RowFilter<T>) = rows.count { predicate(it, it) }

    fun first() = rows.first()
    fun firstOrNull() = rows.firstOrNull()
    fun last() = rows.last() // TODO: optimize (don't iterate through the whole data frame)
    fun lastOrNull() = rows.lastOrNull()
    fun take(numRows: Int) = getRows(0 until numRows)
    fun drop(numRows: Int) = getRows(numRows until nrow)
    fun takeLast(numRows: Int) = getRows(nrow - numRows until nrow)
    fun skipLast(numRows: Int) = getRows(0 until nrow - numRows)
    fun head(numRows: Int = 5) = take(numRows)
    fun tail(numRows: Int = 5) = takeLast(numRows)
    fun reorder(permutation: List<Int>) = columns.map { it.reorder(permutation) }.let { dataFrameOf(it).typed<T>() }
    fun shuffled() = reorder((0 until nrow).shuffled())
    fun <K, V> associate(transform: RowSelector<T, Pair<K, V>>) = rows.associate { transform(it, it) }
    fun <V> associateBy(transform: RowSelector<T, V>) = rows.associateBy { transform(it, it) }
    fun <R> distinctBy(selector: RowSelector<T, R>) = rows.distinctBy { selector(it, it) }.map { it.index }.let { getRows(it) }
    fun distinct() = distinctBy { it.values }
    fun single() = rows.single()

    fun <R> map(selector: RowSelector<T, R>) = rows.map { selector(it, it) }

    fun <C> forEach(selector: ColumnsSelector<T, C>, action: (TypedDataFrameRow<T>, ColumnData<C>) -> Unit) = getColumnsWithData(selector).let { cols ->
        rows.forEach { row ->
            cols.forEach { col ->
                action(row, col)
            }
        }
    }

    val size get() = DataFrameSize(ncol, nrow)
}



internal class TypedDataFrameImpl<T>(override val columns: List<DataCol>) : TypedDataFrame<T> {

    override val nrow: Int

    init {

        nrow = columns.firstOrNull()?.size ?: 0

        val invalidSizeColumns = columns.filter { it.size != nrow }
        if (invalidSizeColumns.size > 0)
            throw Exception("Invalid column sizes: ${invalidSizeColumns}") // TODO

        val columnNames = columns.groupBy { it.name }.filter { it.value.size > 1 }.map { it.key }
        if (columnNames.size > 0)
            throw Exception("Duplicate column names: ${columnNames}. All column names: ${columnNames()}")
    }


    private val columnsMap by lazy { columns.withIndex().associateBy({ it.value.name }, { it.index }) }

    override val rows = object : Iterable<TypedDataFrameRow<T>> {
        override fun iterator() =

                object : Iterator<TypedDataFrameRow<T>> {
                    var curRow = 0

                    override fun hasNext(): Boolean = curRow < nrow

                    override fun next() = get(curRow++)!!
                }
    }

    override fun filter(predicate: RowFilter<T>): TypedDataFrame<T> =
            (0 until nrow).filter {
                val row = get(it)
                predicate(row, row)
            }.let { get(it) }

    override fun groupBy(cols: Iterable<Column>): GroupedDataFrame<T> {

        val columns = cols.map { this[it] }

        val groups = (0 until nrow)
                .map { index -> columns.map { it[index] } to index }
                .groupBy { it.first }
                .map {
                    val rowIndices = it.value.map { it.second }
                    val groupColumns = this.columns.map { it[rowIndices] }
                    it.key to dataFrameOf(groupColumns).typed<T>()
                }

        val keyColumns = columns.mapIndexed { index, column ->
            var hasNulls = false
            val values = groups.map {
                it.first[index].also { if (it == null) hasNulls = true }
            }
            column(column.name, values, column.type.withNullability(hasNulls))
        }

        val groupFrames = groups.map { it.second }
        val groupsColumn = columnForGroupedData.withValues(groupFrames, false)

        val df = dataFrameOf(keyColumns + groupsColumn).typed<T>()
        return GroupedDataFrameImpl(df)
    }

    private fun DataCol.createComparator(nullsLast: Boolean): Comparator<Int> {

        if (!type.isSubtypeOf(getType<Comparable<*>?>()))
            throw UnsupportedOperationException()

        return Comparator<Any?> { left, right ->
            (left as Comparable<Any?>).compareTo(right)
        }.let { if (nullsLast) nullsLast(it) else nullsFirst(it) }
                .let { Comparator { left, right -> it.compare(get(left), get(right)) } }
    }

    override fun sortBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T> {

        val compChain = columns.map {
            val column = this[it.column]
            when (it.direction) {
                SortDirection.Asc -> column.createComparator(it.nullsLast)
                SortDirection.Desc -> column.createComparator(it.nullsLast).reversed()
            }
        }.reduce { a, b -> a.then(b) }

        val permutation = (0 until nrow).sortedWith(compChain)

        return reorder(permutation)
    }

    override fun getRows(indices: Iterable<Int>) = columns.map { col -> col.getRows(indices) }.let { dataFrameOf(it).typed<T>() }

    override fun getRows(mask: BooleanArray) = columns.map { col -> col.getRows(mask) }.let { dataFrameOf(it).typed<T>() }

    override fun getRows(range: IntRange) = columns.map { col -> col.getRows(range) }.let { dataFrameOf(it).typed<T>() }

    override fun getColumnIndex(columnName: String) = columnsMap[columnName] ?: -1

    override fun equals(other: Any?): Boolean {
        val df = other as? TypedDataFrame<*> ?: return false
        return columns == df.columns
    }

    override fun toString() = renderToString()

    override fun addRow(vararg values: Any?): TypedDataFrame<T> {
        assert(values.size == ncol) { "Invalid number of arguments. Expected: $ncol, actual: ${values.size}" }
        return values.mapIndexed { i, v ->
            val col = columns[i]
            if(v != null)
                // Note: type arguments for a new value are not validated here because they are erased
                assert(v.javaClass.kotlin.isSubclassOf(col.type.jvmErasure))
            col.withValues(col.values + listOf(v), col.hasNulls || v == null)
        }.asDataFrame<T>()
    }
}

fun <T> TypedDataFrame<*>.typed(): TypedDataFrame<T> = this as TypedDataFrame<T>

fun <T> TypedDataFrameRow<T>.toDataFrame() = owner.columns.map {
    val value = it[index]
    it.withValues(listOf(value), value == null)
}.let { dataFrameOf(it).typed<T>() }

fun <T> TypedDataFrame<*>.retype(klazz: KClass<*>): TypedDataFrame<T> {
    val newColumns = columns.map {
        when(it) {
            is GroupedColumn<*> -> {
                val columnName = it.name
                val property = klazz.memberProperties.firstOrNull {
                    it.getColumnName() == columnName
                }
                if (property != null) {
                    val dfType = property.returnType
                    assert(dfType.arguments.size == 1)
                    val markerType = dfType.arguments[0].type!!
                    val newDf = it.df.retype<Unit>(markerType.classifier as KClass<*>)
                    ColumnData.createGroup(it.name, newDf, markerType)
                } else it
            }
            else -> it
        }
    }
    return newColumns.asDataFrame()
}

inline fun <reified T> TypedDataFrame<*>.retype() = retype<T>(T::class)
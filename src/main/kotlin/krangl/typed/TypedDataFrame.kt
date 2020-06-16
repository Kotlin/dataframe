package krangl.typed

import krangl.*
import java.util.*
import kotlin.reflect.full.isSubclassOf

interface TypedDataFrameWithColumns<out T> : TypedDataFrame<T> {
    fun columns(vararg col: DataCol) = ColumnGroup(col.toList())

    fun columns(filter: (DataCol) -> Boolean): ColumnGroup

    val allColumns: ColumnGroup

    infix fun ColumnSet.and(other: ColumnSet) = ColumnGroup(listOf(this, other))

    infix fun String.and(other: ColumnSet) = ColumnGroup(listOf(asColumnName(), other))

    infix fun String.and(other: String) = ColumnGroup(listOf(asColumnName(), other.asColumnName()))

    infix fun ColumnSet.and(other: String) = ColumnGroup(listOf(this, other.asColumnName()))

    operator fun String.invoke() = asColumnName()
}

interface TypedDataFrameWithColumnsForSort<out T> : TypedDataFrameWithColumns<T> {

    val NamedColumn.desc get() = ReversedColumn(this)

    val String.desc get() = ReversedColumn(asColumnName())
}

open class TypedDataFrameWithColumnsImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumns<T> {

    override val allColumns get() = ColumnGroup(columns)

    override fun columns(filter: (DataCol) -> Boolean) = ColumnGroup(columns.filter(filter))
}

class TypedDataFrameWithColumnsForSortImpl<T>(df: TypedDataFrame<T>) : TypedDataFrameWithColumnsImpl<T>(df), TypedDataFrameWithColumnsForSort<T>

data class DataFrameSize(val ncol: Int, val nrow: Int) {
    override fun toString() = "$nrow x $ncol"
}

typealias ColumnSelector<T> = TypedDataFrameWithColumns<T>.() -> ColumnSet

typealias SortColumnSelector<T> = TypedDataFrameWithColumnsForSort<T>.() -> ColumnSet

internal fun ColumnSet.extractColumns(): List<NamedColumn> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractColumns() }
    is NamedColumn -> listOf(this)
    else -> throw Exception()
}

internal fun ColumnSet.extractSortColumns(): List<SortColumnDescriptor> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractSortColumns() }
    is ReversedColumn -> column.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction.reversed()) }
    is NamedColumn -> listOf(SortColumnDescriptor(name, SortDirection.Asc))
    else -> throw Exception()
}

internal fun <T> TypedDataFrame<T>.getColumns(selector: ColumnSelector<T>) = selector(TypedDataFrameWithColumnsImpl(this)).extractColumns()

internal fun <T> TypedDataFrame<T>.getSortColumns(selector: SortColumnSelector<T>) = selector(TypedDataFrameWithColumnsForSortImpl(this)).extractSortColumns()

internal fun <T> TypedDataFrame<T>.getColumns(columnNames: Array<out String>) = columnNames.map { this[it] }

enum class SortDirection { Asc, Desc }

fun SortDirection.reversed() = when (this) {
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}

class SortColumnDescriptor(val column: String, val direction: SortDirection)

internal fun <T> TypedDataFrame<T>.new(columns: Iterable<DataCol>) = dataFrameOf(columns).typed<T>()

typealias UntypedDataFrame = TypedDataFrame<Nothing>

interface TypedDataFrame<out T> {

    val nrow: Int
    val ncol: Int get() = columns.size
    val columns: List<DataCol>
    val rows: Iterable<TypedDataFrameRow<T>>

    fun columnNames() = columns.map { it.name }

    operator fun get(rowIndex: Int): TypedDataFrameRow<T>
    operator fun get(columnName: String) = tryGetColumn(columnName) ?: throw Exception("Column not found") // TODO
    operator fun <R> get(column: TypedCol<R>) = get(column.name) as TypedColData<R>
    operator fun get(indices: IntArray) = getRows(indices)
    operator fun get(indices: List<Int>) = getRows(indices)
    operator fun get(mask: BooleanArray) = getRows(mask)
    operator fun get(range: IntRange) = getRows(range)

    operator fun plus(col: DataCol) = dataFrameOf(columns + col).typed<T>()
    operator fun plus(col: Iterable<DataCol>) = new(columns + col)
    operator fun plus(stub: AddRowNumberStub) = addRowNumber(stub.columnName)

    fun getRows(indices: IntArray): TypedDataFrame<T>
    fun getRows(mask: BooleanArray): TypedDataFrame<T>
    fun getRows(indices: List<Int>) = getRows(indices.toIntArray())
    fun getRows(range: IntRange) = getRows(range.toList())

    fun tryGetColumn(name: String): DataCol?

    fun select(columns: Iterable<NamedColumn>) = new(columns.map { this[it.name] })
    fun select(vararg columns: Column) = select(columns.toList())
    fun select(vararg columns: String) = select(getColumns(columns))
    fun select(selector: ColumnSelector<T>) = select(getColumns(selector))
    fun selectIf(filter: DataCol.(DataCol) -> Boolean) = select(columns.filter { filter(it, it) })

    fun sortBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T>
    fun sortBy(columns: Iterable<NamedColumn>) = sortBy(columns.map { SortColumnDescriptor(it.name, SortDirection.Asc) })
    fun sortBy(vararg columns: Column) = sortBy(columns.toList())
    fun sortBy(vararg columns: String) = sortBy(getColumns(columns))
    fun sortBy(selector: SortColumnSelector<T>) = sortBy(getSortColumns(selector))

    fun sortByDesc(columns: Iterable<NamedColumn>) = sortBy(columns.map { SortColumnDescriptor(it.name, SortDirection.Desc) })
    fun sortByDesc(vararg columns: Column) = sortByDesc(columns.toList())
    fun sortByDesc(vararg columns: String) = sortByDesc(getColumns(columns))
    fun sortByDesc(selector: ColumnSelector<T>) = sortByDesc(getColumns(selector))

    fun remove(cols: Iterable<NamedColumn>) = cols.map { it.name }.toSet().let { exclude -> new(columns.filter { !exclude.contains(it.name) }) }
    fun remove(vararg cols: NamedColumn) = remove(cols.toList())
    fun remove(vararg columns: String) = remove(getColumns(columns))
    fun remove(selector: ColumnSelector<T>) = remove(getColumns(selector))

    infix operator fun minus(cols: ColumnSelector<T>) = remove(cols)
    infix operator fun minus(cols: Iterable<NamedColumn>) = remove(cols)
    infix operator fun minus(column: NamedColumn) = remove(column)
    infix operator fun minus(column: String) = remove(column)

    fun groupBy(cols: Iterable<NamedColumn>): GroupedDataFrame<T>
    fun groupBy(vararg cols: Column) = groupBy(cols.toList())
    fun groupBy(vararg cols: String) = groupBy(getColumns(cols))
    fun groupBy(cols: ColumnSelector<T>) = groupBy(getColumns(cols))

    fun update(cols: Iterable<NamedColumn>) = UpdateClauseImpl(this, cols.toList()) as UpdateClause<T>
    fun update(vararg cols: Column) = update(cols.toList())
    fun update(vararg cols: String) = update(getColumns(cols))
    fun update(cols: ColumnSelector<T>) = update(getColumns(cols))

    fun addRowNumber(columnName: String = "id") = new(columns + IntCol(columnName, IntArray(nrow) { it }).typed())

    fun filter(predicate: RowFilter<T>): TypedDataFrame<T>

    fun filterNotNull(cols: Iterable<NamedColumn>) = filter { cols.all { col -> this[col.name] != null } }
    fun filterNotNull(vararg cols: Column) = filterNotNull(cols.toList())
    fun filterNotNull(vararg cols: String) = filterNotNull(getColumns(cols))
    fun filterNotNull(cols: ColumnSelector<T>) = filterNotNull(getColumns(cols))

    fun filterNotNullAny(columns: ColumnSelector<T>) = getColumns(columns).let { cols -> filter { cols.any { col -> this[col.name] != null } } }
    fun filterNotNullAny() = filter { values.any { it.second != null } }
    fun filterNotNull() = filter { values.all { it.second != null } }

    fun <D : Comparable<D>> min(selector: RowSelector<T, D?>): D? = rows.asSequence().map(selector).filterNotNull().min()
    fun <D : Comparable<D>> min(col: TypedCol<D?>): D? = get(col).values.asSequence().filterNotNull().min()

    fun <D : Comparable<D>> max(selector: RowSelector<T, D?>): D? = rows.asSequence().map(selector).filterNotNull().max()
    fun <D : Comparable<D>> max(col: TypedCol<D?>): D? = get(col).values.asSequence().filterNotNull().max()

    fun <D : Comparable<D>> maxBy(selector: RowSelector<T, D>) = rows.maxBy(selector)
    fun <D : Comparable<D>> maxBy(col: TypedCol<D>) = rows.maxBy { col(it) }
    fun <D : Comparable<D>> maxBy(col: String) = rows.maxBy { it[col] as D }

    fun <D : Comparable<D>> minBy(selector: RowSelector<T, D>) = rows.minBy(selector)
    fun <D : Comparable<D>> minBy(col: TypedCol<D>) = rows.minBy { col(it) }
    fun <D : Comparable<D>> minBy(col: String) = rows.minBy { it[col] as D }

    fun all(predicate: RowFilter<T>): Boolean = rows.all(predicate)
    fun any(predicate: RowFilter<T>): Boolean = rows.any(predicate)

    fun count(predicate: RowFilter<T>) = rows.count(predicate)

    fun first() = rows.first()
    fun firstOrNull() = rows.firstOrNull()
    fun last() = rows.last() // TODO: optimize (don't iterate through the whole data frame)
    fun lastOrNull() = rows.lastOrNull()
    fun take(numRows: Int = 5) = getRows(0 until numRows)
    fun skip(numRows: Int = 5) = getRows(numRows until nrow)
    fun takeLast(numRows: Int) = getRows(nrow - numRows until nrow)
    fun skipLast(numRows: Int = 5) = getRows(0 until nrow - numRows)
    fun head(numRows: Int = 5) = take(numRows)
    fun tail(numRows: Int = 5) = takeLast(numRows)

    fun <R> map(selector: RowSelector<T, R>) = rows.map(selector)
    fun forEach(selector: RowSelector<T, Unit>) = rows.forEach(selector)

    val size get() = DataFrameSize(ncol, nrow)
}

interface UpdateClause<out T> {
    val df: TypedDataFrame<T>
    val cols: List<NamedColumn>
}

class UpdateClauseImpl<T>(override val df: TypedDataFrame<T>, override val cols: List<NamedColumn>) : UpdateClause<T>

inline infix fun <T, reified R> UpdateClause<T>.with(noinline expression: TypedDataFrameRow<T>.() -> R?): TypedDataFrame<T> {
    val newCol = df.new(cols.first().name, expression)
    return df - cols + newCol + cols.takeLast(cols.size - 1).map { newCol.rename(it.name) }
}

inline fun <T, reified R> TypedDataFrame<T>.update(vararg cols: Column, noinline expression: TypedDataFrameRow<T>.() -> R?) =
        update(*cols).with(expression)

inline fun <T, reified R> TypedDataFrame<T>.update(vararg cols: String, noinline expression: TypedDataFrameRow<T>.() -> R?) =
        update(*cols).with(expression)

inline fun <T> UpdateClause<T>.withNull() = with { null as Any? }

internal class TypedDataFrameImpl<T>(override val columns: List<DataCol>) : TypedDataFrame<T> {

    override val nrow = columns.firstOrNull()?.length ?: 0

    private val columnsMap by lazy { columns.map { it.name to it }.toMap() }

    init {
        val invalidSizeColumns = columns.filter { it.length != nrow }
        if (invalidSizeColumns.size > 0)
            throw Exception("Invalid column sizes: ${invalidSizeColumns}") // TODO

        val columnNames = columns.groupBy { it.name }.filter { it.value.size > 1 }
        if (columnNames.size > 0)
            throw Exception("Duplicate column names: ${columnNames}") // TODO
    }

    private val rowResolver = RowResolver<T>(this)

    override val rows = object : Iterable<TypedDataFrameRow<T>> {
        override fun iterator() =

                object : Iterator<TypedDataFrameRow<T>> {
                    var curRow = 0

                    val resolver = RowResolver<T>(this@TypedDataFrameImpl)

                    override fun hasNext(): Boolean = curRow < nrow

                    override fun next() = resolver.let { resolver[curRow++]!! }
                }
    }

    override fun get(rowIndex: Int) = rowResolver[rowIndex]!!

    override fun filter(predicate: RowFilter<T>): TypedDataFrame<T> =
            rowWise { getRow ->
                BooleanArray(nrow) { index ->
                    val row = getRow(index)!!
                    predicate(row)
                }
            }.let(::getRows).typed()

    /** Return an iterator over the rows in data in the receiver. */
    private fun TypedDataFrame<*>.rowData(): Iterable<List<Any?>> = object : Iterable<List<Any?>> {

        override fun iterator() = object : Iterator<List<Any?>> {

            val colIterators = columns.map { it.values.iterator() }

            override fun hasNext(): Boolean = colIterators.firstOrNull()?.hasNext() ?: false

            override fun next(): List<Any?> = colIterators.map { it.next() }
        }
    }

    override fun groupBy(cols: Iterable<NamedColumn>): GroupedDataFrame<T> {

        val groups = select(cols)
                .rowData()
                .mapIndexed { index, group -> group to index }
                .groupBy { it.first }
                .map {
                    val groupRowIndices = it.value.map { it.second }.toIntArray()
                    val grpSubCols = columns.map { it.getRows(groupRowIndices) }
                    DataGroupImpl<T>(it.key, dataFrameOf(grpSubCols).typed())
                }

        return GroupedDataFrameImpl(cols.map { it.name }, groups)
    }

    private fun DataCol.createComparator(naLast: Boolean = true): Comparator<Int> {

        if(!valueClass.isSubclassOf(Comparable::class))
            throw UnsupportedOperationException()

        return Comparator<Any?> { left, right ->
            (left as Comparable<Any?>).compareTo(right)
        }.let { if (naLast) nullsLast(it) else nullsFirst(it) }
         .let { Comparator { left, right -> it.compare(values[left], values[right]) } }
    }

    override fun sortBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T> {

        val compChain = columns.map {
            val column = this[it.column]
            when (it.direction) {
                SortDirection.Asc -> column.createComparator()
                SortDirection.Desc -> column.createComparator().reversed()
            }
        }.reduce { a, b -> a.then(b) }

        val permutation = (0 until nrow).sortedWith(compChain).toIntArray()

        return this.columns.map { it.reorder(permutation) }.let {
            dataFrameOf(it).typed()
        }
    }

    override fun getRows(indices: IntArray) = columns.map { col -> col.getRows(indices) }.let { dataFrameOf(it).typed<T>() }

    override fun getRows(mask: BooleanArray) = columns.map { col -> col.getRows(mask) }.let { dataFrameOf(it).typed<T>() }

    override fun tryGetColumn(columnName: String) = columnsMap[columnName]
}

internal fun <T> LinkedList<T>.popSafe() = if (isEmpty()) null else pop()

internal class RowResolver<T>(val dataFrame: TypedDataFrame<*>) {
    private val pool = LinkedList<TypedDataFrameRowImpl<T>>()
    private val map = mutableMapOf<Int, TypedDataFrameRowImpl<T>>()

    fun resetMapping() {
        pool.addAll(map.values)
        map.clear()
    }

    fun readRow(rowIndex: Int) = dataFrame.columns.map { it.name to it[rowIndex] }.toMap()

    operator fun get(index: Int): TypedDataFrameRow<T>? =
            if (index < 0 || index >= dataFrame.nrow) null
            else map[index] ?: pool.let { it.popSafe() }?.also {
                it.row = readRow(index)
                it.index = index
                map[index] = it
            } ?: TypedDataFrameRowImpl(readRow(index), index, this).also { map[index] = it }
}

typealias RowAccessor<T> = (Int) -> TypedDataFrameRow<T>?

fun <R, T> TypedDataFrame<T>.rowWise(body: (RowAccessor<T>) -> R): R {
    val resolver = RowResolver<T>(this)
    fun getRow(index: Int): TypedDataFrameRow<T>? {
        resolver.resetMapping()
        return resolver[index]
    }
    return body(::getRow)
}

fun <T> DataFrame.typed(): TypedDataFrame<T> = TypedDataFrameImpl(cols.map { it.typed() })

fun <T> TypedDataFrame<*>.typed(): TypedDataFrame<T> = TypedDataFrameImpl(columns)

fun <T> TypedDataFrameRow<T>.toDataFrame() =
        dataFrameOf(values.map { it.first })(values.map { it.second })
package krangl.typed

import krangl.*
import java.util.*

interface TypedDataFrameWithColumns<out T> : TypedDataFrame<T> {
    fun columns(vararg col: DataCol) = ColumnGroup(col.toList())

    fun columns(filter: (DataCol) -> Boolean) = ColumnGroup(super.columns.filter(filter))

    val allColumns get() = ColumnGroup(super.columns)

    infix fun DataCol.and(other: DataCol) = ColumnGroup(listOf(this, other))
}

interface TypedDataFrameWithColumnsForSort<out T> : TypedDataFrameWithColumns<T> {

    val DataCol.desc get() = ReversedColumn(this)
}

class TypedDataFrameWithColumnsImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumns<T>

class TypedDataFrameWithColumnsForSortImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumnsForSort<T>

data class DataFrameSize(val ncol: Int, val nrow: Int){
    override fun toString() = "$nrow x $ncol"
}

typealias ColumnSelector<T> = TypedDataFrameWithColumns<T>.() -> DataCol

typealias SortColumnSelector<T> = TypedDataFrameWithColumnsForSort<T>.() -> DataCol

internal fun DataCol.extractColumns(): List<DataCol> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractColumns() }
    else -> listOf(this)
}

internal fun DataCol.extractSortColumns(): List<SortColumnDescriptor> = when (this) {
    is ColumnGroup -> columns.flatMap { it.extractSortColumns() }
    is ReversedColumn -> column.extractSortColumns().map { SortColumnDescriptor(it.column, it.direction.reversed()) }
    else -> listOf(SortColumnDescriptor(this, SortDirection.Asc))
}

internal fun <T> TypedDataFrame<T>.getColumns(selector: ColumnSelector<T>) = selector(TypedDataFrameWithColumnsImpl(this)).extractColumns()

internal fun <T> TypedDataFrame<T>.getSortColumns(selector: SortColumnSelector<T>) = selector(TypedDataFrameWithColumnsForSortImpl(this)).extractSortColumns()

internal fun <T> TypedDataFrame<T>.getColumns(columnNames: Array<out String>) = columnNames.map { this[it] }

enum class SortDirection {Asc, Desc}

fun SortDirection.reversed() = when(this){
    SortDirection.Asc -> SortDirection.Desc
    SortDirection.Desc -> SortDirection.Asc
}

class SortColumnDescriptor(val column: DataCol, val direction: SortDirection)

interface TypedDataFrame<out T> {

    val df: DataFrame
    val nrow: Int get() = df.nrow
    val ncol: Int get() = df.ncol
    val columns: List<DataCol> get() = df.cols
    val rows: Iterable<TypedDataFrameRow<T>>

    operator fun get(rowIndex: Int): TypedDataFrameRow<T>
    operator fun get(columnName: String): DataCol = df[columnName]

    operator fun plus(col: DataCol) = (df + col).typed<T>()
    operator fun plus(col: Iterable<DataCol>) = dataFrameOf(df.cols + col).typed<T>()
    operator fun plus(stub: AddRowNumberStub) = addRowNumber(stub.columnName)

    fun select(columns: Iterable<DataCol>) = df.select(columns.map { it.name }).typed<T>()
    fun select(vararg columns: DataCol) = select(columns.toList())
    fun select(vararg columns: String) = select(getColumns(columns))
    fun select(selector: ColumnSelector<T>) = select(getColumns(selector))
    fun selectIf(filter: DataCol.(DataCol) -> Boolean) = select(columns.filter{filter(it,it)})

    fun sortedBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T>
    fun sortedBy(columns: Iterable<DataCol>) = sortedBy(columns.map { SortColumnDescriptor(it, SortDirection.Asc) })
    fun sortedBy(vararg columns: DataCol) = sortedBy(columns.toList())
    fun sortedBy(vararg columns: String) = sortedBy(getColumns(columns))
    fun sortedBy(selector: SortColumnSelector<T>) = sortedBy(getSortColumns(selector))

    fun sortedByDesc(columns: Iterable<DataCol>) = sortedBy(columns.map { SortColumnDescriptor(it, SortDirection.Desc) })
    fun sortedByDesc(vararg columns: DataCol) = sortedByDesc(columns.toList())
    fun sortedByDesc(vararg columns: String) = sortedByDesc(getColumns(columns))
    fun sortedByDesc(selector: ColumnSelector<T>) = sortedByDesc(getColumns(selector))

    fun remove(cols: Iterable<DataCol>) = df.remove(cols.map { it.name }).typed<T>()
    fun remove(vararg cols: DataCol) = remove(cols.toList())
    fun remove(vararg columns: String) = remove(getColumns(columns))
    fun remove(selector: ColumnSelector<T>) = remove(getColumns(selector))

    infix operator fun minus(selector: ColumnSelector<T>) = remove(selector)
    infix operator fun minus(columns: Iterable<DataCol>) = remove(columns)

    fun groupBy(cols: Iterable<DataCol>): GroupedDataFrame<T>
    fun groupBy(vararg cols: DataCol) = groupBy(cols.toList())
    fun groupBy(vararg cols: String) = groupBy(getColumns(cols))
    fun groupBy(selector: ColumnSelector<T>) = groupBy(getColumns(selector))

    fun update(selector: ColumnSelector<T>) = UpdateClauseImpl(this, getColumns(selector)) as UpdateClause<T>

    fun addRowNumber(columnName: String = "id") = dataFrameOf(columns + IntCol(columnName, IntArray(nrow){it})).typed<T>()

    fun filter(predicate: RowFilter<T>): TypedDataFrame<T>
    fun filterNotNull(columns: ColumnSelector<T>) = getColumns(columns).let { cols -> filter { cols.all { col -> this[col.name] != null } } }
    fun filterNotNullAny(columns: ColumnSelector<T>) = getColumns(columns).let { cols -> filter { cols.any { col -> this[col.name] != null } } }
    fun filterNotNullAny() = filter { fields.any { it.second != null } }
    fun filterNotNull() = filter { fields.all { it.second != null } }

    fun <D : Comparable<D>> min(selector: RowSelector<T, D>): D? = rows.map (selector).min()
    fun <D : Comparable<D>> max(selector: RowSelector<T, D>): D? = rows.map (selector).max()
    fun <D : Comparable<D>> maxBy(selector: RowSelector<T, D>) = rows.maxBy(selector)
    fun <D : Comparable<D>> minBy(selector: RowSelector<T, D>) = rows.minBy(selector)

    fun count(predicate: RowFilter<T>) = rows.count(predicate)
    fun count() = df.count().typed<T>()

    fun first() = rows.first()
    fun firstOrNull() = rows.firstOrNull()
    fun last() = rows.last() // TODO: optimize (don't iterate through the whole data frame)
    fun lastOrNull() = rows.lastOrNull()
    fun take(numRows: Int = 5) = df.take(numRows).typed<T>()
    fun skip(numRows: Int = 5) = takeLast(nrow - numRows)
    fun takeLast(numRows: Int) = df.takeLast(numRows).typed<T>()
    fun skipLast(numRows: Int = 5) = take(nrow - numRows)
    fun head(numRows: Int = 5) = take(numRows)
    fun tail(numRows: Int = 5) = takeLast(numRows)

    fun <R> map(selector: RowSelector<T, R>) = rows.map(selector)

    val size get() = DataFrameSize(ncol, nrow)
}

interface UpdateClause<out T>{
    val df: TypedDataFrame<T>
    val cols: List<DataCol>
}

class UpdateClauseImpl<T>(override val df: TypedDataFrame<T>, override val cols: List<DataCol>): UpdateClause<T>

inline infix fun <T, reified R> UpdateClause<T>.with(noinline expression: TypedDataFrameRow<T>.() -> R?): TypedDataFrame<T> {
    val newCol = df.new(cols.first().name, expression)
    return df - cols + newCol + cols.takeLast(cols.size - 1).map { newCol.rename(it.name) }
}

inline fun <T> UpdateClause<T>.withNull() = with {null as Any?}

internal class TypedDataFrameImpl<T>(override val df: DataFrame) : TypedDataFrame<T> {
    private val rowResolver = RowResolver<T>(df)

    override val rows = object : Iterable<TypedDataFrameRow<T>> {
        override fun iterator() =

                object : Iterator<TypedDataFrameRow<T>> {
                    var curRow = 0

                    val resolver = RowResolver<T>(df)

                    override fun hasNext(): Boolean = curRow < nrow

                    override fun next() = resolver.let { resolver[curRow++]!! }
                }
    }

    override fun get(rowIndex: Int) = rowResolver[rowIndex]!!

    override fun filter(predicate: RowFilter<T>): TypedDataFrame<T> =
            df.filter {
                rowWise { getRow ->
                    BooleanArray(nrow) { index ->
                        val row = getRow(index)!!
                        predicate(row)
                    }
                }
            }.typed<T>()

    /** Return an iterator over the rows in data in the receiver. */
    internal fun TypedDataFrame<*>.rowData(): Iterable<List<Any?>> = object : Iterable<List<Any?>> {

        override fun iterator() = object : Iterator<List<Any?>> {

            val colIterators = columns.map { it.values().iterator() }

            override fun hasNext(): Boolean = colIterators.firstOrNull()?.hasNext() ?: false

            override fun next(): List<Any?> = colIterators.map { it.next() }
        }
    }

    override fun groupBy(cols: Iterable<DataCol>): GroupedDataFrame<T> {

        fun extractGroup(col: DataCol, indices: IntArray): DataCol = when (col) {
            is DoubleCol -> DoubleCol(col.name, Array(indices.size) { col.values[indices[it]] })
            is IntCol -> IntCol(col.name, Array(indices.size) { col.values[indices[it]] })
            is LongCol -> LongCol(col.name, Array(indices.size) { col.values[indices[it]] })
            is BooleanCol -> BooleanCol(col.name, Array(indices.size) { col.values[indices[it]] })
            is StringCol -> StringCol(col.name, Array(indices.size) { col.values[indices[it]] })
            is AnyCol -> AnyCol(col.name, Array(indices.size) { col.values[indices[it]] })
            else -> throw UnsupportedOperationException()
        }

        val groups = select(cols)
                .rowData()
                .mapIndexed { index, group -> group to index }
                .groupBy { it.first }
                .map {
                    val groupRowIndices = it.value.map { it.second }.toIntArray()
                    val grpSubCols = columns.map { extractGroup(it, groupRowIndices) }
                    DataGroupImpl<T>(it.key, dataFrameOf(grpSubCols).typed())
                }

        return GroupedDataFrameImpl(cols.map { it.name }, groups)
    }

    private fun DataCol.createComparator(naLast: Boolean = true): Comparator<Int> {
        fun <T : Comparable<T>> nullWhere(): Comparator<T?> = if (naLast) nullsLast<T>() else nullsFirst<T>()

        return when (this) {
            is DoubleCol -> Comparator { left, right -> nullWhere<Double>().compare(values[left], values[right]) }
            is IntCol -> Comparator { left, right -> nullWhere<Int>().compare(values[left], values[right]) }
            is LongCol -> Comparator { left, right -> nullWhere<Long>().compare(values[left], values[right]) }
            is BooleanCol -> Comparator { left, right -> nullWhere<Boolean>().compare(values[left], values[right]) }
            is StringCol -> Comparator { left, right -> nullWhere<String>().compare(values[left], values[right]) }
            is AnyCol -> Comparator { left, right ->
                @Suppress("UNCHECKED_CAST")
                nullWhere<Comparable<Any>>().compare(values[left] as Comparable<Any>, values[right] as Comparable<Any>)
            }
            else -> throw UnsupportedOperationException()
        }
    }

    override fun sortedBy(columns: List<SortColumnDescriptor>): TypedDataFrame<T> {

        val compChain = columns.map {
            when (it.direction) {
                SortDirection.Asc -> it.column.createComparator()
                SortDirection.Desc -> it.column.createComparator().reversed()
            }
        }.reduce { a, b -> a.then(b) }

        val permutation = (0 until nrow).sortedWith(compChain).toIntArray()

        return this.columns.map {
            when (it) {
                is DoubleCol -> DoubleCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                is IntCol -> IntCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                is LongCol -> LongCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                is BooleanCol -> BooleanCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                is StringCol -> StringCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                is AnyCol -> AnyCol(it.name, Array(nrow, { index -> it.values[permutation[index]] }))
                else -> throw UnsupportedOperationException()
            }
        }.let {
            dataFrameOf(it).typed()
        }
    }
}

internal class RowResolver<T>(val dataFrame: DataFrame) {
    private val pool = LinkedList<TypedDataFrameRowImpl<T>>()
    private val map = mutableMapOf<Int, TypedDataFrameRowImpl<T>>()

    fun resetMapping() {
        pool.addAll(map.values)
        map.clear()
    }

    operator fun get(index: Int): TypedDataFrameRow<T>? =
            if (index < 0 || index >= dataFrame.nrow) null
            else map[index] ?: pool.popSafe()?.also {
                it.row = dataFrame.row(index)
                it.index = index
                map[index] = it
            } ?: TypedDataFrameRowImpl(dataFrame.row(index), index, this).also { map[index] = it }
}

typealias RowAccessor<T> = (Int) -> TypedDataFrameRow<T>?

fun <R, T> TypedDataFrame<T>.rowWise(body: (RowAccessor<T>) -> R): R {
    val resolver = RowResolver<T>(this.df)
    fun getRow(index: Int): TypedDataFrameRow<T>? {
        resolver.resetMapping()
        return resolver[index]
    }
    return body(::getRow)
}

fun <T> DataFrame.typed(): TypedDataFrame<T> = TypedDataFrameImpl(this)

fun <T> TypedDataFrame<*>.typed() = df.typed<T>()

fun <T> TypedDataFrameRow<T>.toDataFrame() =
        dataFrameOf(fields.map { it.first })(fields.map{ it.second })
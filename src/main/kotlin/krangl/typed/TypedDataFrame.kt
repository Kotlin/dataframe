package krangl.typed

import krangl.DataCol
import krangl.DataFrame
import krangl.DataFrameRow
import krangl.dataFrameOf
import krangl.typed.tracking.ColumnAccessTracker
import java.util.*

interface TypedDataFrameRow<out T> {
    val prev: TypedDataFrameRow<T>?
    val next: TypedDataFrameRow<T>?
    val index: Int
    fun getRow(index: Int): TypedDataFrameRow<T>?
    operator fun get(name: String): Any?
    val fields: List<Pair<String, Any?>>
}

interface TypedDataFrameWithColumns<out T> : TypedDataFrame<T> {
    fun columns(vararg col: DataCol) = ColumnGroup(col.toList())

    fun columns(filter: (DataCol) -> Boolean) = ColumnGroup(super.columns.filter(filter))
}

class TypedDataFrameWithColumnsImpl<T>(df: TypedDataFrame<T>) : TypedDataFrame<T> by df, TypedDataFrameWithColumns<T>

typealias ColumnSelector<T> = TypedDataFrameWithColumns<T>.() -> DataCol

internal fun DataCol.extractColumns() = when (this) {
    is ColumnGroup -> columns
    else -> listOf(this)
}

internal fun <T> TypedDataFrame<T>.getColumns(selector: ColumnSelector<T>) = selector(TypedDataFrameWithColumnsImpl(this)).extractColumns()

interface TypedDataFrame<out T> {
    val df: DataFrame
    val nrow: Int get() = df.nrow
    val ncol: Int get() = df.ncol
    val columns: List<DataCol> get() = df.cols
    val rows: Iterable<TypedDataFrameRow<T>>

    operator fun get(rowIndex: Int): TypedDataFrameRow<T>
    operator fun get(columnName: String): DataCol = df[columnName]

    fun select(columns: Iterable<DataCol>) = df.select(columns.map { it.name }).typed<T>()
    fun select(vararg columns: DataCol) = select(columns.toList())
    fun select(selector: ColumnSelector<T>) = select(getColumns(selector))
    fun selectIf(filter: DataCol.(DataCol) -> Boolean) = select(columns.filter{filter(it,it)})

    fun sortedBy(columns: Iterable<DataCol>) = df.sortedBy(*(columns.map { it.name }.toTypedArray())).typed<T>()
    fun sortedBy(vararg columns: DataCol) = sortedBy(columns.toList())
    fun sortedBy(selector: ColumnSelector<T>) = sortedBy(getColumns(selector))

    fun sortedByDesc(columns: Iterable<DataCol>) = df.sortedByDescending(*(columns.map { it.name }.toTypedArray())).typed<T>()
    fun sortedByDesc(vararg columns: DataCol) = sortedByDesc(columns.toList())
    fun sortedByDesc(selector: ColumnSelector<T>) = sortedByDesc(getColumns(selector))

    fun remove(cols: Iterable<DataCol>) = df.remove(cols.map { it.name }).typed<T>()
    fun remove(vararg cols: DataCol) = remove(cols.toList())
    fun remove(selector: ColumnSelector<T>) = remove(getColumns(selector))
    infix operator fun minus(selector: ColumnSelector<T>) = remove(selector)

    fun groupBy(cols: Iterable<DataCol>) = df.groupBy(*(cols.map { it.name }.toTypedArray())).typed<T>()
    fun groupBy(vararg cols: DataCol) = groupBy(cols.toList())
    fun groupBy(selector: ColumnSelector<T>) = groupBy(getColumns(selector))

    fun ungroup() = df.ungroup().typed<T>()

    fun groupedBy() = df.groupedBy().typed<T>()
    fun groups() = df.groups().map { it.typed<T>() }
}

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
}

internal class TypedDataFrameRowImpl<T>(var row: DataFrameRow, override var index: Int, val resolver: RowResolver<T>) : TypedDataFrameRow<T> {

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.lastAccessedColumn.set(name)
        return row[name]
    }

    override val prev: TypedDataFrameRow<T>?
        get() = resolver[index - 1]
    override val next: TypedDataFrameRow<T>?
        get() = resolver[index + 1]

    override fun getRow(index: Int): TypedDataFrameRow<T>? = resolver[index]
    override val fields: List<Pair<String, Any?>>
        get() = row.entries.map { it.key to it.value }

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

fun <T, D> TypedDataFrame<D>.rowWise(body: ((Int) -> TypedDataFrameRow<D>?) -> T): T {
    val resolver = RowResolver<D>(this.df)
    fun getRow(index: Int): TypedDataFrameRow<D>? {
        resolver.resetMapping()
        return resolver[index]
    }
    return body(::getRow)
}

fun <T> DataFrame.typed(): TypedDataFrame<T> = TypedDataFrameImpl(this)

fun <T> TypedDataFrame<*>.typed() = df.typed<T>()

fun <T> TypedDataFrameRow<T>.toDataFrame() =
        dataFrameOf(fields.map { it.first })(fields.map{ it.second })
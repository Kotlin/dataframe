package krangl.typed

import krangl.DataFrameRow
import krangl.typed.tracking.ColumnAccessTracker

interface TypedDataFrameRow<out T> {
    val prev: TypedDataFrameRow<T>?
    val next: TypedDataFrameRow<T>?
    val index: Int
    fun getRow(index: Int): TypedDataFrameRow<T>?
    operator fun get(name: String): Any?
    operator fun <R: Any> get(column: TypedCol<R>) = get(column.name) as R?
    fun <T> read(name: String) = get(name) as T?
    val fields: List<Pair<String, Any?>>
    fun int(name: String) = nint(name)!!
    fun nint(name: String) = read<Int>(name)
    fun string(name: String) = nstring(name)!!
    fun nstring(name: String) = read<String>(name)
    fun double(name: String) = ndouble(name)!!
    fun ndouble(name: String) = read<Double>(name)
}

internal class TypedDataFrameRowImpl<T>(var row: DataFrameRow, override var index: Int, val resolver: RowResolver<T>) : TypedDataFrameRow<T> {

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
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
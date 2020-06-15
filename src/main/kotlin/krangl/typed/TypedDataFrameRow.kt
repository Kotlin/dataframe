package krangl.typed

import krangl.DataFrameRow
import krangl.typed.tracking.ColumnAccessTracker

interface TypedDataFrameRow<out T> {
    val prev: TypedDataFrameRow<T>?
    val next: TypedDataFrameRow<T>?
    val index: Int
    fun getRow(index: Int): TypedDataFrameRow<T>?
    operator fun get(name: String): Any?
    operator fun <R> get(column: TypedCol<R>) = get(column.name) as R
    operator fun <R> TypedCol<R>.invoke() = get(this)
    operator fun <R> String.invoke() = get(this) as R

    fun <T> read(name: String) = get(name) as T
    val values: List<Pair<String, Any?>>

    fun int(name: String) = read<Int>(name)
    fun nint(name: String) = read<Int?>(name)
    fun string(name: String) = read<String>(name)
    fun nstring(name: String) = read<String?>(name)
    fun double(name: String) = read<Double>(name)
    fun ndouble(name: String) = read<Double?>(name)

    operator fun TypedCol<Int>.compareTo(a: Int) = get(this).compareTo(a)
    operator fun TypedCol<Long>.compareTo(a: Long) = get(this).compareTo(a)
    operator fun TypedCol<Double>.compareTo(a: Double) = get(this).compareTo(a)
    operator fun TypedCol<String>.compareTo(a: String) = get(this).compareTo(a)

    operator fun TypedCol<Int>.plus(a: Int) = get(this) + a
    operator fun TypedCol<Long>.plus(a: Long) = get(this) + a
    operator fun TypedCol<Double>.plus(a: Double) = get(this) + a
    operator fun TypedCol<String>.plus(a: String) = get(this) + a
    operator fun Int.plus(col: TypedCol<Int>) = this + get(col)
    operator fun Long.plus(col: TypedCol<Long>) = this + get(col)
    operator fun Double.plus(col: TypedCol<Double>) = this + get(col)
    operator fun String.plus(col: TypedCol<String>) = this + get(col)

    operator fun TypedCol<Int>.minus(a: Int) = get(this) - a
    operator fun TypedCol<Long>.minus(a: Long) = get(this) - a
    operator fun TypedCol<Double>.minus(a: Double) = get(this) - a
    operator fun Int.minus(col: TypedCol<Int>) = this - get(col)
    operator fun Long.minus(col: TypedCol<Long>) = this - get(col)
    operator fun Double.minus(col: TypedCol<Double>) = this - get(col)

    operator fun TypedCol<Int>.times(a: Int) = get(this) * a
    operator fun TypedCol<Long>.times(a: Long) = get(this) * a
    operator fun TypedCol<Double>.times(a: Double) = get(this) * a
    operator fun TypedCol<Double>.times(a: Int) = get(this) * a
    operator fun TypedCol<Long>.times(a: Int) = get(this) * a
    operator fun TypedCol<Double>.times(a: Long) = get(this) * a

    operator fun TypedCol<Int>.div(a: Int) = get(this) / a
    operator fun TypedCol<Long>.div(a: Long) = get(this) / a
    operator fun TypedCol<Double>.div(a: Double) = get(this) / a
    operator fun TypedCol<Double>.div(a: Int) = get(this) / a
    operator fun TypedCol<Long>.div(a: Int) = get(this) / a
    operator fun TypedCol<Double>.div(a: Long) = get(this) / a

    infix fun <R> TypedCol<R>.eq(a: R?) = get(this) == a
    infix fun <R> TypedCol<R>.neq(a: R?) = get(this) != a
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

    override val values: List<Pair<String, Any?>>
        get() = row.entries.map { it.key to it.value }

}
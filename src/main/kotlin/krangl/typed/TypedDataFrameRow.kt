package krangl.typed

import krangl.DataFrameRow
import krangl.typed.tracking.ColumnAccessTracker
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface TypedDataFrameRow<out T> {
    val owner: TypedDataFrame<T>
    val prev: TypedDataFrameRow<T>?
    val next: TypedDataFrameRow<T>?
    val index: Int
    fun getRow(index: Int): TypedDataFrameRow<T>?
    operator fun get(name: String): Any?
    operator fun <R> get(column: TypedCol<R>) = get(column.name) as R
    operator fun <R> get(property: KProperty<R>) = get(property.name) as R
    operator fun <R> TypedCol<R>.invoke() = get(this)
    operator fun <R> KProperty1<*,R>.invoke() = get(this)
    operator fun <R> String.invoke() = get(this) as R

    fun <T> read(name: String) = get(name) as T
    val values: List<Any?>

    fun int(name: String) = read<Int>(name)
    fun nint(name: String) = read<Int?>(name)
    fun string(name: String) = read<String>(name)
    fun nstring(name: String) = read<String?>(name)
    fun double(name: String) = read<Double>(name)
    fun ndouble(name: String) = read<Double?>(name)

    fun forwardIterable() = this.toIterable { it.next }
    fun backwardIterable() = this.toIterable { it.prev }

    operator fun <R : Comparable<R>> TypedCol<R>.compareTo(other: R) = get(this).compareTo(other)
    operator fun <R : Comparable<R>> KProperty1<*, R>.compareTo(other: R) = get(this).compareTo(other)
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
    infix fun <R> KProperty1<*,R>.eq(a: R?) = get(this) == a
    infix fun <R> TypedCol<R>.neq(a: R?) = get(this) != a
    infix fun <R> KProperty1<*,R>.neq(a: R?) = get(this) != a
}

internal class TypedDataFrameRowImpl<T>(override var index: Int, override val owner: TypedDataFrame<T>) : TypedDataFrameRow<T> {

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return owner[name][index]
    }

    override val prev: TypedDataFrameRow<T>?
        get() = if(index > 0) owner[index-1] else null
    override val next: TypedDataFrameRow<T>?
        get() = if(index < owner.nrow-1) owner[index+1] else null

    override fun getRow(index: Int): TypedDataFrameRow<T>? = if(index >= 0 && index < owner.nrow) TypedDataFrameRowImpl(index, owner) else null

    override val values: List<Any?>
        get() = owner.columns.map { it[index] }
}

internal fun <T> T.toIterable(getNext: (T) -> T?) = Iterable<T> {

    object : Iterator<T> {

        var current: T? = null
        var beforeStart = true
        var next: T? = null

        override fun hasNext(): Boolean {
            if (beforeStart) return true
            if (next == null) next = getNext(current!!)
            return next != null
        }

        override fun next(): T {
            if (beforeStart) {
                current = this@toIterable
                beforeStart = false
                return current!!
            }
            current = next ?: getNext(current!!)
            next = null
            return current!!
        }
    }
}
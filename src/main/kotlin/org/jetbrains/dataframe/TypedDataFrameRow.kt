package org.jetbrains.dataframe

import org.jetbrains.dataframe.tracking.ColumnAccessTracker
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface DataFrameRowBase<out T> {
    operator fun get(name: String): Any?
}

interface TypedDataFrameRow<out T>: DataFrameRowBase<T> {
    val owner: TypedDataFrame<T>
    val prev: TypedDataFrameRow<T>?
    val next: TypedDataFrameRow<T>?
    val index: Int
    fun getRow(index: Int): TypedDataFrameRow<T>?
    override operator fun get(name: String): Any?
    operator fun get(columnIndex: Int): Any?
    operator fun <R> get(column: ColumnDef<R>) = get(column.name) as R
    operator fun <R> get(property: KProperty<R>) = get(property.name) as R
    operator fun <R> ColumnDef<R>.invoke() = get(this)
    operator fun <R> KProperty1<*, R>.invoke() = get(this)
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

    operator fun <R : Comparable<R>> ColumnDef<R>.compareTo(other: R) = get(this).compareTo(other)
    operator fun <R : Comparable<R>> KProperty1<*, R>.compareTo(other: R) = get(this).compareTo(other)
    operator fun ColumnDef<Int>.plus(a: Int) = get(this) + a
    operator fun ColumnDef<Long>.plus(a: Long) = get(this) + a
    operator fun ColumnDef<Double>.plus(a: Double) = get(this) + a
    operator fun ColumnDef<String>.plus(a: String) = get(this) + a
    operator fun Int.plus(col: ColumnDef<Int>) = this + get(col)
    operator fun Long.plus(col: ColumnDef<Long>) = this + get(col)
    operator fun Double.plus(col: ColumnDef<Double>) = this + get(col)
    operator fun String.plus(col: ColumnDef<String>) = this + get(col)

    operator fun ColumnDef<Int>.minus(a: Int) = get(this) - a
    operator fun ColumnDef<Long>.minus(a: Long) = get(this) - a
    operator fun ColumnDef<Double>.minus(a: Double) = get(this) - a
    operator fun Int.minus(col: ColumnDef<Int>) = this - get(col)
    operator fun Long.minus(col: ColumnDef<Long>) = this - get(col)
    operator fun Double.minus(col: ColumnDef<Double>) = this - get(col)

    operator fun ColumnDef<Int>.times(a: Int) = get(this) * a
    operator fun ColumnDef<Long>.times(a: Long) = get(this) * a
    operator fun ColumnDef<Double>.times(a: Double) = get(this) * a
    operator fun ColumnDef<Double>.times(a: Int) = get(this) * a
    operator fun ColumnDef<Long>.times(a: Int) = get(this) * a
    operator fun ColumnDef<Double>.times(a: Long) = get(this) * a

    operator fun ColumnDef<Int>.div(a: Int) = get(this) / a
    operator fun ColumnDef<Long>.div(a: Long) = get(this) / a
    operator fun ColumnDef<Double>.div(a: Double) = get(this) / a
    operator fun ColumnDef<Double>.div(a: Int) = get(this) / a
    operator fun ColumnDef<Long>.div(a: Int) = get(this) / a
    operator fun ColumnDef<Double>.div(a: Long) = get(this) / a

    infix fun <R> ColumnDef<R>.eq(a: R?) = get(this) == a
    infix fun <R> KProperty1<*, R>.eq(a: R?) = get(this) == a
    infix fun <R> ColumnDef<R>.neq(a: R?) = get(this) != a
    infix fun <R> KProperty1<*, R>.neq(a: R?) = get(this) != a
}

internal class TypedDataFrameRowImpl<T>(override var index: Int, override val owner: TypedDataFrame<T>) : TypedDataFrameRow<T> {

    override operator fun get(name: String): Any? {
        ColumnAccessTracker.registerColumnAccess(name)
        return owner[name][index]
    }

    override val prev: TypedDataFrameRow<T>?
        get() = if (index > 0) owner[index - 1] else null
    override val next: TypedDataFrameRow<T>?
        get() = if (index < owner.nrow - 1) owner[index + 1] else null

    override fun getRow(index: Int): TypedDataFrameRow<T>? = if (index >= 0 && index < owner.nrow) TypedDataFrameRowImpl(index, owner) else null

    override val values by lazy { owner.columns.map { it[index] } }

    override fun get(columnIndex: Int): Any? {
        val column = owner.columns[columnIndex]
        ColumnAccessTracker.registerColumnAccess(column.name)
        return column[index]
    }

    override fun toString(): String {
        return "{ " + owner.columns.map { "${it.name}:${it[index]}" }.joinToString() + " }"
    }

    override fun equals(other: Any?): Boolean {
        val o = other as? TypedDataFrameRow<T>
        if(o == null) return false
        return values.equals(o.values)
    }

    override fun hashCode() = values.hashCode()
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
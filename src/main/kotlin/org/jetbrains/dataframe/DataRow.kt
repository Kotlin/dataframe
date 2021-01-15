package org.jetbrains.dataframe

import org.jetbrains.dataframe.api.columns.ColumnData
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

interface DataRowBase<out T> {
    operator fun get(name: String): Any?
    fun tryGet(name: String): Any?
}

interface DataRow<out T>: DataRowBase<T> {
    val owner: DataFrame<T>
    val prev: DataRow<T>?
    val next: DataRow<T>?
    val index: Int
    fun getRow(index: Int): DataRow<T>?
    override operator fun get(name: String): Any?
    override fun tryGet(name: String): Any?
    operator fun get(columnIndex: Int): Any?
    operator fun <R> get(column: ColumnDef<R>) = get(column.name()) as R
    operator fun <R> get(column: ColumnData<R>) = column[index]
    operator fun <R> get(property: KProperty<R>) = get(property.name) as R
    operator fun <R> ColumnDef<R>.invoke() = get(this)
    operator fun <R> KProperty1<*, R>.invoke() = get(this)
    operator fun <R> String.invoke() = get(this) as R

    fun <T> read(name: String) = get(name) as T
    fun size() = owner.ncol
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

typealias Selector<T, R> = T.(T) -> R
typealias RowSelector<T, R> = Selector<DataRow<T>, R>
typealias RowFilter<T> = RowSelector<T, Boolean>
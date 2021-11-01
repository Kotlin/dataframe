package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.toIterable
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

public interface DataRow<out T> {

    public fun index(): Int

    public fun df(): DataFrame<T>
    public fun prev(): DataRow<T>?
    public fun next(): DataRow<T>?
    public fun getRow(index: Int): DataRow<T>?
    public fun neighbours(relativeIndices: Iterable<Int>): Sequence<DataRow<T>> = relativeIndices.asSequence().mapNotNull { getRow(index + it) }

    public fun ncol(): Int = df().ncol()
    public fun columnNames(): List<String> = df().columnNames()

    // region get cell value

    public operator fun get(columnIndex: Int): Any?
    public operator fun <R> get(expression: RowExpression<T, R>): R = expression(this, this)
    public operator fun <R> get(column: ColumnReference<R>): R
    public operator fun <R> get(columns: List<ColumnReference<R>>): List<R> = columns.map { get(it) }
    public operator fun <R> get(property: KProperty<R>): R = get(property.name) as R
    public operator fun get(first: Column, vararg other: Column): DataRow<T> = owner.get(first, *other)[index]
    public operator fun get(first: String, vararg other: String): DataRow<T> = owner.get(first, *other)[index]
    public operator fun get(path: ColumnPath): Any? = owner.get(path)[index]
    public operator fun get(name: String): Any?
    public fun tryGet(name: String): Any?

    // endregion

    public fun <T> read(name: String): T = get(name) as T
    public fun size(): Int = owner.ncol()
    public fun values(): List<Any?>

    public operator fun String.get(vararg path: String): ColumnPath = ColumnPath(listOf(this) + path)

    public operator fun <R> ColumnReference<R>.invoke(): R = get(this)
    public operator fun <R> String.invoke(): R = this@DataRow.get(this) as R
    public operator fun <R> ColumnPath.invoke(): R = get(this) as R

    public fun String.int(): Int = read(this)
    public fun String.intOrNull(): Int? = read(this)
    public fun String.string(): String = read(this)
    public fun String.stringOrNull(): String? = read(this)
    public fun String.boolean(): Boolean = read(this)
    public fun String.booleanOrNull(): Boolean? = read(this)
    public fun String.double(): Double = read(this)
    public fun String.doubleOrNull(): Double? = read(this)
    public fun String.comparable(): Comparable<Any?> = read(this)
    public fun String.comparableOrNull(): Comparable<Any?>? = read(this)
    public fun String.numberOrNull(): Number? = read(this)

    public fun forwardIterable(): Iterable<DataRow<T>> = this.toIterable { it.next }
    public fun backwardIterable(): Iterable<DataRow<T>> = this.toIterable { it.prev }

    public operator fun <R : Comparable<R>> ColumnReference<R>.compareTo(other: R): Int = get(this).compareTo(other)
    public operator fun ColumnReference<Int>.plus(a: Int): Int = get(this) + a
    public operator fun ColumnReference<Long>.plus(a: Long): Long = get(this) + a
    public operator fun ColumnReference<Double>.plus(a: Double): Double = get(this) + a
    public operator fun ColumnReference<String>.plus(a: String): String = get(this) + a
    public operator fun Int.plus(col: ColumnReference<Int>): Int = this + get(col)
    public operator fun Long.plus(col: ColumnReference<Long>): Long = this + get(col)
    public operator fun Double.plus(col: ColumnReference<Double>): Double = this + get(col)

    public operator fun ColumnReference<Int>.minus(a: Int): Int = get(this) - a
    public operator fun ColumnReference<Long>.minus(a: Long): Long = get(this) - a
    public operator fun ColumnReference<Double>.minus(a: Double): Double = get(this) - a
    public operator fun Int.minus(col: ColumnReference<Int>): Int = this - get(col)
    public operator fun Long.minus(col: ColumnReference<Long>): Long = this - get(col)
    public operator fun Double.minus(col: ColumnReference<Double>): Double = this - get(col)

    public operator fun ColumnReference<Int>.times(a: Int): Int = get(this) * a
    public operator fun ColumnReference<Long>.times(a: Long): Long = get(this) * a
    public operator fun ColumnReference<Double>.times(a: Double): Double = get(this) * a
    public operator fun ColumnReference<Double>.times(a: Int): Double = get(this) * a
    public operator fun ColumnReference<Long>.times(a: Int): Long = get(this) * a
    public operator fun ColumnReference<Double>.times(a: Long): Double = get(this) * a

    public operator fun ColumnReference<Int>.div(a: Int): Int = get(this) / a
    public operator fun ColumnReference<Long>.div(a: Long): Long = get(this) / a
    public operator fun ColumnReference<Double>.div(a: Double): Double = get(this) / a
    public operator fun ColumnReference<Double>.div(a: Int): Double = get(this) / a
    public operator fun ColumnReference<Long>.div(a: Int): Long = get(this) / a
    public operator fun ColumnReference<Double>.div(a: Long): Double = get(this) / a

    public infix fun <R> ColumnReference<R>.eq(a: R?): Boolean = get(this) == a
    public infix fun <R> KProperty1<*, R>.eq(a: R?): Boolean = get(this) == a
    public infix fun <R> ColumnReference<R>.neq(a: R?): Boolean = get(this) != a
    public infix fun <R> KProperty1<*, R>.neq(a: R?): Boolean = get(this) != a

    public companion object {
        public val empty: AnyRow = DataFrame.empty(1)[0]
    }
}

public val AnyRow.values: List<Any?> get() = values()
public val AnyRow.index: Int get() = index()
public val <T> DataRow<T>.prev: DataRow<T>? get() = prev()
public val <T> DataRow<T>.next: DataRow<T>? get() = next()

// TODO: remove
public operator fun Any?.get(column: String): Any? = when (this) {
    is AnyRow -> get(column)
    is AnyCol -> get(column)
    is AnyFrame -> get(column)
    else -> error("Unable to get value by string index from type ${this?.javaClass}")
}

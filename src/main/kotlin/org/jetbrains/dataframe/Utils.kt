package org.jetbrains.dataframe

import java.math.BigDecimal
import kotlin.reflect.KClass

typealias Predicate<T> = (T) -> Boolean

internal infix fun <T> (Predicate<T>).and(other: Predicate<T>): Predicate<T> = { this(it) && other(it) }

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

internal fun <T> List<T>.removeAt(index: Int) = subList(0, index) + subList(index + 1, size)

internal inline fun <reified T> listOfNulls(count: Int) = arrayOfNulls<T>(count).asList()

internal inline fun <reified T: Any> Int.cast() = convert(this, T::class)

internal fun <T: Any> convert(src:Int, targetType: KClass<T>): T = when(targetType) {
    Double::class -> src.toDouble() as T
    Long::class -> src.toLong() as T
    Float::class -> src.toFloat() as T
    BigDecimal::class -> src.toBigDecimal() as T
    else -> throw NotImplementedError("Casting int to ${targetType} is not supported")
}
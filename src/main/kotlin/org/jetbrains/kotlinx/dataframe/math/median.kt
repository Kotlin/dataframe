package org.jetbrains.kotlinx.dataframe.math

import org.jetbrains.kotlinx.dataframe.impl.asList
import java.math.BigDecimal
import kotlin.random.Random
import kotlin.reflect.KType

public inline fun <reified T : Comparable<T>> Iterable<T?>.median(type: KType): T? {
    val list = if (type.isMarkedNullable) filterNotNull() else (this as Iterable<T>).asList()
    val size = list.size
    if (size == 0) return null
    val index = size / 2
    if (index == 0 || size % 2 == 1) return list.quickSelect(index)
    return when (type.classifier) {
        Double::class -> ((list.quickSelect(index-1) as Double + list.quickSelect(index) as Double) / 2.0) as T
        Int::class -> ((list.quickSelect(index-1) as Int + list.quickSelect(index) as Int) / 2) as T
        Long::class -> ((list.quickSelect(index-1) as Long + list.quickSelect(index) as Long) / 2L) as T
        Byte::class -> ((list.quickSelect(index-1) as Byte + list.quickSelect(index) as Byte) / 2).toByte() as T
        BigDecimal::class -> ((list.quickSelect(index-1) as BigDecimal + list.quickSelect(index) as BigDecimal) / BigDecimal(2)) as T
        else -> list.quickSelect(index-1)
    }
}

@PublishedApi
internal fun <T : Comparable<T>> List<T>.quickSelect(k: Int): T {
    if (k < 0 || k >= size) throw IndexOutOfBoundsException("k = $k, size = $size")

    var list = this
    var temp = mutableListOf<T>()
    var less = mutableListOf<T>()
    var k = k
    var greater = mutableListOf<T>()
    while (list.size > 1) {
        var equal = 0
        val x = list.random()
        greater.clear()
        less.clear()
        for (v in list) {
            val comp = v.compareTo(x)
            when {
                comp < 0 -> less.add(v)
                comp > 0 -> greater.add(v)
                else -> equal++
            }
        }
        when {
            k < less.size -> {
                list = less
                less = temp
                temp = list
            }
            k < less.size + equal -> {
                return x
            }
            else -> {
                list = greater
                greater = temp
                temp = list
                k -= less.size + equal
            }
        }
    }
    return list[0]
}

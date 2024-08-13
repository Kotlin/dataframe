@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal class ColumnDataHolderImpl<T>(private val list: List<T>, distinct: Lazy<Set<T>>?) : ColumnDataHolder<T> {

    override val distinct = distinct ?: lazy { list.toSet() }

    override val size: Int get() = list.size

    override fun toSet(): Set<T> = distinct.value

    override fun toList(): List<T> = list

    override fun get(index: Int): T = list[index]

    override fun get(range: IntRange): List<T> = list.subList(range.first, range.last + 1)

    override fun contains(value: T): Boolean = list.contains(value)

    override fun iterator(): Iterator<T> = list.iterator()
}

private val BOOLEAN = typeOf<Boolean>()
private val BYTE = typeOf<Byte>()
private val SHORT = typeOf<Short>()
private val INT = typeOf<Int>()
private val LONG = typeOf<Long>()
private val FLOAT = typeOf<Float>()
private val DOUBLE = typeOf<Double>()
private val CHAR = typeOf<Char>()
private val UBYTE = typeOf<UByte>()
private val USHORT = typeOf<UShort>()
private val UINT = typeOf<UInt>()
private val ULONG = typeOf<ULong>()

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [list].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    list: Collection<T>,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    if (list is ColumnDataHolder<*>) return list as ColumnDataHolder<T>

    try {
        val newList = when (type) {
            BOOLEAN -> (list as Collection<Boolean>).toBooleanArray().asList()
            BYTE -> (list as Collection<Byte>).toByteArray().asList()
            SHORT -> (list as Collection<Short>).toShortArray().asList()
            INT -> (list as Collection<Int>).toIntArray().asList()
            LONG -> (list as Collection<Long>).toLongArray().asList()
            FLOAT -> (list as Collection<Float>).toFloatArray().asList()
            DOUBLE -> (list as Collection<Double>).toDoubleArray().asList()
            CHAR -> (list as Collection<Char>).toCharArray().asList()
            UBYTE -> (list as Collection<UByte>).toUByteArray().asList()
            USHORT -> (list as Collection<UShort>).toUShortArray().asList()
            UINT -> (list as Collection<UInt>).toUIntArray().asList()
            ULONG -> (list as Collection<ULong>).toULongArray().asList()
            else -> list.asList()
        } as List<T>

        return ColumnDataHolderImpl(newList, distinct)
    } catch (e: Exception) {
        throw IllegalArgumentException("Can't create ColumnDataHolder from $list and type $type", e)
    }
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [array].
 * If [array] is an array of primitives, it will be converted to a primitive array first before being
 * wrapped with [asList].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    array: Array<T>,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    try {
        val list = when (type) {
            BOOLEAN -> (array as Array<Boolean>).toBooleanArray().asList()
            BYTE -> (array as Array<Byte>).toByteArray().asList()
            SHORT -> (array as Array<Short>).toShortArray().asList()
            INT -> (array as Array<Int>).toIntArray().asList()
            LONG -> (array as Array<Long>).toLongArray().asList()
            FLOAT -> (array as Array<Float>).toFloatArray().asList()
            DOUBLE -> (array as Array<Double>).toDoubleArray().asList()
            CHAR -> (array as Array<Char>).toCharArray().asList()
            UBYTE -> (array as Array<UByte>).toUByteArray().asList()
            USHORT -> (array as Array<UShort>).toUShortArray().asList()
            UINT -> (array as Array<UInt>).toUIntArray().asList()
            ULONG -> (array as Array<ULong>).toULongArray().asList()
            else -> array.asList()
        } as List<T>

        return ColumnDataHolderImpl(list, distinct)
    } catch (e: Exception) {
        throw IllegalArgumentException(
            "Can't create ColumnDataHolder from $array and mismatching type $type",
            e,
        )
    }
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [primitiveArray].
 * [primitiveArray] must be an array of primitives, returns `null` if something goes wrong.
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    primitiveArray: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    val newList = when {
        type == BOOLEAN && primitiveArray is BooleanArray -> primitiveArray.asList()

        type == BYTE && primitiveArray is ByteArray -> primitiveArray.asList()

        type == SHORT && primitiveArray is ShortArray -> primitiveArray.asList()

        type == INT && primitiveArray is IntArray -> primitiveArray.asList()

        type == LONG && primitiveArray is LongArray -> primitiveArray.asList()

        type == FLOAT && primitiveArray is FloatArray -> primitiveArray.asList()

        type == DOUBLE && primitiveArray is DoubleArray -> primitiveArray.asList()

        type == CHAR && primitiveArray is CharArray -> primitiveArray.asList()

        type == UBYTE && primitiveArray is UByteArray -> primitiveArray.asList()

        type == USHORT && primitiveArray is UShortArray -> primitiveArray.asList()

        type == UINT && primitiveArray is UIntArray -> primitiveArray.asList()

        type == ULONG && primitiveArray is ULongArray -> primitiveArray.asList()

        !primitiveArray.isPrimitiveArray -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from non primitive array $primitiveArray and type $type",
        )

        else -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from primitive array $primitiveArray and type $type",
        )
    } as List<T>

    return ColumnDataHolderImpl(newList, distinct)
}

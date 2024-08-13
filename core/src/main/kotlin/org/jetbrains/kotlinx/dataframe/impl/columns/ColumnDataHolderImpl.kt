@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Using the [ofPrimitiveArray] functions, this can store natively without converting:
 * - [BooleanArray]
 * - [ByteArray]
 * - [ShortArray]
 * - [IntArray]
 * - [LongArray]
 * - [FloatArray]
 * - [DoubleArray]
 * - [CharArray]
 * - [UByteArray]
 * - [UShortArray]
 * - [UIntArray]
 * - [ULongArray]
 *
 * Store with converting to primitive arrays:
 * - [Array][Array]`<`[Boolean][Boolean]`>`
 * - [Array][Array]`<`[Byte][Byte]`>`
 * - [Array][Array]`<`[Short][Short]`>`
 * - [Array][Array]`<`[Int][Int]`>`
 * - [Array][Array]`<`[Long][Long]`>`
 * - [Array][Array]`<`[Float][Float]`>`
 * - [Array][Array]`<`[Double][Double]`>`
 * - [Array][Array]`<`[Char][Char]`>`
 * - [Array][Array]`<`[UByte][UByte]`>`
 * - [Array][Array]`<`[UShort][UShort]`>`
 * - [Array][Array]`<`[UInt][UInt]`>`
 * - [Array][Array]`<`[ULong][ULong]`>`
 * - [Collection][Collection]`<`[Boolean][Boolean]`>`
 * - [Collection][Collection]`<`[Byte][Byte]`>`
 * - [Collection][Collection]`<`[Short][Short]`>`
 * - [Collection][Collection]`<`[Int][Int]`>`
 * - [Collection][Collection]`<`[Long][Long]`>`
 * - [Collection][Collection]`<`[Float][Float]`>`
 * - [Collection][Collection]`<`[Double][Double]`>`
 * - [Collection][Collection]`<`[Char][Char]`>`
 * - [Collection][Collection]`<`[UByte][UByte]`>`
 * - [Collection][Collection]`<`[UShort][UShort]`>`
 * - [Collection][Collection]`<`[UInt][UInt]`>`
 * - [Collection][Collection]`<`[ULong][ULong]`>`
 *
 * Store them as is:
 * - [Array][Array]`<`[Any?][Any]`>`
 * - [Collection][Collection]`<`[Any?][Any]`>`
 *
 */
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

internal val BOOLEAN = typeOf<Boolean>()
internal val BYTE = typeOf<Byte>()
internal val SHORT = typeOf<Short>()
internal val INT = typeOf<Int>()
internal val LONG = typeOf<Long>()
internal val FLOAT = typeOf<Float>()
internal val DOUBLE = typeOf<Double>()
internal val CHAR = typeOf<Char>()
internal val UBYTE = typeOf<UByte>()
internal val USHORT = typeOf<UShort>()
internal val UINT = typeOf<UInt>()
internal val ULONG = typeOf<ULong>()

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [collection].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.ofCollection(
    collection: Collection<T>,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> {
    if (collection is ColumnDataHolder<*>) return collection as ColumnDataHolder<T>

    try {
        val newList = when (type) {
            BOOLEAN -> (collection as Collection<Boolean>).toBooleanArray().asList()
            BYTE -> (collection as Collection<Byte>).toByteArray().asList()
            SHORT -> (collection as Collection<Short>).toShortArray().asList()
            INT -> (collection as Collection<Int>).toIntArray().asList()
            LONG -> (collection as Collection<Long>).toLongArray().asList()
            FLOAT -> (collection as Collection<Float>).toFloatArray().asList()
            DOUBLE -> (collection as Collection<Double>).toDoubleArray().asList()
            CHAR -> (collection as Collection<Char>).toCharArray().asList()
            UBYTE -> (collection as Collection<UByte>).toUByteArray().asList()
            USHORT -> (collection as Collection<UShort>).toUShortArray().asList()
            UINT -> (collection as Collection<UInt>).toUIntArray().asList()
            ULONG -> (collection as Collection<ULong>).toULongArray().asList()
            else -> collection.asList()
        } as List<T>

        return ColumnDataHolderImpl(newList, distinct)
    } catch (e: Exception) {
        throw IllegalArgumentException("Can't create ColumnDataHolder from $collection and type $type", e)
    }
}

/**
 * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [array].
 * If [array] is an array of primitives, it will be converted to a primitive array first before being
 * wrapped with [asList].
 */
@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.ofBoxedArray(
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
internal fun <T> ColumnDataHolder.Companion.ofPrimitiveArray(
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

@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    any: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    when {
        any.isPrimitiveArray -> ofPrimitiveArray(primitiveArray = any, type = type, distinct = distinct)
        any.isArray -> ofBoxedArray(array = any as Array<T>, type = type, distinct = distinct)
        any is Collection<*> -> ofCollection(collection = any as Collection<T>, type = type, distinct = distinct)
        else -> throw IllegalArgumentException("Can't create ColumnDataHolder from $any and type $type")
    }

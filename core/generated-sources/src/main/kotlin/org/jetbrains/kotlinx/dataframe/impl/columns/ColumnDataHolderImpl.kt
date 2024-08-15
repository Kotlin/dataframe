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
 * - [Array][Array]`<`[Boolean?][Boolean]`>`
 * - [Array][Array]`<`[Byte?][Byte]`>`
 * - [Array][Array]`<`[Short?][Short]`>`
 * - [Array][Array]`<`[Int?][Int]`>`
 * - [Array][Array]`<`[Long?][Long]`>`
 * - [Array][Array]`<`[Float?][Float]`>`
 * - [Array][Array]`<`[Double?][Double]`>`
 * - [Array][Array]`<`[Char?][Char]`>`
 * - [Array][Array]`<`[UByte?][UByte]`>`
 * - [Array][Array]`<`[UShort?][UShort]`>`
 * - [Array][Array]`<`[UInt?][UInt]`>`
 * - [Array][Array]`<`[ULong?][ULong]`>`
 * - [Collection][Collection]`<`[Boolean?][Boolean]`>`
 * - [Collection][Collection]`<`[Byte?][Byte]`>`
 * - [Collection][Collection]`<`[Short?][Short]`>`
 * - [Collection][Collection]`<`[Int?][Int]`>`
 * - [Collection][Collection]`<`[Long?][Long]`>`
 * - [Collection][Collection]`<`[Float?][Float]`>`
 * - [Collection][Collection]`<`[Double?][Double]`>`
 * - [Collection][Collection]`<`[Char?][Char]`>`
 * - [Collection][Collection]`<`[UByte?][UByte]`>`
 * - [Collection][Collection]`<`[UShort?][UShort]`>`
 * - [Collection][Collection]`<`[UInt?][UInt]`>`
 * - [Collection][Collection]`<`[ULong?][ULong]`>`
 *
 * Yes, as you can see, also nullable types are supported. The values are stored in primitive arrays,
 * and a separate array is used to store the indices of the null values.
 *
 * Since, [ColumnDataHolder] can be used as a [List], this is invisible to the user.
 *
 * Store them as is:
 * - [Array][Array]`<`[Any?][Any]`>`
 * - [Collection][Collection]`<`[Any?][Any]`>`
 *
 */
internal class ColumnDataHolderImpl<T>(
    private val list: List<T>,
    distinct: Lazy<Set<T>>?,
    private val nullIndices: SortedIntArray,
//    private val nullIndices: MyBooleanArray,
) : ColumnDataHolder<T> {

    override val distinct = distinct ?: lazy {
        buildSet {
            var anyNull = false
            for (i in list.indices) {
                if (i in nullIndices) {
//                if (nullIndices[i]) {
                    anyNull = true
                } else {
                    add(list[i])
                }
            }
            if (anyNull) add(null as T)
        }
    }

    override val size: Int get() = list.size

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun indexOf(element: T): Int {
        if (element == null) return nullIndices.first()
//        if (element == null) return nullIndices.indexOf(true)
        for (i in list.indices) {
            if (i in nullIndices) continue
//            if (nullIndices[i]) continue
            if (list[i] == element) return i
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean = elements.toSet().all { contains(it) }

    override fun toSet(): Set<T> = distinct.value

    override fun get(index: Int): T =
        if (index in nullIndices) {
//        if (nullIndices[index]) {
            null as T
        } else {
            list[index]
        }

    override fun get(range: IntRange): List<T> {
        val start = range.first
        val sublist = list.subList(start, range.last + 1).toMutableList()
        for (i in sublist.indices) {
            if (start + i in nullIndices) sublist[i] = null as T
//            if (nullIndices[start + i]) sublist[i] = null as T
        }
        return sublist
    }

    override fun contains(element: T): Boolean =
        if (element == null) {
            nullIndices.isNotEmpty()
//            true in nullIndices
        } else {
            element in list
        }

    override fun iterator(): Iterator<T> = listIterator()

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> =
        object : ListIterator<T> {

            val iterator = list.listIterator(index)

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun hasPrevious(): Boolean = iterator.hasNext()

            override fun next(): T {
                val i = nextIndex()
                val res = iterator.next()
                return if (i in nullIndices) null as T else res
//                return if (nullIndices[i]) null as T else res
            }

            override fun nextIndex(): Int = iterator.nextIndex()

            override fun previous(): T {
                val i = previousIndex()
                val res = iterator.previous()
                return if (i in nullIndices) null as T else res
//                return if (nullIndices[i]) null as T else res
            }

            override fun previousIndex(): Int = iterator.previousIndex()
        }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = get(fromIndex..<toIndex)

    override fun lastIndexOf(element: T): Int {
        if (element == null) return nullIndices.last()
//        if (element == null) return nullIndices.lastIndexOf(true)
        for (i in list.indices.reversed()) {
            if (i in nullIndices) continue
//            if (nullIndices[i]) continue
            if (list[i] == element) return i
        }
        return -1
    }

//    // TODO optimize
//    override fun equals(other: Any?): Boolean = toList() == other
//
//    // TODO optimize
//    override fun hashCode(): Int = toList().hashCode()

    override fun toString(): String = (this as Iterable<T>).joinToString(prefix = "[", postfix = "]")
}

@JvmInline
internal value class SortedIntArray(val array: IntArray = intArrayOf()) : Collection<Int> {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()

    override fun iterator(): Iterator<Int> = array.iterator()

    override fun containsAll(elements: Collection<Int>): Boolean = elements.all { contains(it) }

    override fun contains(element: Int): Boolean = array.binarySearch(element) >= 0
}

@JvmInline
internal value class MyBooleanArray(val array: BooleanArray = booleanArrayOf()) : Collection<Boolean> {

    operator fun get(index: Int): Boolean =
        if (isEmpty()) {
            false
        } else {
            array[index]
        }

    operator fun set(index: Int, value: Boolean) {
        array[index] = value
    }

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()

    override fun iterator(): Iterator<Boolean> = array.iterator()

    override fun containsAll(elements: Collection<Boolean>): Boolean = elements.all { contains(it) }

    override fun contains(element: Boolean): Boolean = element in array
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

internal val NULLABLE_BOOLEAN = typeOf<Boolean?>()
internal val NULLABLE_BYTE = typeOf<Byte?>()
internal val NULLABLE_SHORT = typeOf<Short?>()
internal val NULLABLE_INT = typeOf<Int?>()
internal val NULLABLE_LONG = typeOf<Long?>()
internal val NULLABLE_FLOAT = typeOf<Float?>()
internal val NULLABLE_DOUBLE = typeOf<Double?>()
internal val NULLABLE_CHAR = typeOf<Char?>()
internal val NULLABLE_UBYTE = typeOf<UByte?>()
internal val NULLABLE_USHORT = typeOf<UShort?>()
internal val NULLABLE_UINT = typeOf<UInt?>()
internal val NULLABLE_ULONG = typeOf<ULong?>()

internal fun zeroValueOf(type: KType): Any? =
    when (type) {
        NULLABLE_BOOLEAN, BOOLEAN -> false
        NULLABLE_BYTE, BYTE -> 0.toByte()
        NULLABLE_SHORT, SHORT -> 0.toShort()
        NULLABLE_INT, INT -> 0
        NULLABLE_LONG, LONG -> 0L
        NULLABLE_FLOAT, FLOAT -> 0.0f
        NULLABLE_DOUBLE, DOUBLE -> 0.0
        NULLABLE_CHAR, CHAR -> 0.toChar()
        NULLABLE_UBYTE, UBYTE -> 0.toUByte()
        NULLABLE_USHORT, USHORT -> 0.toUShort()
        NULLABLE_UINT, UINT -> 0.toUInt()
        NULLABLE_ULONG, ULONG -> 0.toULong()
        else -> null
    }

private fun <T> Array<T?>.fillNulls(zeroValue: Any, nullIndices: BooleanArray): Array<T> {
    for (i in indices) {
        if (this[i] == null) {
            this[i] = zeroValue as T
            nullIndices[i] = true
        }
    }
    return this as Array<T>
}

private fun <T> MutableList<T?>.fillNulls(zeroValue: Any, nullIndices: BooleanArray): List<T> {
    for (i in indices) {
        if (this[i] == null) {
            this[i] = zeroValue as T
            nullIndices[i] = true
        }
    }
    return this as List<T>
}

private fun BooleanArray.indicesWhereTrue(): SortedIntArray {
    val array = IntArray(count { it })
    var j = 0
    for (i in indices) {
        if (this[i]) {
            array[j++] = i
        }
    }
    return SortedIntArray(array)
}

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
        val isNull = BooleanArray(collection.size)
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

            NULLABLE_BOOLEAN -> (collection as Collection<Boolean?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toBooleanArray()
                .asList()

            NULLABLE_BYTE -> (collection as Collection<Byte?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toByteArray()
                .asList()

            NULLABLE_SHORT -> (collection as Collection<Short?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toShortArray()
                .asList()

            NULLABLE_INT -> (collection as Collection<Int?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toIntArray()
                .asList()

            NULLABLE_LONG -> (collection as Collection<Long?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toLongArray()
                .asList()

            NULLABLE_FLOAT -> (collection as Collection<Float?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toFloatArray()
                .asList()

            NULLABLE_DOUBLE -> (collection as Collection<Double?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toDoubleArray()
                .asList()

            NULLABLE_CHAR -> (collection as Collection<Char?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toCharArray()
                .asList()

            NULLABLE_UBYTE -> (collection as Collection<UByte?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUByteArray()
                .asList()

            NULLABLE_USHORT -> (collection as Collection<UShort?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUShortArray()
                .asList()

            NULLABLE_UINT -> (collection as Collection<UInt?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUIntArray()
                .asList()

            NULLABLE_ULONG -> (collection as Collection<ULong?>)
                .toMutableList()
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toULongArray()
                .asList()

            else -> collection.asList()
        } as List<T>

        return ColumnDataHolderImpl(newList, distinct, isNull.indicesWhereTrue())
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
        val isNull = BooleanArray(array.size)
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

            NULLABLE_BOOLEAN -> (array as Array<Boolean?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toBooleanArray()
                .asList()

            NULLABLE_BYTE -> (array as Array<Byte?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toByteArray()
                .asList()

            NULLABLE_SHORT -> (array as Array<Short?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toShortArray()
                .asList()

            NULLABLE_INT -> (array as Array<Int?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toIntArray()
                .asList()

            NULLABLE_LONG -> (array as Array<Long?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toLongArray()
                .asList()

            NULLABLE_FLOAT -> (array as Array<Float?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toFloatArray()
                .asList()

            NULLABLE_DOUBLE -> (array as Array<Double?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toDoubleArray()
                .asList()

            NULLABLE_CHAR -> (array as Array<Char?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toCharArray()
                .asList()

            NULLABLE_UBYTE -> (array as Array<UByte?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUByteArray()
                .asList()

            NULLABLE_USHORT -> (array as Array<UShort?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUShortArray()
                .asList()

            NULLABLE_UINT -> (array as Array<UInt?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toUIntArray()
                .asList()

            NULLABLE_ULONG -> (array as Array<ULong?>)
                .fillNulls(zeroValueOf(type)!!, isNull)
                .toULongArray()
                .asList()

            else -> array.asList()
        } as List<T>

        return ColumnDataHolderImpl(list, distinct, isNull.indicesWhereTrue())
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

    return ColumnDataHolderImpl(newList, distinct, /*BooleanArray(newList.size)*/SortedIntArray())
}

@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    any: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    when {
        any is ColumnDataHolder<*> -> any as ColumnDataHolder<T>
        any.isPrimitiveArray -> ofPrimitiveArray(primitiveArray = any, type = type, distinct = distinct)
        any.isArray -> ofBoxedArray(array = any as Array<T>, type = type, distinct = distinct)
        any is Collection<*> -> ofCollection(collection = any as Collection<T>, type = type, distinct = distinct)
        else -> throw IllegalArgumentException("Can't create ColumnDataHolder from $any and type $type")
    }

@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl.columns

import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.bytes.ByteArrayList
import it.unimi.dsi.fastutil.chars.CharArrayList
import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.floats.FloatArrayList
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntSortedSet
import it.unimi.dsi.fastutil.longs.LongArrayList
import it.unimi.dsi.fastutil.shorts.ShortArrayList
import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isArray
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
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
 * - [Collection][Collection]`<`[Boolean?][Boolean]`>`
 * - [Collection][Collection]`<`[Byte?][Byte]`>`
 * - [Collection][Collection]`<`[Short?][Short]`>`
 * - [Collection][Collection]`<`[Int?][Int]`>`
 * - [Collection][Collection]`<`[Long?][Long]`>`
 * - [Collection][Collection]`<`[Float?][Float]`>`
 * - [Collection][Collection]`<`[Double?][Double]`>`
 * - [Collection][Collection]`<`[Char?][Char]`>`
 *
 * Yes, as you can see, also nullable types are supported. The values are stored in primitive arrays,
 * and a separate set is used to store the indices of the null values.
 *
 * Since, [ColumnDataHolder] can be used as a [List], this is invisible to the user.
 *
 * @param T the type of the elements in the column
 * @param list a [PrimitiveArrayList] or any other [MutableList] that can store the elements
 * @param zeroValue When [list] is a [PrimitiveArrayList], this is the zero value for the primitive type
 * @param nullIndices a set of indices where the null values are stored, only used if [list] is a [PrimitiveArrayList]
 */
internal class ColumnDataHolderImpl<T>(
    private var list: MutableList<T> = PrimitiveArrayList<Any>() as MutableList<T>,
    distinct: Lazy<Set<T>>? = null,
    private var zeroValue: Any? = Undefined,
    private val nullIndices: IntSortedSet = IntAVLTreeSet(),
) : ColumnDataHolder<T> {

    private object Undefined

    private fun IntSortedSet.fastContains(index: Int): Boolean =
        when (size) {
            0 -> false
            1 -> firstInt() == index
            2 -> firstInt() == index || lastInt() == index
            else -> contains(index)
        }

    override val distinct = distinct ?: lazy {
        buildSet {
            addAll(this@ColumnDataHolderImpl)
        }.toMutableSet()
    }

    override val size: Int get() = leadingNulls + list.size

    override var usesPrimitiveArrayList = list is PrimitiveArrayList<*>

    override fun canAddPrimitively(element: Any?): Boolean =
        when {
            !usesPrimitiveArrayList -> false
            element == null -> true
            list is PrimitiveArrayList<*> -> (list as PrimitiveArrayList<*>).canAdd(element)
            else -> false
        }

    // for when the list cannot be initialized yet, keeps track of potential leading null values
    private var leadingNulls = 0

    override fun add(element: T) {
        // check if we need to switch to a boxed mutable list to add this element
        if (usesPrimitiveArrayList &&
            element != null &&
            !(list as PrimitiveArrayList<*>).canAdd(element)
        ) {
            list = this.toMutableList()
            leadingNulls = 0
            usesPrimitiveArrayList = false
            nullIndices.clear()
        }

        if (distinct.isInitialized()) {
            distinct.value as MutableSet<T> += element
        }

        if (!usesPrimitiveArrayList) {
            list += element
        } else if (element == null) {
            nullIndices += size
            if (zeroValue is Undefined) {
                leadingNulls++
            } else {
                list += zeroValue as T
            }
        } else {
            // set a new zeroValue if the current one is unset
            if (zeroValue is Undefined) {
                zeroValue = zeroValueFor(element)
                while (leadingNulls > 0) {
                    list += zeroValue as T
                    leadingNulls--
                }
            }

            list += element
        }
    }

    override fun isEmpty(): Boolean = size == 0

    override fun indexOf(element: T): Int {
        if (!usesPrimitiveArrayList) return list.indexOf(element)

        if (element == null) return nullIndices.firstInt()
        for (i in list.indices) {
            if (nullIndices.fastContains(i)) continue
            if (list[i] == element) return i
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean = elements.toSet().all { contains(it) }

    override fun toSet(): Set<T> = distinct.value

    override fun get(index: Int): T =
        when {
            usesPrimitiveArrayList && nullIndices.fastContains(index) -> null as T
            leadingNulls > 0 && index < leadingNulls -> null as T
            else -> list[index]
        }

    override fun get(range: IntRange): List<T> {
        if (!usesPrimitiveArrayList) {
            return list.subList(range.first, range.last + 1)
        }
        if (leadingNulls > 0 && range.first >= 0 && range.last < leadingNulls) {
            return List(range.last - range.first + 1) { null as T }
        }

        val start = range.first
        val sublist = list.subList(start, range.last + 1).toMutableList()
        for (i in sublist.indices) {
            if (nullIndices.fastContains(start + i)) sublist[i] = null as T
        }
        return sublist
    }

    override fun contains(element: T): Boolean =
        if (usesPrimitiveArrayList && element == null) {
            nullIndices.isNotEmpty()
        } else {
            element in list
        }

    override fun iterator(): Iterator<T> = listIterator()

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun listIterator(index: Int): ListIterator<T> =
        when {
            !usesPrimitiveArrayList -> list.listIterator(index)

            leadingNulls > 0 -> List(leadingNulls) { null as T }.listIterator(index)

            else -> object : ListIterator<T> {

                val iterator = list.listIterator(index)

                override fun hasNext(): Boolean = iterator.hasNext()

                override fun hasPrevious(): Boolean = iterator.hasNext()

                override fun next(): T {
                    val i = nextIndex()
                    val res = iterator.next()
                    return if (nullIndices.fastContains(i)) null as T else res
                }

                override fun nextIndex(): Int = iterator.nextIndex()

                override fun previous(): T {
                    val i = previousIndex()
                    val res = iterator.previous()
                    return if (nullIndices.fastContains(i)) null as T else res
                }

                override fun previousIndex(): Int = iterator.previousIndex()
            }
        }

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = get(fromIndex..<toIndex)

    override fun lastIndexOf(element: T): Int {
        if (element == null) return nullIndices.lastInt()
        for (i in list.indices.reversed()) {
            if (nullIndices.fastContains(i)) continue
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
internal val ANY = typeOf<Any>()

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
internal val NULLABLE_ANY = typeOf<Any?>()

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

internal fun zeroValueFor(element: Any?): Any? =
    when (element) {
        null -> null
        is Boolean -> false
        is Byte -> 0.toByte()
        is Short -> 0.toShort()
        is Int -> 0
        is Long -> 0L
        is Float -> 0.0f
        is Double -> 0.0
        is Char -> 0.toChar()
        is UByte -> 0.toUByte()
        is UShort -> 0.toUShort()
        is UInt -> 0.toUInt()
        is ULong -> 0.toULong()
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

private fun BooleanArray.indicesWhereTrue(): IntSortedSet {
    val set = IntAVLTreeSet()
    for (i in indices) {
        if (this[i]) set += i
    }
    return set
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
            BOOLEAN -> BooleanArrayList((collection as Collection<Boolean>).toBooleanArray()).asPrimitiveArrayList()

            BYTE -> ByteArrayList((collection as Collection<Byte>).toByteArray()).asPrimitiveArrayList()

            SHORT -> ShortArrayList((collection as Collection<Short>).toShortArray()).asPrimitiveArrayList()

            INT -> IntArrayList((collection as Collection<Int>).toIntArray()).asPrimitiveArrayList()

            LONG -> LongArrayList((collection as Collection<Long>).toLongArray()).asPrimitiveArrayList()

            FLOAT -> FloatArrayList((collection as Collection<Float>).toFloatArray()).asPrimitiveArrayList()

            DOUBLE -> DoubleArrayList((collection as Collection<Double>).toDoubleArray()).asPrimitiveArrayList()

            CHAR -> CharArrayList((collection as Collection<Char>).toCharArray()).asPrimitiveArrayList()

//            UBYTE -> (collection as Collection<UByte>).toUByteArray().asList()
//
//            USHORT -> (collection as Collection<UShort>).toUShortArray().asList()
//
//            UINT -> (collection as Collection<UInt>).toUIntArray().asList()
//
//            ULONG -> (collection as Collection<ULong>).toULongArray().asList()

            NULLABLE_BOOLEAN -> BooleanArrayList(
                (collection as Collection<Boolean?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toBooleanArray(),
            ).asPrimitiveArrayList()

            NULLABLE_BYTE -> ByteArrayList(
                (collection as Collection<Byte?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toByteArray(),
            ).asPrimitiveArrayList()

            NULLABLE_SHORT -> ShortArrayList(
                (collection as Collection<Short?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toShortArray(),
            ).asPrimitiveArrayList()

            NULLABLE_INT -> IntArrayList(
                (collection as Collection<Int?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toIntArray(),
            ).asPrimitiveArrayList()

            NULLABLE_LONG -> LongArrayList(
                (collection as Collection<Long?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toLongArray(),
            ).asPrimitiveArrayList()

            NULLABLE_FLOAT -> FloatArrayList(
                (collection as Collection<Float?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toFloatArray(),
            ).asPrimitiveArrayList()

            NULLABLE_DOUBLE -> DoubleArrayList(
                (collection as Collection<Double?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toDoubleArray(),
            ).asPrimitiveArrayList()

            NULLABLE_CHAR -> CharArrayList(
                (collection as Collection<Char?>)
                    .toMutableList()
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toCharArray(),
            ).asPrimitiveArrayList()

//            NULLABLE_UBYTE -> (collection as Collection<UByte?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUByteArray()
//                .asList()
//
//            NULLABLE_USHORT -> (collection as Collection<UShort?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUShortArray()
//                .asList()
//
//            NULLABLE_UINT -> (collection as Collection<UInt?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUIntArray()
//                .asList()
//
//            NULLABLE_ULONG -> (collection as Collection<ULong?>)
//                .toMutableList()
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toULongArray()
//                .asList()

            else -> {
                for ((i, it) in collection.withIndex()) {
                    if (it == null) isNull[i] = true
                }
                collection.toMutableList()
            }
        } as MutableList<T>

        return ColumnDataHolderImpl(
            list = newList,
            distinct = distinct,
            zeroValue = zeroValueOf(type) as T,
            nullIndices = if (newList is PrimitiveArrayList<*>) isNull.indicesWhereTrue() else IntAVLTreeSet(),
        )
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
            BOOLEAN -> BooleanArrayList((array as Array<Boolean>).toBooleanArray()).asPrimitiveArrayList()

            BYTE -> ByteArrayList((array as Array<Byte>).toByteArray()).asPrimitiveArrayList()

            SHORT -> ShortArrayList((array as Array<Short>).toShortArray()).asPrimitiveArrayList()

            INT -> IntArrayList((array as Array<Int>).toIntArray()).asPrimitiveArrayList()

            LONG -> LongArrayList((array as Array<Long>).toLongArray()).asPrimitiveArrayList()

            FLOAT -> FloatArrayList((array as Array<Float>).toFloatArray()).asPrimitiveArrayList()

            DOUBLE -> DoubleArrayList((array as Array<Double>).toDoubleArray()).asPrimitiveArrayList()

            CHAR -> CharArrayList((array as Array<Char>).toCharArray()).asPrimitiveArrayList()

//            UBYTE -> (array as Array<UByte>).toUByteArray().asList()
//
//            USHORT -> (array as Array<UShort>).toUShortArray().asList()
//
//            UINT -> (array as Array<UInt>).toUIntArray().asList()
//
//            ULONG -> (array as Array<ULong>).toULongArray().asList()

            NULLABLE_BOOLEAN -> BooleanArrayList(
                (array as Array<Boolean?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toBooleanArray(),
            ).asPrimitiveArrayList()

            NULLABLE_BYTE -> ByteArrayList(
                (array as Array<Byte?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toByteArray(),
            ).asPrimitiveArrayList()

            NULLABLE_SHORT -> ShortArrayList(
                (array as Array<Short?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toShortArray(),
            ).asPrimitiveArrayList()

            NULLABLE_INT -> IntArrayList(
                (array as Array<Int?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toIntArray(),
            ).asPrimitiveArrayList()

            NULLABLE_LONG -> LongArrayList(
                (array as Array<Long?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toLongArray(),
            ).asPrimitiveArrayList()

            NULLABLE_FLOAT -> FloatArrayList(
                (array as Array<Float?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toFloatArray(),
            ).asPrimitiveArrayList()

            NULLABLE_DOUBLE -> DoubleArrayList(
                (array as Array<Double?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toDoubleArray(),
            ).asPrimitiveArrayList()

            NULLABLE_CHAR -> CharArrayList(
                (array as Array<Char?>)
                    .fillNulls(zeroValueOf(type)!!, isNull)
                    .toCharArray(),
            ).asPrimitiveArrayList()

//            NULLABLE_UBYTE -> (array as Array<UByte?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUByteArray()
//                .asList()
//
//            NULLABLE_USHORT -> (array as Array<UShort?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUShortArray()
//                .asList()
//
//            NULLABLE_UINT -> (array as Array<UInt?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toUIntArray()
//                .asList()
//
//            NULLABLE_ULONG -> (array as Array<ULong?>)
//                .fillNulls(zeroValueOf(type)!!, isNull)
//                .toULongArray()
//                .asList()

            else -> {
                for ((i, it) in array.withIndex()) {
                    if (it == null) isNull[i] = true
                }
                array.toMutableList()
            }
        } as MutableList<T>

        return ColumnDataHolderImpl(
            list = list,
            distinct = distinct,
            zeroValue = zeroValueOf(type),
            nullIndices = if (list is PrimitiveArrayList<*>) isNull.indicesWhereTrue() else IntAVLTreeSet(),
        )
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
        type == BOOLEAN && primitiveArray is BooleanArray -> BooleanArrayList(primitiveArray).asPrimitiveArrayList()

        type == BYTE && primitiveArray is ByteArray -> ByteArrayList(primitiveArray).asPrimitiveArrayList()

        type == SHORT && primitiveArray is ShortArray -> ShortArrayList(primitiveArray).asPrimitiveArrayList()

        type == INT && primitiveArray is IntArray -> IntArrayList(primitiveArray).asPrimitiveArrayList()

        type == LONG && primitiveArray is LongArray -> LongArrayList(primitiveArray).asPrimitiveArrayList()

        type == FLOAT && primitiveArray is FloatArray -> FloatArrayList(primitiveArray).asPrimitiveArrayList()

        type == DOUBLE && primitiveArray is DoubleArray -> DoubleArrayList(primitiveArray).asPrimitiveArrayList()

        type == CHAR && primitiveArray is CharArray -> CharArrayList(primitiveArray).asPrimitiveArrayList()

//        type == UBYTE && primitiveArray is UByteArray -> primitiveArray.asList()
//
//        type == USHORT && primitiveArray is UShortArray -> primitiveArray.asList()
//
//        type == UINT && primitiveArray is UIntArray -> primitiveArray.asList()
//
//        type == ULONG && primitiveArray is ULongArray -> primitiveArray.asList()

        !primitiveArray.isPrimitiveArray -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from non primitive array $primitiveArray and type $type",
        )

        else -> throw IllegalArgumentException(
            "Can't create ColumnDataHolder from primitive array $primitiveArray and type $type",
        )
    } as MutableList<T>

    return ColumnDataHolderImpl(
        list = newList,
        distinct = distinct,
        zeroValue = zeroValueOf(type),
    )
}

@Suppress("UNCHECKED_CAST")
internal fun <T> ColumnDataHolder.Companion.of(
    any: Any,
    type: KType,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    when {
        any is ColumnDataHolder<*> -> any as ColumnDataHolder<T>

        any.isPrimitiveArray -> ofPrimitiveArray(
            primitiveArray = any,
            type = type,
            distinct = distinct,
        )

        any.isArray -> ofBoxedArray(
            array = any as Array<T>,
            type = type,
            distinct = distinct,
        )

        any is Collection<*> -> ofCollection(
            collection = any as Collection<T>,
            type = type,
            distinct = distinct,
        )

        else -> throw IllegalArgumentException("Can't create ColumnDataHolder from $any and type $type")
    }

internal fun <T> ColumnDataHolder.Companion.empty(initCapacity: Int = 0): ColumnDataHolder<T> =
    ColumnDataHolderImpl(
        list = PrimitiveArrayList<Any>(initCapacity) as MutableList<T>,
    )

internal fun <T> ColumnDataHolder.Companion.emptyForType(
    type: KType,
    initCapacity: Int = 0,
    distinct: Lazy<Set<T>>? = null,
): ColumnDataHolder<T> =
    ColumnDataHolderImpl(
        list = PrimitiveArrayList.forTypeOrNull<Any>(
            kType = type.withNullability(false),
            initCapacity = initCapacity,
        ) as MutableList<T>? ?: mutableListOf(),
        distinct = distinct,
        zeroValue = zeroValueOf(type),
    )

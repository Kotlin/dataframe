@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.impl.isPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.typeOf

internal class ColumnDataHolderImpl<T> private constructor(
    private val list: List<T>,
    distinct: Lazy<Set<T>>?,
) : ColumnDataHolder<T> {

    override val distinct = distinct ?: lazy { list.toSet() }
    override val size: Int get() = list.size

    override fun toSet(): Set<T> = distinct.value
    override fun toList(): List<T> = list
    override fun get(index: Int): T = list[index]
    override fun get(range: IntRange): List<T> = list.subList(range.first, range.last + 1)
    override fun contains(value: T): Boolean = list.contains(value)
    override fun iterator(): Iterator<T> = list.iterator()

    companion object {

        /**
         * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [list].
         */
        @Suppress("UNCHECKED_CAST")
        internal fun <T> of(list: Collection<T>, type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> {
            if (list is ColumnDataHolder<*>) return list as ColumnDataHolder<T>

            return try {
                when (type) {
                    BOOLEAN -> ColumnDataHolderImpl((list as Collection<Boolean>).toBooleanArray().asList(), distinct)
                    BYTE -> ColumnDataHolderImpl((list as Collection<Byte>).toByteArray().asList(), distinct)
                    SHORT -> ColumnDataHolderImpl((list as Collection<Short>).toShortArray().asList(), distinct)
                    INT -> ColumnDataHolderImpl((list as Collection<Int>).toIntArray().asList(), distinct)
                    LONG -> ColumnDataHolderImpl((list as Collection<Long>).toLongArray().asList(), distinct)
                    FLOAT -> ColumnDataHolderImpl((list as Collection<Float>).toFloatArray().asList(), distinct)
                    DOUBLE -> ColumnDataHolderImpl((list as Collection<Double>).toDoubleArray().asList(), distinct)
                    CHAR -> ColumnDataHolderImpl((list as Collection<Char>).toCharArray().asList(), distinct)
                    UBYTE -> ColumnDataHolderImpl((list as Collection<UByte>).toUByteArray().asList(), distinct)
                    USHORT -> ColumnDataHolderImpl((list as Collection<UShort>).toUShortArray().asList(), distinct)
                    UINT -> ColumnDataHolderImpl((list as Collection<UInt>).toUIntArray().asList(), distinct)
                    ULONG -> ColumnDataHolderImpl((list as Collection<ULong>).toULongArray().asList(), distinct)
                    else -> ColumnDataHolderImpl(list.asList(), distinct)
                } as ColumnDataHolder<T>
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
        internal fun <T> of(array: Array<T>, type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
            try {
                when (type) {
                    BOOLEAN -> ColumnDataHolderImpl((array as Array<Boolean>).toBooleanArray().asList(), distinct)
                    BYTE -> ColumnDataHolderImpl((array as Array<Byte>).toByteArray().asList(), distinct)
                    SHORT -> ColumnDataHolderImpl((array as Array<Short>).toShortArray().asList(), distinct)
                    INT -> ColumnDataHolderImpl((array as Array<Int>).toIntArray().asList(), distinct)
                    LONG -> ColumnDataHolderImpl((array as Array<Long>).toLongArray().asList(), distinct)
                    FLOAT -> ColumnDataHolderImpl((array as Array<Float>).toFloatArray().asList(), distinct)
                    DOUBLE -> ColumnDataHolderImpl((array as Array<Double>).toDoubleArray().asList(), distinct)
                    CHAR -> ColumnDataHolderImpl((array as Array<Char>).toCharArray().asList(), distinct)
                    UBYTE -> ColumnDataHolderImpl((array as Array<UByte>).toUByteArray().asList(), distinct)
                    USHORT -> ColumnDataHolderImpl((array as Array<UShort>).toUShortArray().asList(), distinct)
                    UINT -> ColumnDataHolderImpl((array as Array<UInt>).toUIntArray().asList(), distinct)
                    ULONG -> ColumnDataHolderImpl((array as Array<ULong>).toULongArray().asList(), distinct)
                    else -> ColumnDataHolderImpl(array.asList(), distinct)
                } as ColumnDataHolder<T>
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Can't create ColumnDataHolder from $array and mismatching type $type",
                    e
                )
            }

        /**
         * Constructs [ColumnDataHolderImpl] using an [asList] wrapper around the [primitiveArray].
         * [primitiveArray] must be an array of primitives, returns `null` if something goes wrong.
         */
        @Suppress("UNCHECKED_CAST")
        internal fun <T> of(primitiveArray: Any, type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
            when {
                type == BOOLEAN && primitiveArray is BooleanArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == BYTE && primitiveArray is ByteArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == SHORT && primitiveArray is ShortArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == INT && primitiveArray is IntArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == LONG && primitiveArray is LongArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == FLOAT && primitiveArray is FloatArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == DOUBLE && primitiveArray is DoubleArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == CHAR && primitiveArray is CharArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == UBYTE && primitiveArray is UByteArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == USHORT && primitiveArray is UShortArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == UINT && primitiveArray is UIntArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                type == ULONG && primitiveArray is ULongArray ->
                    ColumnDataHolderImpl(primitiveArray.asList(), distinct)

                !primitiveArray.isPrimitiveArray ->
                    throw IllegalArgumentException(
                        "Can't create ColumnDataHolder from non primitive array $primitiveArray and type $type"
                    )

                else ->
                    throw IllegalArgumentException(
                        "Can't create ColumnDataHolder from primitive array $primitiveArray and type $type"
                    )
            } as ColumnDataHolder<T>
    }
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

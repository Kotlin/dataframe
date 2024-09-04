@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.impl.columns.BOOLEAN
import org.jetbrains.kotlinx.dataframe.impl.columns.BYTE
import org.jetbrains.kotlinx.dataframe.impl.columns.CHAR
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.DOUBLE
import org.jetbrains.kotlinx.dataframe.impl.columns.FLOAT
import org.jetbrains.kotlinx.dataframe.impl.columns.INT
import org.jetbrains.kotlinx.dataframe.impl.columns.LONG
import org.jetbrains.kotlinx.dataframe.impl.columns.SHORT
import org.jetbrains.kotlinx.dataframe.impl.columns.UBYTE
import org.jetbrains.kotlinx.dataframe.impl.columns.UINT
import org.jetbrains.kotlinx.dataframe.impl.columns.ULONG
import org.jetbrains.kotlinx.dataframe.impl.columns.USHORT
import org.jetbrains.kotlinx.dataframe.impl.columns.ofBoxedArray
import org.jetbrains.kotlinx.dataframe.impl.columns.ofCollection
import org.jetbrains.kotlinx.dataframe.impl.columns.ofPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Represents the contents of a column; however, it may be implemented.
 * The default implementation is found at [ColumnDataHolderImpl].
 */
public interface ColumnDataHolder<T> : List<T> {

    public fun toSet(): Set<T>

    public operator fun get(range: IntRange): List<T>

    public fun add(element: T)

    public fun add(boolean: Boolean)

    public fun add(byte: Byte)

    public fun add(short: Short)

    public fun add(int: Int)

    public fun add(long: Long)

    public fun add(float: Float)

    public fun add(double: Double)

    public fun add(char: Char)

    public operator fun set(index: Int, value: T)

    public operator fun set(index: Int, value: Boolean)

    public operator fun set(index: Int, value: Byte)

    public operator fun set(index: Int, value: Short)

    public operator fun set(index: Int, value: Int)

    public operator fun set(index: Int, value: Long)

    public operator fun set(index: Int, value: Float)

    public operator fun set(index: Int, value: Double)

    public operator fun set(index: Int, value: Char)

    public fun isNull(index: Int): Boolean

    public fun hasNulls(): Boolean

    public fun canAddPrimitively(element: Any?): Boolean

    public val distinct: Lazy<Set<T>>

    public val usesPrimitiveArrayList: Boolean

    public companion object
}

public fun <T> Collection<T>.toColumnDataHolder(type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    ColumnDataHolder.ofCollection(this, type, distinct)

public inline fun <reified T> Collection<T>.toColumnDataHolder(distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    this.toColumnDataHolder(typeOf<T>(), distinct)

public fun <T> Array<T>.toColumnDataHolder(type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    ColumnDataHolder.ofBoxedArray(this, type, distinct)

public inline fun <reified T> Array<T>.toColumnDataHolder(distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    this.toColumnDataHolder(typeOf<T>(), distinct)

public fun BooleanArray.asColumnDataHolder(distinct: Lazy<Set<Boolean>>? = null): ColumnDataHolder<Boolean> =
    ColumnDataHolder.ofPrimitiveArray(this, BOOLEAN, distinct)

public fun ByteArray.asColumnDataHolder(distinct: Lazy<Set<Byte>>? = null): ColumnDataHolder<Byte> =
    ColumnDataHolder.ofPrimitiveArray(this, BYTE, distinct)

public fun ShortArray.asColumnDataHolder(distinct: Lazy<Set<Short>>? = null): ColumnDataHolder<Short> =
    ColumnDataHolder.ofPrimitiveArray(this, SHORT, distinct)

public fun IntArray.asColumnDataHolder(distinct: Lazy<Set<Int>>? = null): ColumnDataHolder<Int> =
    ColumnDataHolder.ofPrimitiveArray(this, INT, distinct)

public fun LongArray.asColumnDataHolder(distinct: Lazy<Set<Long>>? = null): ColumnDataHolder<Long> =
    ColumnDataHolder.ofPrimitiveArray(this, LONG, distinct)

public fun FloatArray.asColumnDataHolder(distinct: Lazy<Set<Float>>? = null): ColumnDataHolder<Float> =
    ColumnDataHolder.ofPrimitiveArray(this, FLOAT, distinct)

public fun DoubleArray.asColumnDataHolder(distinct: Lazy<Set<Double>>? = null): ColumnDataHolder<Double> =
    ColumnDataHolder.ofPrimitiveArray(this, DOUBLE, distinct)

public fun CharArray.asColumnDataHolder(distinct: Lazy<Set<Char>>? = null): ColumnDataHolder<Char> =
    ColumnDataHolder.ofPrimitiveArray(this, CHAR, distinct)

public fun UByteArray.asColumnDataHolder(distinct: Lazy<Set<UByte>>? = null): ColumnDataHolder<UByte> =
    ColumnDataHolder.ofPrimitiveArray(this, UBYTE, distinct)

public fun UShortArray.asColumnDataHolder(distinct: Lazy<Set<UShort>>? = null): ColumnDataHolder<UShort> =
    ColumnDataHolder.ofPrimitiveArray(this, USHORT, distinct)

public fun UIntArray.asColumnDataHolder(distinct: Lazy<Set<UInt>>? = null): ColumnDataHolder<UInt> =
    ColumnDataHolder.ofPrimitiveArray(this, UINT, distinct)

public fun ULongArray.asColumnDataHolder(distinct: Lazy<Set<ULong>>? = null): ColumnDataHolder<ULong> =
    ColumnDataHolder.ofPrimitiveArray(this, ULONG, distinct)

@file:OptIn(ExperimentalUnsignedTypes::class)

package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ofBoxedArray
import org.jetbrains.kotlinx.dataframe.impl.columns.ofCollection
import org.jetbrains.kotlinx.dataframe.impl.columns.ofPrimitiveArray
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Represents the contents of a column, however it may be implemented.
 * The default implementation is found at [ColumnDataHolderImpl].
 */
public interface ColumnDataHolder<T> : Iterable<T> {

    public val size: Int

    public fun toSet(): Set<T>

    public fun toList(): List<T>

    public fun contains(value: T): Boolean

    public operator fun get(index: Int): T

    public operator fun get(range: IntRange): List<T>

    public val distinct: Lazy<Set<T>>

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
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Boolean>(), distinct)

public fun ByteArray.asColumnDataHolder(distinct: Lazy<Set<Byte>>? = null): ColumnDataHolder<Byte> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Byte>(), distinct)

public fun ShortArray.asColumnDataHolder(distinct: Lazy<Set<Short>>? = null): ColumnDataHolder<Short> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Short>(), distinct)

public fun IntArray.asColumnDataHolder(distinct: Lazy<Set<Int>>? = null): ColumnDataHolder<Int> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Int>(), distinct)

public fun LongArray.asColumnDataHolder(distinct: Lazy<Set<Long>>? = null): ColumnDataHolder<Long> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Long>(), distinct)

public fun FloatArray.asColumnDataHolder(distinct: Lazy<Set<Float>>? = null): ColumnDataHolder<Float> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Float>(), distinct)

public fun DoubleArray.asColumnDataHolder(distinct: Lazy<Set<Double>>? = null): ColumnDataHolder<Double> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Double>(), distinct)

public fun CharArray.asColumnDataHolder(distinct: Lazy<Set<Char>>? = null): ColumnDataHolder<Char> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<Char>(), distinct)

public fun UByteArray.asColumnDataHolder(distinct: Lazy<Set<UByte>>? = null): ColumnDataHolder<UByte> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<UByte>(), distinct)

public fun UShortArray.asColumnDataHolder(distinct: Lazy<Set<UShort>>? = null): ColumnDataHolder<UShort> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<UShort>(), distinct)

public fun UIntArray.asColumnDataHolder(distinct: Lazy<Set<UInt>>? = null): ColumnDataHolder<UInt> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<UInt>(), distinct)

public fun ULongArray.asColumnDataHolder(distinct: Lazy<Set<ULong>>? = null): ColumnDataHolder<ULong> =
    ColumnDataHolder.ofPrimitiveArray(this, typeOf<ULong>(), distinct)

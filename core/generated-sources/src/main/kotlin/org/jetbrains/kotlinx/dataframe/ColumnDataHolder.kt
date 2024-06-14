package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderImpl
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
    ColumnDataHolderImpl.of(this, type, distinct)

public inline fun <reified T> Collection<T>.toColumnDataHolder(distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    this.toColumnDataHolder(typeOf<T>(), distinct)

public fun <T> Array<T>.toColumnDataHolder(type: KType, distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    ColumnDataHolderImpl.of(this, type, distinct)

public inline fun <reified T> Array<T>.toColumnDataHolder(distinct: Lazy<Set<T>>? = null): ColumnDataHolder<T> =
    this.toColumnDataHolder(typeOf<T>(), distinct)

public fun BooleanArray.asColumnDataHolder(distinct: Lazy<Set<Boolean>>? = null): ColumnDataHolder<Boolean> =
    ColumnDataHolderImpl.of(this, typeOf<Boolean>(), distinct)

public fun ByteArray.asColumnDataHolder(distinct: Lazy<Set<Byte>>? = null): ColumnDataHolder<Byte> =
    ColumnDataHolderImpl.of(this, typeOf<Byte>(), distinct)

public fun ShortArray.asColumnDataHolder(distinct: Lazy<Set<Short>>? = null): ColumnDataHolder<Short> =
    ColumnDataHolderImpl.of(this, typeOf<Short>(), distinct)

public fun IntArray.asColumnDataHolder(distinct: Lazy<Set<Int>>? = null): ColumnDataHolder<Int> =
    ColumnDataHolderImpl.of(this, typeOf<Int>(), distinct)

public fun LongArray.asColumnDataHolder(distinct: Lazy<Set<Long>>? = null): ColumnDataHolder<Long> =
    ColumnDataHolderImpl.of(this, typeOf<Long>(), distinct)

public fun FloatArray.asColumnDataHolder(distinct: Lazy<Set<Float>>? = null): ColumnDataHolder<Float> =
    ColumnDataHolderImpl.of(this, typeOf<Float>(), distinct)

public fun DoubleArray.asColumnDataHolder(distinct: Lazy<Set<Double>>? = null): ColumnDataHolder<Double> =
    ColumnDataHolderImpl.of(this, typeOf<Double>(), distinct)

public fun CharArray.asColumnDataHolder(distinct: Lazy<Set<Char>>? = null): ColumnDataHolder<Char> =
    ColumnDataHolderImpl.of(this, typeOf<Char>(), distinct)

public fun UByteArray.asColumnDataHolder(distinct: Lazy<Set<UByte>>? = null): ColumnDataHolder<UByte> =
    ColumnDataHolderImpl.of(this, typeOf<UByte>(), distinct)

public fun UShortArray.asColumnDataHolder(distinct: Lazy<Set<UShort>>? = null): ColumnDataHolder<UShort> =
    ColumnDataHolderImpl.of(this, typeOf<UShort>(), distinct)

public fun UIntArray.asColumnDataHolder(distinct: Lazy<Set<UInt>>? = null): ColumnDataHolder<UInt> =
    ColumnDataHolderImpl.of(this, typeOf<UInt>(), distinct)

public fun ULongArray.asColumnDataHolder(distinct: Lazy<Set<ULong>>? = null): ColumnDataHolder<ULong> =
    ColumnDataHolderImpl.of(this, typeOf<ULong>(), distinct)

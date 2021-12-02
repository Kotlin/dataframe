package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import kotlin.reflect.KType

internal abstract class DataColumnImpl<T>(
    protected val values: List<T>,
    val name: String,
    val type: KType,
    distinct: Lazy<Set<T>>? = null
) : DataColumn<T>, DataColumnInternal<T> {

    protected val distinct = distinct ?: lazy { values.toSet() }

    override fun name() = name

    override fun values() = values

    override fun type() = type

    override fun toSet() = distinct.value

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = dataFrameOf(this).toString() // "${name()}: $type"

    override fun countDistinct() = toSet().size

    override fun get(index: Int) = values[index]

    override fun size() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    private val hashCode by lazy { getHashCode() }

    override fun hashCode() = hashCode

    override operator fun get(range: IntRange) = createWithValues(values.subList(range.start, range.endInclusive + 1))

    protected abstract fun createWithValues(values: List<T>, hasNulls: Boolean? = null): DataColumn<T>
}

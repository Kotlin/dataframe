package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.DataColumn
import org.jetbrains.dataframe.checkEquals
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.getHashCode
import kotlin.reflect.KType

internal abstract class DataColumnImpl<T>(override val values: List<T>, val name: String, override val type: KType, set: Set<T>? = null) : DataColumn<T>, DataColumnInternal<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun name() = name

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = dataFrameOf(this).toString() // "${name()}: $type"

    override val ndistinct by lazy { toSet().size }

    override fun get(index: Int) = values[index]

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override val size: Int
        get() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun slice(indices: Iterable<Int>): DataColumn<T> {
        var nullable = false
        val newValues = indices.map {
            val value = values[it]
            if(value == null) nullable = true
            value
        }
        return createWithValues(newValues, nullable)
    }

    override fun slice(mask: BooleanArray): DataColumn<T> {
        val res = ArrayList<T?>(size)
        var hasNulls = false
        for(index in 0 until size) {
            if(mask[index]) {
                val value = this[index]
                if(!hasNulls && value == null) hasNulls = true
                res.add(value)
            }
        }
        return createWithValues(res as List<T>, hasNulls)
    }

    override fun slice(range: IntRange) = createWithValues(values.subList(range.start, range.endInclusive + 1))

    protected abstract fun createWithValues(values: List<T>, hasNulls: Boolean? = null): DataColumn<T>
}
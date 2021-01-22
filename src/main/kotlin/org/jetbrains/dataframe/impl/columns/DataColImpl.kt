package org.jetbrains.dataframe.impl.columns

import org.jetbrains.dataframe.api.columns.DataCol
import org.jetbrains.dataframe.checkEquals
import org.jetbrains.dataframe.getHashCode
import kotlin.reflect.KType

internal abstract class DataColImpl<T>(override val values: List<T>, val name: String, override val type: KType, set: Set<T>? = null) : DataCol<T>, DataColInternal<T> {

    var valuesSet: Set<T>? = set
        private set

    override fun name() = name

    override fun toSet() = valuesSet ?: values.toSet().also { valuesSet = it }

    fun contains(value: T) = toSet().contains(value)

    override fun toString() = "${name()}: $type"

    override val ndistinct = toSet().size

    override fun get(index: Int) = values[index]

    override fun get(columnName: String) = throw UnsupportedOperationException()

    override val size: Int
        get() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    override fun hashCode() = getHashCode()

    override fun slice(indices: Iterable<Int>): DataCol<T> {
        var nullable = false
        val newValues = indices.map { get(it).also { if (it == null) nullable = true } }
        return createWithValues(newValues, nullable)
    }

    override fun slice(mask: BooleanArray): DataCol<T> {
        var nullable = false
        val newValues = values.filterIndexed { index, value -> mask[index].also { if (it && value == null) nullable = true } }
        return createWithValues(newValues, nullable)
    }

    override fun slice(range: IntRange) = createWithValues(values.subList(range.start, range.endInclusive + 1))

    protected abstract fun createWithValues(values: List<T>, hasNulls: Boolean? = null): DataCol<T>
}
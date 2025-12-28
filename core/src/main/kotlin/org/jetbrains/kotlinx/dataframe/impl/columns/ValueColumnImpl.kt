package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

@JvmInline
internal value class StatisticResult(val value: Any?)

public class ParameterValue(public val parameter: Any?) {

    override fun equals(other: Any?): Boolean {
        val otherAsParameterValue = other as ParameterValue?
        val that = otherAsParameterValue?.parameter
        if (parameter is Boolean && that is Boolean) {
            return this.parameter == that
        }
        if (parameter is Double && that is Double) {
            return this.parameter == that
        }
        if (parameter is Int && that is Int) {
            return this.parameter == that
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        if (parameter is Boolean) {
            return this.parameter.hashCode()
        }
        if (parameter is Double) {
            return this.parameter.hashCode()
        }
        if (parameter is Int) {
            return this.parameter.hashCode()
        }
        return super.hashCode()
    }
}

internal interface ValueColumnInternal<T> : ValueColumn<T> {
    val statistics: MutableMap<String, MutableMap<Map<String, ParameterValue?>, StatisticResult>>
}

internal open class ValueColumnImpl<T>(
    values: List<T>,
    name: String,
    type: KType,
    val defaultValue: T? = null,
    distinct: Lazy<Set<T>>? = null,
) : DataColumnImpl<T>(values, name, type, distinct),
    ValueColumn<T>,
    ValueColumnInternal<T> {

    override fun distinct() = ValueColumnImpl(toSet().toList(), name, type, defaultValue, distinct)

    override fun rename(newName: String) = ValueColumnImpl(values, newName, type, defaultValue, distinct)

    override fun changeType(type: KType) = ValueColumnImpl(values, name, type, defaultValue, distinct)

    override fun addParent(parent: ColumnGroup<*>): DataColumn<T> = ValueColumnWithParent(parent, this)

    override fun createWithValues(values: List<T>, hasNulls: Boolean?): ValueColumn<T> {
        val nulls = hasNulls ?: values.any { it == null }
        return DataColumn.createValueColumn(name, values, type.withNullability(nulls))
    }

    override fun get(indices: Iterable<Int>): ValueColumn<T> {
        var nullable = false
        val newValues = indices.map {
            val value = values[it]
            if (value == null) nullable = true
            value
        }
        return createWithValues(newValues, nullable)
    }

    override fun get(columnName: String) =
        throw UnsupportedOperationException("Can not get nested column '$columnName' from ValueColumn '$name'")

    override operator fun get(range: IntRange): ValueColumn<T> = super<DataColumnImpl>.get(range) as ValueColumn<T>

    override fun defaultValue() = defaultValue

    override fun forceResolve() = ResolvingValueColumn(this)

    override val statistics = mutableMapOf<String, MutableMap<Map<String, ParameterValue?>, StatisticResult>>()
}

internal class ResolvingValueColumn<T>(override val source: ValueColumn<T>) :
    ValueColumn<T> by source,
    ValueColumnInternal<T>,
    ForceResolvedColumn<T> {

    override fun resolve(context: ColumnResolutionContext) = super<ValueColumn>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) =
        context.df.getColumn<T>(source.name(), context.unresolvedColumnsPolicy)?.addPath()

    override fun getValue(row: AnyRow) = super<ValueColumn>.getValue(row)

    override fun getValueOrNull(row: AnyRow) = super<ValueColumn>.getValueOrNull(row)

    override fun rename(newName: String) = ResolvingValueColumn(source.rename(newName))

    override fun toString(): String = source.toString()

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode(): Int = source.hashCode()

    override val statistics = mutableMapOf<String, MutableMap<Map<String, ParameterValue?>, StatisticResult>>()
}

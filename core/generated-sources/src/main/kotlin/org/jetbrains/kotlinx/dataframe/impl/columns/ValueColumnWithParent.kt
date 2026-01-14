package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import kotlin.reflect.KType

internal class ValueColumnWithParent<T>(override val parent: ColumnGroup<*>, override val source: ValueColumn<T>) :
    ColumnWithParent<T>,
    ValueColumn<T> by source,
    ValueColumnInternal<T>,
    DataColumnInternal<T> {

    override fun equals(other: Any?) = source.checkEquals(other)

    override fun hashCode() = source.hashCode()

    override fun path() = super<ColumnWithParent>.path()

    override fun resolve(context: ColumnResolutionContext) = super<ColumnWithParent>.resolve(context)

    override fun resolveSingle(context: ColumnResolutionContext) = super<ColumnWithParent>.resolveSingle(context)

    override fun rename(newName: String): ValueColumnWithParent<T> =
        ValueColumnWithParent(parent, source.rename(newName))

    override fun forceResolve() = ResolvingValueColumn(this)

    override fun changeType(type: KType) =
        ValueColumnWithParent(parent, source.internal().changeType(type).asValueColumn())

    override fun addParent(parent: ColumnGroup<*>) = source.addParent(parent)

    private val statisticsCache = mutableMapOf<String, MutableMap<Map<String, Any>, StatisticResult>>()

    override fun putStatisticCache(statName: String, arguments: Map<String, Any>, value: StatisticResult) {
        statisticsCache.getOrPut(statName) {
            mutableMapOf<Map<String, Any>, StatisticResult>()
        }[arguments] = value
    }

    override fun getStatisticCacheOrNull(statName: String, arguments: Map<String, Any>): StatisticResult? =
        statisticsCache[statName]?.get(arguments)
}

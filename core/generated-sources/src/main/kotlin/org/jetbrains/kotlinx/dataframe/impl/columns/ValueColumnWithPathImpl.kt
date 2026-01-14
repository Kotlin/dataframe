package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn

internal class ValueColumnWithPathImpl<T> internal constructor(
    override val data: ValueColumn<T>,
    override val path: ColumnPath,
) : ColumnWithPath<T>,
    ValueColumn<T> by data,
    ValueColumnInternal<T> {

    override fun resolveSingle(context: ColumnResolutionContext): ColumnWithPath<T> = this

    override fun rename(newName: String) =
        if (newName == name()) {
            this
        } else {
            ValueColumnWithPathImpl(data.rename(newName), path.dropLast(1) + newName)
        }

    override fun path() = path

    private val statisticsCache = mutableMapOf<String, MutableMap<Map<String, Any>, StatisticResult>>()

    override fun putStatisticCache(statName: String, arguments: Map<String, Any>, value: StatisticResult) {
        statisticsCache.getOrPut(statName) {
            mutableMapOf<Map<String, Any>, StatisticResult>()
        }[arguments] = value
    }

    override fun getStatisticCacheOrNull(statName: String, arguments: Map<String, Any>): StatisticResult? =
        statisticsCache[statName]?.get(arguments)
}

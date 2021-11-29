package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Corr
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castNotNull
import org.jetbrains.kotlinx.dataframe.api.convertToDouble
import org.jetbrains.kotlinx.dataframe.api.getColumnsWithPaths
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.api.isSuitableForCorr
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toValueColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.math.stdMean

internal fun <T, C, R> Corr<T, C>.corrImpl(otherColumns: ColumnsSelector<T, R>): DataFrame<T> {
    val len = df.nrow()

    fun <P> List<ColumnWithPath<P>>.unpackColumnGroups() = flatMap {
        // extract nested number columns from ColumnGroups
        if (it.isColumnGroup()) {
            val groupPath = it.path
            df.getColumnsWithPaths { groupPath.dfs { it.isSuitableForCorr() } }.map { it.cast() }
        } else listOf(it)
    }

    var cols1 = df.getColumnsWithPaths(columns)
        .filter { it.isColumnGroup() || it.isSuitableForCorr() }

    val cols2 = df.getColumnsWithPaths(otherColumns)
        .filter { it.isColumnGroup() || it.isSuitableForCorr() }
        .unpackColumnGroups()

    val indexColumnName = if (cols1.size == 1 && cols1[0].isColumnGroup()) cols1[0].name else "column"
    cols1 = cols1.unpackColumnGroups()

    val index = cols1.map { it.name }.toValueColumn(indexColumnName)

    val cols = cols1.associateTo(mutableMapOf()) { it.path to it.data.convertToDouble().castNotNull() }
    cols2.forEach {
        if (!cols.containsKey(it.path)) {
            cols[it.path] = it.data.convertToDouble().castNotNull()
        }
    }

    val stdMeans = cols.mapValues {
        it.value.toList().stdMean()
    }

    val cache = mutableMapOf<Pair<ColumnPath, ColumnPath>, Double>()

    val newColumns = cols2.map { c2 ->
        val values = cols1.map { c1 ->
            val cachedValue = cache[c2.path to c1.path]
            if (cachedValue != null) cachedValue
            else {
                val (d1, m1) = stdMeans[c1.path]!!
                val (d2, m2) = stdMeans[c2.path]!!
                val v1 = cols[c1.path]!!
                val v2 = cols[c2.path]!!
                val cov = (0 until len).sumOf { (v1[it] - m1) * (v2[it] - m2) }
                val res = cov / (d1 * d2)
                cache[c1.path to c2.path] = res
                res
            }
        }
        values.toValueColumn(c2.name)
    }

    return dataFrameOf(listOf(index) + newColumns).cast()
}

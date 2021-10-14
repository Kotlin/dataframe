package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.name
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.math.stdMean
import org.jetbrains.kotlinx.dataframe.toColumn

internal fun <T, C : Number> DataFrame<T>.corrImpl(cols: List<DataColumn<C>>): AnyFrame {
    val len = nrow()

    val index = cols.map { it.name }.toColumn("column")

    val values = cols.map { it.values.map { it.toDouble() } }
    val stdMeans = values.map { it.stdMean() }

    val newColumns = cols.mapIndexed { i1, c1 ->
        val values = cols.mapIndexed { i2, c2 ->
            val (d1, m1) = stdMeans[i1]
            val (d2, m2) = stdMeans[i2]
            val v1 = values[i1]
            val v2 = values[i2]
            val cov = (0 until len).map { (v1[it] - m1) * (v2[it] - m2) }.sum()
            cov / (d1 * d2)
        }
        values.toColumn(c1.name)
    }

    return dataFrameOf(listOf(index) + newColumns)
}

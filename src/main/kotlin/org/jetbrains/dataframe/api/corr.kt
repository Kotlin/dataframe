package org.jetbrains.dataframe

import org.jetbrains.dataframe.columns.isNumber

fun AnyFrame.corr(): AnyFrame {
    val cols = columns().filter { !it.type.isMarkedNullable && it.isNumber() }
    val n = cols.size
    val n1 = n + 1

    val corrDfNames = mutableListOf("feature")
    corrDfNames.addAll(cols.map { it.name() })

    val data = MutableList<Any>(n1 * n) { index ->
        val xInd = index / n1
        val yInd = index % n1
        if (yInd == 0) return@MutableList corrDfNames[xInd + 1]

        val x = get(cols[xInd]).values.toList().map { (it as Number).toDouble() }
        val y = get(cols[yInd - 1]).values.toList().map { (it as Number).toDouble() }
        val (dx, xm) = x.stdMean()
        val (dy, ym) = y.stdMean()
        val cov = x.mapIndexed { i, xi -> (xi - xm) * (y[i] - ym) }.sum()
        cov / (dx * dy)
    }

    return dataFrameOf(corrDfNames)(data)
}

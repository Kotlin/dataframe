package org.jetbrains.dataframe

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowSelector

public fun <T> DataRow<T>.movingAverage(k: Int, selector: RowSelector<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumByDouble {
        count++
        selector(it).toDouble()
    } / count
}

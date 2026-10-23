package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.isNaN
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.type

/** Computes running maxima in one pass, retaining the input's missing-value representation and type. */
internal fun <T : Comparable<T>> DataColumn<T?>.cumMaxImpl(skipNA: Boolean): DataColumn<T?> {
    var maximum: T? = null
    var missing: T? = null
    var propagateMissing = false
    val values = ArrayList<T?>(size())

    for (value in this) {
        values.add(
            when {
                propagateMissing -> missing

                value == null || value.isNaN -> {
                    if (!skipNA) {
                        missing = value
                        propagateMissing = true
                    }
                    value
                }

                else -> {
                    val previous = maximum
                    if (previous == null || value > previous) maximum = value
                    maximum
                }
            },
        )
    }

    return DataColumn.createValueColumn(name(), values, type(), defaultValue = defaultValue())
}

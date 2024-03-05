package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector

@PublishedApi
internal fun <T, R> DataColumn<T?>.mapNotNullValues(transform: (T) -> R): DataColumn<R?> {
    val collector = createDataCollector(size)
    values.forEach {
        if (it == null) collector.add(null)
        else collector.add(transform(it))
    }
    return collector.toColumn(name).cast()
}

package org.jetbrains.kotlinx.dataframe.impl.api

import com.beust.klaxon.internal.firstNotNullResult
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asDataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal fun <T> concatImpl(name: String, columns: List<DataColumn<T>>): DataColumn<T> =
    concatImpl(name, columns, columns.map { it.size() })

internal fun <T> concatImpl(name: String, columns: List<DataColumn<T>?>, columnSizes: List<Int>): DataColumn<T> {
    when (columns.size) {
        0 -> return DataColumn.empty(name).cast()
        1 -> return columns[0] ?: DataColumn.empty(name).cast()
    }

    if (columns.all { it == null || it.isColumnGroup() }) {
        val frames = columns.mapIndexed { index, col ->
            col?.asColumnGroup() ?: DataFrame.empty(columnSizes[index])
        }
        val merged = concatImpl(frames)
        return DataColumn.createColumnGroup(name, merged).asDataColumn().cast()
    } else {
        var nulls = false
        val types = mutableSetOf<KType>()
        var listOfNullable = false
        val defaultValue = columns.firstNotNullResult { it?.defaultValue() }
        var hasList = false
        val list = columns.flatMapIndexed { index, col ->
            if (col != null) {
                val type = col.type()
                if (type.classifier == List::class) {
                    val typeArgument = type.arguments[0].type
                    if (typeArgument != null) {
                        types.add(typeArgument)
                        if (!listOfNullable) listOfNullable = typeArgument.isMarkedNullable
                        hasList = true
                    }
                } else {
                    types.add(type)
                    if (!nulls) nulls = col.hasNulls
                }
                col.toList()
            } else {
                val nrow = columnSizes[index]
                if (!nulls && nrow > 0 && defaultValue == null) nulls = true
                List(nrow) { defaultValue }
            }
        }

        val guessType = types.size > 1
        val baseType = types.commonType()
        val tartypeOf = if (guessType || !hasList) baseType.withNullability(nulls)
        else getListType(baseType.withNullability(listOfNullable))
        return createColumnGuessingType(name, list, tartypeOf, guessType, defaultValue).cast()
    }
}

internal fun <T> concatImpl(dataFrames: List<DataFrame<T>>): DataFrame<T> {
    when (dataFrames.size) {
        0 -> return emptyDataFrame()
        1 -> return dataFrames[0]
    }

    // collect column names preserving original order
    val columnNames = dataFrames
        .fold(emptyList<String>()) { acc, df -> acc + (df.columnNames() - acc) }

    val sizes = dataFrames.map { it.nrow }
    val columns = columnNames.map { name ->
        concatImpl(name, dataFrames.map { it.getColumnOrNull(name) }, sizes)
    }
    return if (columns.isEmpty()) {
        // make sure the number of rows translate correctly if there are no columns
        DataFrame.empty(nrow = sizes.sum())
    } else {
        dataFrameOf(columns)
    }.cast()
}

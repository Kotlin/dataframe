package org.jetbrains.kotlinx.dataframe.impl.api

import com.beust.klaxon.internal.firstNotNullResult
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.isColumnGroup
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.baseType
import org.jetbrains.kotlinx.dataframe.impl.columns.asColumnGroup
import org.jetbrains.kotlinx.dataframe.impl.columns.guessColumnType
import org.jetbrains.kotlinx.dataframe.impl.getListType
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

internal fun concatImpl(dataFrames: List<AnyFrame>): AnyFrame {
    if (dataFrames.size == 1) return dataFrames[0]

    // collect column names preserving original order
    val columnNames = dataFrames
        .fold(emptyList<String>()) { acc, df -> acc + (df.columnNames() - acc) }

    val columns = columnNames.map { name ->

        val columns = dataFrames.map { it.getColumnOrNull(name) }

        if (columns.all { it == null || it.isColumnGroup() }) {
            val frames = columns.mapIndexed { index, col ->
                col?.asColumnGroup() ?: emptyDataFrame(dataFrames[index].nrow)
            }
            val merged = concatImpl(frames)
            DataColumn.createColumnGroup(name, merged)
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
                    val nrow = dataFrames[index].nrow
                    if (!nulls && nrow > 0 && defaultValue == null) nulls = true
                    List(nrow) { defaultValue }
                }
            }

            val guessType = types.size > 1
            val baseType = baseType(types)
            val targetType = if (guessType || !hasList) baseType.withNullability(nulls)
            else getListType(baseType.withNullability(listOfNullable))
            guessColumnType(name, list, targetType, guessType, defaultValue)
        }
    }
    return dataFrameOf(columns)
}

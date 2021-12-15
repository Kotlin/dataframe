package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.impl.columns.asFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

public enum class ExtraColumns { Remove, Keep, Fail }

@PublishedApi
internal fun <T> AnyFrame.convertToImpl(type: KType, allowConversion: Boolean, extraColumns: ExtraColumns): DataFrame<T> {
    fun AnyFrame.convertToSchema(schema: DataFrameSchema): AnyFrame {
        var visited = 0
        val newColumns = columns().mapNotNull {
            val targetColumn = schema.columns[it.name()]
            if (targetColumn == null) {
                when (extraColumns) {
                    ExtraColumns.Fail -> throw IllegalArgumentException("Column `${it.name}` is not present in target class")
                    ExtraColumns.Keep -> it
                    ExtraColumns.Remove -> null
                }
            } else {
                visited++
                val currentSchema = it.extractSchema()
                when {
                    targetColumn == currentSchema -> it
                    !allowConversion -> throw IllegalArgumentException("Column `${it.name}` has type `${it.type()}` that differs from target type `${targetColumn.type}`")
                    else -> {
                        when (targetColumn.kind) {
                            ColumnKind.Value -> {
                                val tartypeOf = targetColumn.type
                                require(!it.hasNulls() || tartypeOf.isMarkedNullable) {
                                    "Column `${it.name}` has nulls and can not be converted to non-nullable type `$tartypeOf`"
                                }
                                it.convertTo(tartypeOf)
                            }
                            ColumnKind.Group -> {
                                require(it.kind == ColumnKind.Group) {
                                    "Column `${it.name}` is ${it.kind}Column and can not be converted to `ColumnGroup`"
                                }
                                val columnGroup = it.asColumnGroup()
                                DataColumn.createColumnGroup(
                                    it.name(),
                                    columnGroup.convertToSchema((targetColumn as ColumnSchema.Group).schema)
                                )
                            }
                            ColumnKind.Frame -> {
                                require(it.kind == ColumnKind.Frame) {
                                    "Column `${it.name}` is ${it.kind}Column and can not be converted to `FrameColumn`"
                                }
                                val frameColumn = it.asFrameColumn()
                                val frameSchema = (targetColumn as ColumnSchema.Frame).schema
                                val frames = frameColumn.values().map { it.convertToSchema(frameSchema) }
                                DataColumn.createFrameColumn(it.name(), frames, schema = lazy { frameSchema })
                            }
                        }
                    }
                }
            }
        }

        if (visited != schema.columns.size) {
            val unvisited = schema.columns.keys - columnNames()
            throw IllegalArgumentException("The following columns were not found in DataFrame: $unvisited")
        }
        return newColumns.toDataFrame()
    }

    val clazz = type.jvmErasure
    val marker = MarkersExtractor[clazz]
    return convertToSchema(marker.schema).cast()
}

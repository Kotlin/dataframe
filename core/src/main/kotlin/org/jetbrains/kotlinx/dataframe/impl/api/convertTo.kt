package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.ExcessiveColumns
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.exceptions.ExcessiveColumnsException
import org.jetbrains.kotlinx.dataframe.exceptions.TypeConversionException
import org.jetbrains.kotlinx.dataframe.impl.columns.asAnyFrameColumn
import org.jetbrains.kotlinx.dataframe.impl.emptyPath
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

private class Converter(val transform: (Any?) -> Any?, val skipNulls: Boolean)

private class ConvertSchemaDslImpl<T> : ConvertSchemaDsl<T> {
    val converters = mutableMapOf<Pair<KType, KType>, Converter>()

    override fun <A, B> convert(from: KType, to: KType, converter: (A) -> B) {
        converters[from.withNullability(false) to to.withNullability(false)] = Converter(converter as (Any?) -> Any?, !from.isMarkedNullable)
    }

    fun getConverter(from: KType, to: KType): Converter? {
        return converters[from.withNullability(false) to to.withNullability(false)]
    }
}

@PublishedApi
internal fun AnyFrame.convertToImpl(
    type: KType,
    allowConversion: Boolean,
    excessiveColumns: ExcessiveColumns,
    body: ConvertSchemaDsl<Any>.() -> Unit = {}
): AnyFrame {
    val dsl = ConvertSchemaDslImpl<Any>()
    dsl.body()

    fun AnyFrame.convertToSchema(schema: DataFrameSchema, path: ColumnPath): AnyFrame {
        if (ncol == 0) return schema.createEmptyDataFrame()
        var visited = 0
        val newColumns = columns().mapNotNull {
            val targetColumn = schema.columns[it.name()]
            if (targetColumn == null) {
                when (excessiveColumns) {
                    ExcessiveColumns.Fail -> throw ExcessiveColumnsException(listOf(it.name))
                    ExcessiveColumns.Keep -> it
                    ExcessiveColumns.Remove -> null
                }
            } else {
                visited++
                val currentSchema = it.extractSchema()
                when {
                    targetColumn == currentSchema -> it
                    !allowConversion -> throw IllegalArgumentException("Column `${it.name}` has type `${it.type()}` that differs from target type `${targetColumn.type}`")
                    else -> {
                        val columnPath = path + it.name
                        when (targetColumn.kind) {
                            ColumnKind.Value -> {
                                val from = it.type()
                                val to = targetColumn.type
                                val converter = dsl.getConverter(from, to)
                                if (converter != null) {
                                    val nullsAllowed = to.isMarkedNullable
                                    it.map(to, Infer.Nulls) {
                                        val result = if (it != null || !converter.skipNulls) converter.transform(it) else it
                                        if (!nullsAllowed && result == null) throw TypeConversionException(it, from, to)
                                        result
                                    }
                                } else it.convertTo(to)
                            }
                            ColumnKind.Group -> {
                                require(it.kind == ColumnKind.Group) {
                                    "Column `${it.name}` is ${it.kind}Column and can not be converted to `ColumnGroup`"
                                }
                                val columnGroup = it.asColumnGroup()
                                DataColumn.createColumnGroup(
                                    it.name(),
                                    columnGroup.convertToSchema((targetColumn as ColumnSchema.Group).schema, columnPath)
                                )
                            }
                            ColumnKind.Frame -> {
                                require(it.kind == ColumnKind.Frame) {
                                    "Column `${it.name}` is ${it.kind}Column and can not be converted to `FrameColumn`"
                                }
                                val frameColumn = it.asAnyFrameColumn()
                                val frameSchema = (targetColumn as ColumnSchema.Frame).schema
                                val frames = frameColumn.values().map { it.convertToSchema(frameSchema, columnPath) }
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
    return convertToSchema(marker.schema, emptyPath())
}

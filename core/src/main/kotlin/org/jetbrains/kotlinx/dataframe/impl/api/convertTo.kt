package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.ExcessiveColumns
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
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
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyColumn
import org.jetbrains.kotlinx.dataframe.impl.schema.createEmptyDataFrame
import org.jetbrains.kotlinx.dataframe.impl.schema.extractSchema
import org.jetbrains.kotlinx.dataframe.impl.schema.render
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.size
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

private class Converter(val transform: (Any?) -> Any?, val skipNulls: Boolean)

private class ConvertSchemaDslImpl<T> : ConvertSchemaDsl<T> {
    private val converters: MutableMap<Pair<KType, KType>, Converter> = mutableMapOf()

    private val flexibleConverters: MutableMap<Pair<(KType) -> Boolean, (KType) -> Boolean>, Converter> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <A, B> convert(from: KType, to: KType, converter: (A) -> B) {
        converters[from.withNullability(false) to to.withNullability(false)] =
            Converter(converter as (Any?) -> Any?, !from.isMarkedNullable)
    }

    override fun convert(from: (KType) -> Boolean, to: (KType) -> Boolean, converter: (Any?) -> Any?) {
        flexibleConverters[from to to] = Converter(converter, false)
    }

    /**
     * Attempts to find a converter for the given types. First it tries to find an exact match,
     * then it tries to find a flexible match where the first one will be used.
     */
    fun getConverter(from: KType, to: KType): Converter? =
        converters[from.withNullability(false) to to.withNullability(false)]
            ?: flexibleConverters
                .entries
                .firstOrNull { (predicate, _) ->
                    predicate.first(from) && predicate.second(to)
                }?.value
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
        // if current frame is empty,
        if (ncol == 0 && !schema.columns.all { it.value.type.isMarkedNullable }) return schema.createEmptyDataFrame()
        var visited = 0
        val newColumns = columns().mapNotNull { originalColumn ->
            val targetColumn = schema.columns[originalColumn.name()]
            if (targetColumn == null) {
                when (excessiveColumns) {
                    ExcessiveColumns.Fail -> throw ExcessiveColumnsException(listOf(originalColumn.name))
                    ExcessiveColumns.Keep -> originalColumn
                    ExcessiveColumns.Remove -> null
                }
            } else {
                visited++

                val currentSchema = originalColumn.extractSchema()
                when {
                    targetColumn == currentSchema -> originalColumn

                    !allowConversion -> {
                        val originalSchema = mapOf(originalColumn.name to currentSchema)
                            .render(0, StringBuilder(), "\t")

                        val targetSchema = mapOf(originalColumn.name to targetColumn)
                            .render(0, StringBuilder(), "\t")

                        throw IllegalArgumentException("Column has schema:\n $originalSchema\n that differs from target schema:\n $targetSchema")
                    }

                    else -> {
                        val columnPath = path + originalColumn.name

                        when (targetColumn.kind) {
                            ColumnKind.Value -> {
                                val from = originalColumn.type()
                                val to = targetColumn.type
                                val converter = dsl.getConverter(from, to)

                                if (converter != null) {
                                    val nullsAllowed = to.isMarkedNullable
                                    originalColumn.map(to, Infer.Nulls) {
                                        val result =
                                            if (it != null || !converter.skipNulls) converter.transform(it)
                                            else it

                                        if (!nullsAllowed && result == null) throw TypeConversionException(it, from, to)

                                        result
                                    }
                                } else originalColumn.convertTo(to)
                            }

                            ColumnKind.Group -> {
                                require(originalColumn.kind == ColumnKind.Group) {
                                    "Column `${originalColumn.name}` is ${originalColumn.kind}Column and can not be converted to `ColumnGroup`"
                                }
                                val columnGroup = originalColumn.asColumnGroup()

                                DataColumn.createColumnGroup(
                                    name = originalColumn.name(),
                                    df = columnGroup.convertToSchema(
                                        schema = (targetColumn as ColumnSchema.Group).schema,
                                        path = columnPath,
                                    ),
                                )
                            }

                            ColumnKind.Frame -> {
                                // perform any patches if needed to be able to convert a column to a frame column
                                val patchedOriginalColumn: AnyCol = when {
                                    // a value column of AnyFrame? can be converted to a frame column by making nulls empty dataframes
                                    originalColumn.kind == ColumnKind.Value && originalColumn.all { it is AnyFrame? } -> {
                                        originalColumn
                                            .map { (it ?: emptyDataFrame<Any?>()) as AnyFrame }
                                            .convertTo<AnyFrame>()
                                    }

                                    else -> originalColumn
                                }

                                require(patchedOriginalColumn.kind == ColumnKind.Frame) {
                                    "Column `${patchedOriginalColumn.name}` is ${patchedOriginalColumn.kind}Column and can not be converted to `FrameColumn`"
                                }
                                val frameColumn = patchedOriginalColumn.asAnyFrameColumn()
                                val frameSchema = (targetColumn as ColumnSchema.Frame).schema
                                val frames = frameColumn.values().map { it.convertToSchema(frameSchema, columnPath) }

                                DataColumn.createFrameColumn(
                                    name = patchedOriginalColumn.name(),
                                    groups = frames,
                                    schema = lazy { frameSchema },
                                )
                            }
                        }
                    }
                }
            }
        }.toMutableList()

        // when the target is nullable but the source does not contain a column, fill it in with nulls / empty dataframes
        val newColumnsNames = newColumns.map { it.name() }
        val size = this.size.nrow
        schema.columns.forEach { (name, targetColumn) ->
            if (name !in newColumnsNames && (targetColumn.nullable || targetColumn.type.isMarkedNullable)) {
                visited++
                newColumns += targetColumn.createEmptyColumn(name, size)
            }
        }

        if (visited != schema.columns.size) {
            val unvisited = schema.columns.keys - columnNames().toSet()
            throw IllegalArgumentException("The following columns were not found in DataFrame: $unvisited, and the type was not nullable")
        }
        return newColumns.toDataFrame()
    }

    val clazz = type.jvmErasure
    val marker = MarkersExtractor.get(clazz)
    return convertToSchema(marker.schema, emptyPath())
}

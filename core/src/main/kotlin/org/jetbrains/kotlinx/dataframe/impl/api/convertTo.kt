package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.ConvertSchemaDsl
import org.jetbrains.kotlinx.dataframe.api.ConverterScope
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

private open class Converter(val transform: ConverterScope.(Any?) -> Any?, val skipNulls: Boolean)

private class ConvertSchemaDslImpl<T> : ConvertSchemaDsl<T> {
    private val converters: MutableMap<Pair<KType, KType>, Converter> = mutableMapOf()

    private val flexibleConverters: MutableMap<Pair<(KType) -> Boolean, (ColumnSchema) -> Boolean>, Converter> =
        mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <A, B> convert(from: KType, to: KType, converter: (A) -> B) {
        converters[from.withNullability(false) to to.withNullability(false)] =
            Converter({ converter(it as A) }, !from.isMarkedNullable)
    }

    override fun convertIf(
        from: (KType) -> Boolean,
        to: (ColumnSchema) -> Boolean,
        converter: ConverterScope.(Any?) -> Any?,
    ) {
        flexibleConverters[from to to] = Converter(converter, false)
    }

    /**
     * Attempts to find a converter for the given types. First it tries to find an exact match,
     * then it tries to find a flexible match where the first one will be used.
     */
    fun getConverter(fromType: KType, toSchema: ColumnSchema): Converter? =
        converters[fromType.withNullability(false) to toSchema.type.withNullability(false)]
            ?: flexibleConverters
                .entries
                .firstOrNull { (predicate, _) ->
                    predicate.first(fromType) && predicate.second(toSchema)
                }?.value
}

@PublishedApi
internal fun AnyFrame.convertToImpl(
    type: KType,
    allowConversion: Boolean,
    excessiveColumns: ExcessiveColumns,
    body: ConvertSchemaDsl<Any>.() -> Unit = {},
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

                        // try to perform any user-specified conversions first
                        val from = originalColumn.type()
                        val to = targetColumn.type
                        val converter = dsl.getConverter(from, targetColumn)

                        val convertedColumn = if (converter != null) {
                            val nullsAllowed = to.isMarkedNullable
                            originalColumn.map(to, Infer.Nulls) {
                                val result =
                                    if (it != null || !converter.skipNulls) {
                                        converter.transform(ConverterScope(from, targetColumn), it)
                                    } else {
                                        it
                                    }

                                if (!nullsAllowed && result == null) throw TypeConversionException(it, from, to)

                                result
                            }
                        } else null

                        when (targetColumn.kind) {
                            ColumnKind.Value ->
                                convertedColumn ?: originalColumn.convertTo(to)

                            ColumnKind.Group -> {
                                val column = convertedColumn ?: originalColumn
                                require(column.kind == ColumnKind.Group) {
                                    "Column `${column.name}` is ${column.kind}Column and can not be converted to `ColumnGroup`"
                                }
                                val columnGroup = column.asColumnGroup()

                                DataColumn.createColumnGroup(
                                    name = column.name(),
                                    df = columnGroup.convertToSchema(
                                        schema = (targetColumn as ColumnSchema.Group).schema,
                                        path = columnPath,
                                    ),
                                )
                            }

                            ColumnKind.Frame -> {
                                val column = convertedColumn ?: originalColumn

                                // perform any patches if needed to be able to convert a column to a frame column
                                val patchedOriginalColumn: AnyCol = when {
                                    // a value column of AnyFrame? can be converted to a frame column by making nulls empty dataframes
                                    column.kind == ColumnKind.Value && column.all { it is AnyFrame? } -> {
                                        column
                                            .map { (it ?: emptyDataFrame<Any?>()) as AnyFrame }
                                            .convertTo<AnyFrame>()
                                    }

                                    else -> column
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
            throw IllegalArgumentException("The following columns were not found in DataFrame: $unvisited, and their type was not nullable")
        }
        return newColumns.toDataFrame()
    }

    val clazz = type.jvmErasure
    val marker = MarkersExtractor.get(clazz)
    return convertToSchema(marker.schema, emptyPath())
}

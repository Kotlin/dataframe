package org.jetbrains.kotlinx.dataframe.impl.schema

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.baseType
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

internal fun AnyFrame.extractSchema(): DataFrameSchema =
    DataFrameSchemaImpl(columns().filter { it.name().isNotEmpty() }.map { it.name() to it.extractSchema() }.toMap())

internal fun Iterable<DataFrameSchema>.intersectSchemas(): DataFrameSchema {
    val collectedTypes = mutableMapOf<String, MutableSet<ColumnSchema>>()
    var first = true
    val toUpdate = mutableSetOf<String>()
    forEach { schema ->
        if (first) {
            schema.columns.forEach { collectedTypes.put(it.key, mutableSetOf(it.value)) }
            first = false
        } else {
            collectedTypes.forEach { entry ->
                val otherType = schema.columns[entry.key]
                if (otherType == null) {
                    toUpdate.add(entry.key)
                } else entry.value.add(otherType)
            }
            toUpdate.forEach { collectedTypes.remove(it) }
            toUpdate.clear()
        }
    }
    val result = collectedTypes.mapValues {
        val columnKinds = it.value.map { it.kind }.distinct()
        val kind = columnKinds.first()
        when {
            columnKinds.size > 1 -> ColumnSchema.Value(typeOf<Any>().withNullability(it.value.any { it.nullable }))
            kind == ColumnKind.Value -> ColumnSchema.Value(
                baseType(
                    it.value.map { (it as ColumnSchema.Value).type }
                        .toSet()
                )
            )
            kind == ColumnKind.Frame -> ColumnSchema.Frame( // intersect only not empty schemas
                it.value.mapNotNull { (it as ColumnSchema.Frame).schema.takeIf { it.columns.isNotEmpty() } }
                    .intersectSchemas(),
                it.value.any { it.nullable }
            )
            kind == ColumnKind.Group -> ColumnSchema.Group(
                it.value.map { (it as ColumnSchema.Group).schema }
                    .intersectSchemas()
            )
            else -> throw RuntimeException()
        }
    }
    return DataFrameSchemaImpl(result)
}

internal fun AnyCol.extractSchema(): ColumnSchema = when (this) {
    is ValueColumn<*> -> ColumnSchema.Value(type)
    is ColumnGroup<*> -> ColumnSchema.Group(schema())
    is FrameColumn<*> -> ColumnSchema.Frame(
        schema.value,
        hasNulls
    )
    else -> throw RuntimeException()
}

internal fun ColumnSchema.createEmptyColumn(name: String): AnyCol = when (this) {
    is ColumnSchema.Value -> DataColumn.createValueColumn<Any?>(name, emptyList(), type)
    is ColumnSchema.Group -> DataColumn.createColumnGroup(name, schema.createEmptyDataFrame()) as AnyCol
    is ColumnSchema.Frame -> DataColumn.createFrameColumn<Any?>(name, emptyList(), lazyOf(schema))
    else -> error("Unexpected ColumnSchema: $this")
}
internal fun DataFrameSchema.createEmptyDataFrame(): AnyFrame = columns.map { (name, schema) -> schema.createEmptyColumn(name) }.toDataFrame()

@PublishedApi
internal fun createEmptyDataFrameOf(clazz: KClass<*>): AnyFrame = MarkersExtractor[clazz].schema.createEmptyDataFrame()

internal fun getPropertiesOrder(clazz: KClass<*>): Map<String, Int> =
    clazz.primaryConstructor?.parameters?.mapNotNull { it.name }?.mapIndexed { i, v -> v to i }?.toMap() ?: emptyMap()

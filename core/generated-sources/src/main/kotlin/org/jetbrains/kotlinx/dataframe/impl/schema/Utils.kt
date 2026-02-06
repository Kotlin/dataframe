package org.jetbrains.kotlinx.dataframe.impl.schema

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkersExtractor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.commonType
import org.jetbrains.kotlinx.dataframe.impl.getterName
import org.jetbrains.kotlinx.dataframe.impl.isGetterLike
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

internal fun AnyFrame.extractSchema(): DataFrameSchema =
    DataFrameSchemaImpl(columns().filter { it.name().isNotEmpty() }.associate { it.name() to it.extractSchema() })

// helper overload for friend modules
@JvmName("intersectSchemasOverload")
internal fun intersectSchemas(schemas: Iterable<DataFrameSchema>): DataFrameSchema = schemas.intersectSchemas()

internal fun Iterable<DataFrameSchema>.intersectSchemas(): DataFrameSchema {
    val collectedTypes = mutableMapOf<String, MutableSet<ColumnSchema>>()
    var first = true
    val columnsToUpdate = mutableSetOf<String>()
    forEach { schema ->
        if (first) {
            schema.columns.forEach { (name, columnSchema) ->
                collectedTypes[name] = mutableSetOf(columnSchema)
            }
            first = false
        } else {
            collectedTypes.forEach { (name, columnSchemas) ->
                val otherType = schema.columns[name]
                if (otherType == null) {
                    columnsToUpdate.add(name)
                } else {
                    columnSchemas.add(otherType)
                }
            }
            columnsToUpdate.forEach { collectedTypes.remove(it) }
            columnsToUpdate.clear()
        }
    }
    val result = collectedTypes.mapValues { (name, columnSchemas) ->
        val columnKinds = columnSchemas.map { it.kind }.distinct()
        val kind = columnKinds.first()
        when {
            columnKinds.size > 1 -> ColumnSchema.Value(typeOf<Any>().withNullability(columnSchemas.any { it.nullable }))

            kind == ColumnKind.Value -> ColumnSchema.Value(
                type = columnSchemas
                    .map { (it as ColumnSchema.Value).type }
                    .toSet()
                    .commonType(),
            )

            kind == ColumnKind.Frame -> ColumnSchema.Frame(
                // intersect only not empty schemas
                schema = columnSchemas
                    .mapNotNull { (it as ColumnSchema.Frame).schema.takeIf { it.columns.isNotEmpty() } }
                    .intersectSchemas(),
                nullable = columnSchemas.any { it.nullable },
                contentType = columnSchemas
                    .mapNotNull { (it as ColumnSchema.Frame).contentType }
                    .toSet()
                    .commonType(),
            )

            kind == ColumnKind.Group -> ColumnSchema.Group(
                schema = columnSchemas.map { (it as ColumnSchema.Group).schema }
                    .intersectSchemas(),
                contentType = columnSchemas
                    .mapNotNull { (it as ColumnSchema.Group).contentType }
                    .toSet()
                    .commonType(),
            )

            else -> throw RuntimeException()
        }
    }
    return DataFrameSchemaImpl(result)
}

internal fun AnyCol.extractSchema(): ColumnSchema =
    when (this) {
        is ValueColumn<*> -> ColumnSchema.Value(type)
        is ColumnGroup<*> -> ColumnSchema.Group(schema(), typeOf<Any?>())
        is FrameColumn<*> -> ColumnSchema.Frame(schema.value, hasNulls, typeOf<Any?>())
        else -> throw RuntimeException("Unknown column type: $this")
    }

@PublishedApi
internal fun getSchema(kClass: KClass<*>): DataFrameSchema = MarkersExtractor.get(kClass).schema

/**
 * Create "empty" column based on the toplevel of [this] [ColumnSchema].
 */
internal fun ColumnSchema.createEmptyColumn(name: String): AnyCol =
    when (this) {
        is ColumnSchema.Value -> DataColumn.createValueColumn<Any?>(name, emptyList(), type)
        is ColumnSchema.Group -> DataColumn.createColumnGroup(name, schema.createEmptyDataFrame()) as AnyCol
        is ColumnSchema.Frame -> DataColumn.createFrameColumn<Any?>(name, emptyList(), lazyOf(schema))
        else -> error("Unexpected ColumnSchema: $this")
    }

/**
 * Creates a column based on [this] [ColumnSchema] filled with `null` or empty dataframes.
 * @throws IllegalStateException if the column is not nullable and [numberOfRows]` > 0`.
 */
internal fun ColumnSchema.createNullFilledColumn(name: String, numberOfRows: Int): AnyCol =
    when (this) {
        is ColumnSchema.Value -> {
            if (!type.isMarkedNullable && numberOfRows > 0) {
                error("Cannot create a null-filled value column of type $type as it's not nullable.")
            }
            DataColumn.createValueColumn(
                name = name,
                values = List(numberOfRows) { null },
                type = type,
            )
        }

        is ColumnSchema.Group -> DataColumn.createColumnGroup(
            name = name,
            df = schema.createEmptyDataFrame(numberOfRows),
        ) as AnyCol

        is ColumnSchema.Frame -> DataColumn.createFrameColumn(
            name = name,
            groups = List(numberOfRows) { emptyDataFrame<Any?>() },
            schema = lazyOf(schema),
        )

        else -> error("Cannot create null-filled column of unexpected ColumnSchema: $this")
    }

internal fun DataFrameSchema.createEmptyDataFrame(): AnyFrame =
    columns.map { (name, schema) ->
        schema.createEmptyColumn(name)
    }.toDataFrame()

internal fun DataFrameSchema.createEmptyDataFrame(numberOfRows: Int): AnyFrame =
    if (columns.isEmpty()) {
        DataFrame.empty(numberOfRows)
    } else {
        columns.map { (name, schema) ->
            schema.createNullFilledColumn(name, numberOfRows)
        }.toDataFrame()
    }

@PublishedApi
internal fun createEmptyDataFrameOf(clazz: KClass<*>): AnyFrame =
    MarkersExtractor.get(clazz).schema.createEmptyDataFrame()

/**
 * Returns a map of property names to their order in the primary/single constructor, if it exists,
 * `null` otherwise.
 */
internal fun getPropertyOrderFromPrimaryConstructor(clazz: KClass<*>): Map<String, Int>? =
    (clazz.primaryConstructor ?: clazz.constructors.singleOrNull())
        ?.parameters
        ?.mapNotNull { it.name }
        ?.mapIndexed { i, v -> v to i }
        ?.toMap()

/**
 * Sorts [this] according to the order of their [columnName] in the primary/single constructor of [klass]
 * if it exists, else, it falls back to lexicographical sorting.
 */
internal fun <T> Iterable<KCallable<T>>.sortWithConstructor(klass: KClass<*>): List<KCallable<T>> {
    require(all { it.isGetterLike() })
    val primaryConstructorOrder = getPropertyOrderFromPrimaryConstructor(klass)

    // starting off lexicographically
    val lexicographicalColumns = sortedBy { it.columnName }

    // if no primary constructor, return lexicographical order
    if (primaryConstructorOrder == null) {
        return lexicographicalColumns
    }

    // else sort the ones in the primary constructor first according to the order in there
    // leave the rest at the end in lexicographical order
    val (propsInConstructor, propsNotInConstructor) =
        lexicographicalColumns.partition { it.getterName in primaryConstructorOrder.keys }

    return propsInConstructor
        .sortedBy { primaryConstructorOrder[it.getterName] } +
        propsNotInConstructor
}

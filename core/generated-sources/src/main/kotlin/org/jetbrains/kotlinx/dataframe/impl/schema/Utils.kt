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
                } else columnSchemas.add(otherType)
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

internal fun AnyCol.extractSchema(): ColumnSchema = when (this) {
    is ValueColumn<*> -> ColumnSchema.Value(type)
    is ColumnGroup<*> -> ColumnSchema.Group(schema(), typeOf<Any?>())
    is FrameColumn<*> -> ColumnSchema.Frame(schema.value, hasNulls, typeOf<Any?>())
    else -> throw RuntimeException("Unknown column type: $this")
}

internal fun ColumnSchema.createEmptyColumn(name: String): AnyCol = when (this) {
    is ColumnSchema.Value -> DataColumn.createValueColumn<Any?>(name, emptyList(), type)
    is ColumnSchema.Group -> DataColumn.createColumnGroup(name, schema.createEmptyDataFrame()) as AnyCol
    is ColumnSchema.Frame -> DataColumn.createFrameColumn<Any?>(name, emptyList(), lazyOf(schema))
    else -> error("Unexpected ColumnSchema: $this")
}

/** Create "empty" column, filled with either null or empty dataframes. */
internal fun ColumnSchema.createEmptyColumn(name: String, numberOfRows: Int): AnyCol =
    when (this) {
        is ColumnSchema.Value -> DataColumn.createValueColumn(
            name = name,
            values = List(numberOfRows) { null },
            type = type,
        )

        is ColumnSchema.Group -> DataColumn.createColumnGroup(
            name = name,
            df = schema.createEmptyDataFrame(numberOfRows),
        ) as AnyCol

        is ColumnSchema.Frame -> DataColumn.createFrameColumn(
            name = name,
            groups = List(numberOfRows) { emptyDataFrame<Any?>() },
            schema = lazyOf(schema),
        )

        else -> error("Unexpected ColumnSchema: $this")
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
            schema.createEmptyColumn(name, numberOfRows)
        }.toDataFrame()
    }

@PublishedApi
internal fun createEmptyDataFrameOf(clazz: KClass<*>): AnyFrame =
    MarkersExtractor.get(clazz).schema.createEmptyDataFrame()

internal fun getPropertyOrderFromPrimaryConstructor(clazz: KClass<*>): Map<String, Int>? =
    clazz.primaryConstructor
        ?.parameters
        ?.mapNotNull { it.name }
        ?.mapIndexed { i, v -> v to i }
        ?.toMap()

internal fun getPropertyOrderFromAllConstructors(clazz: KClass<*>): List<Map<String, Int>> =
    clazz.constructors
        .map { constructor ->
            constructor.parameters
                .mapNotNull { it.name }
                .mapIndexed { i, v -> v to i }
                .toMap()
        }

/**
 * Sorts [this] according to the order of them in the constructors of [klass].
 * It prefers the primary constructor if it exists, else it falls back to the other constructors to do the sorting.
 * Finally, it falls back to lexicographical sorting if a property does not exist in any constructor.
 */
internal fun <T> Iterable<KCallable<T>>.sortWithConstructors(klass: KClass<*>): List<KCallable<T>> {
    require(all { it.isGetterLike() })
    val primaryConstructorOrder = getPropertyOrderFromPrimaryConstructor(klass)
    val allConstructorsOrders = getPropertyOrderFromAllConstructors(klass)

    // starting off lexicographically, sort properties according to the order of all constructors
    val allConstructorsSortedProperties = allConstructorsOrders
        .fold(this.sortedBy { it.columnName }) { props, constructorOrder ->
            props
                .withIndex()
                .sortedBy { (i, it) -> constructorOrder[it.columnName] ?: i }
                .map { it.value }
        }.toList()

    if (primaryConstructorOrder == null) {
        return allConstructorsSortedProperties
    }

    // prefer to sort properties according to the order in the primary constructor if it exists.
    // if a property does not exist in the primary constructor, fall back to the other order

    val (propsInConstructor, propsNotInConstructor) =
        this.partition { it.columnName in primaryConstructorOrder.keys }

    val allConstructorsSortedPropertyNames = allConstructorsSortedProperties.map { it.columnName }

    return propsInConstructor.sortedBy { primaryConstructorOrder[it.columnName] } +
        propsNotInConstructor.sortedBy { allConstructorsSortedPropertyNames.indexOf(it.columnName) }
}

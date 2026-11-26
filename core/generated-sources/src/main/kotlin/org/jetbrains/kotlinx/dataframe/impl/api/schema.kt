package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.impl.schema.getSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass

@PublishedApi
internal fun compileTimeSchemaImpl(runtimeSchema: DataFrameSchema?, klass: KClass<*>): DataFrameSchema {
    val compileSchema = getSchema(klass)
    if (runtimeSchema == null) return compileSchema
    val root = ColumnPath(emptyList())
    val order = buildMap {
        putColumnsOrder(runtimeSchema, path = root)
    }
    return compileSchema.sortedBy(order, path = root)
}

internal fun MutableMap<ColumnPath, Int>.putColumnsOrder(schema: DataFrameSchema, path: ColumnPath) {
    schema.columns.entries.forEachIndexed { i, (name, column) ->
        val columnPath = path + name
        this[columnPath] = i
        when (column) {
            is ColumnSchema.Frame -> putColumnsOrder(column.schema, columnPath)
            is ColumnSchema.Group -> putColumnsOrder(column.schema, columnPath)
            is ColumnSchema.Value -> Unit
        }
    }
}

internal fun DataFrameSchema.sortedBy(order: Map<ColumnPath, Int>, path: ColumnPath): DataFrameSchema {
    val sorted = columns.map { (name, column) ->
        name to when (column) {
            is ColumnSchema.Frame -> ColumnSchema.Frame(
                column.schema.sortedBy(order, path + name),
                column.nullable,
                column.contentType,
            )

            is ColumnSchema.Group -> ColumnSchema.Group(column.schema.sortedBy(order, path + name), column.contentType)

            is ColumnSchema.Value -> column
        }
    }.sortedBy { (name, _) ->
        order[path + name]
    }.toMap()
    return DataFrameSchemaImpl(sorted)
}

package org.jetbrains.kotlinx.dataframe.impl.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.impl.schema.getSchema
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import kotlin.reflect.KClass

@PublishedApi
internal fun compileTimeSchemaImpl(runtimeSchema: DataFrameSchema, klass: KClass<*>): DataFrameSchema {
    val compileSchema = getSchema(klass)
    val root = ColumnPath(emptyList())
    val order = mutableMapOf<ColumnPath, Int>()
    runtimeSchema.putColumnsOrder(order, path = root)
    return compileSchema.sorted(order, path = root)
}

internal fun DataFrameSchema.putColumnsOrder(order: MutableMap<ColumnPath, Int>, path: ColumnPath) {
    columns.entries.forEachIndexed { i, (name, column) ->
        val columnPath = path + name
        order[columnPath] = i
        when (column) {
            is ColumnSchema.Frame -> {
                column.schema.putColumnsOrder(order, columnPath)
            }

            is ColumnSchema.Group -> {
                column.schema.putColumnsOrder(order, columnPath)
            }
        }
    }
}

internal fun DataFrameSchema.sorted(order: Map<ColumnPath, Int>, path: ColumnPath): DataFrameSchema {
    val sorted = columns.map { (name, column) ->
        name to when (column) {
            is ColumnSchema.Frame -> ColumnSchema.Frame(
                column.schema.sorted(order, path + name),
                column.nullable,
                column.contentType,
            )

            is ColumnSchema.Group -> ColumnSchema.Group(column.schema.sorted(order, path + name), column.contentType)

            is ColumnSchema.Value -> column

            else -> TODO("unexpected ColumnSchema class ${column::class}")
        }
    }.sortedBy { (name, _) ->
        order[path + name]
    }.toMap()
    return DataFrameSchemaImpl(sorted)
}

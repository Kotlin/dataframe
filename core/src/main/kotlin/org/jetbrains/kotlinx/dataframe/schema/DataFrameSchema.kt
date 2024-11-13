package org.jetbrains.kotlinx.dataframe.schema

import org.jetbrains.annotations.Debug
import org.jetbrains.kotlinx.dataframe.util.DebugEntry
import org.jetbrains.kotlinx.dataframe.util.renderColumnNameAndType

@Debug.Renderer(
    text = "\"schema\"",
    childrenArray = "org.jetbrains.kotlinx.dataframe.schema.DataFrameSchemaKt.debugInfo(this)",
    hasChildren = "!this.columns.isEmpty()",
)
public interface DataFrameSchema {

    public val columns: Map<String, ColumnSchema>

    public fun compare(other: DataFrameSchema): CompareResult
}

private fun DataFrameSchema.debugInfo(): Array<Any> =
    columns.map { (name, schema) ->
        when (schema) {
            is ColumnSchema.Value -> DebugEntry(renderColumnNameAndType(name, schema))

            is ColumnSchema.Frame -> DebugEntry(
                key = renderColumnNameAndType(name, schema),
                value = schema.schema.debugInfo(),
            )

            is ColumnSchema.Group -> DebugEntry(
                key = renderColumnNameAndType(name, schema),
                value = schema.schema.debugInfo(),
            )

            else -> error("")
        }
    }.toTypedArray()

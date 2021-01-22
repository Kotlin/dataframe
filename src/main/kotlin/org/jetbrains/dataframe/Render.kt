package org.jetbrains.dataframe

internal fun String.truncate(limit: Int) = if (limit in 1 until length) {
    if (limit < 4) substring(0, limit)
    else substring(0, limit - 3) + "..."
} else {
    this
}

data class Image(val url: String)

internal fun renderSchema(df: DataFrame<*>): String =
        df.columns().map { "${it.name()}:${renderType(it)}"}.joinToString()

internal fun renderType(column: AnyCol) =
    when(column.kind()) {
        ColumnKind.Value -> {
            val type = column.type
            val result = type.toString()
            if (result.startsWith("kotlin.")) result.substring(7)
            else result
        }
        ColumnKind.Frame -> {
            val table = column.asTable()
            "[${renderSchema(table.df)}]"
        }
        ColumnKind.Map -> {
            val group = column.asGroup()
            "{${renderSchema(group.df)}}"
        }
    }



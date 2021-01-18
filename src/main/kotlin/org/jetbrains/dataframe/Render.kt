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

internal fun renderType(column: DataCol) =
    when(column.kind()) {
        ColumnKind.Data -> {
            val type = column.type
            val result = type.toString()
            if (result.startsWith("kotlin.")) result.substring(7)
            else result
        }
        ColumnKind.Table -> {
            val table = column.asTable()
            "[${renderSchema(table.df)}]"
        }
        ColumnKind.Group -> {
            val group = column.asGrouped()
            "{${renderSchema(group.df)}}"
        }
    }



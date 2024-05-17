package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.io.toJson
import java.io.File

public fun AnyFrame.printTable(name: String) {
    File("$name.json+dataframe").writeText(serialize(this))
}

internal fun serialize(df: AnyFrame): String {
    return """
    {
        "nrow": ${df.rowsCount()},
        "ncol": ${df.columnNames().size},
        "columns": ${df.columnNames().map { "\"$it\"" }},
        "kotlin_dataframe": ${df.toJson()}
    }
    """.trimIndent()
}

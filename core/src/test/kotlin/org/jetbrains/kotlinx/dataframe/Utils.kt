package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.getColumns
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.types.UtilTests
import java.net.URL

fun testResource(resourcePath: String): URL = UtilTests::class.java.classLoader.getResource(resourcePath)!!

fun testCsv(csvName: String) = testResource("$csvName.csv")

fun testJson(jsonName: String) = testResource("$jsonName.json")

fun <T : DataFrame<*>> T.toDebugString(rowsLimit: Int = 20) =
    """
    ${renderToString(borders = true, title = true, columnTypes = true, valueLimit = -1, rowsLimit = rowsLimit)}
    
    ${schema()}
    """.trimIndent()

/**
 * Prints dataframe to console with borders, title, column types and schema
 */
fun <T : DataFrame<*>> T.alsoDebug(println: String? = null, rowsLimit: Int = 20): T =
    apply {
        println?.let { println(it) }
        print(borders = true, title = true, columnTypes = true, valueLimit = -1, rowsLimit = rowsLimit)
        schema().print()
    }

fun DataFrame<*>.toCode(variableName: String = "df"): String =
    buildString {
        append("val $variableName = dataFrameOf(\n")
        appendColumns(this@toCode, indent = 1)
        append(")")
    }

private fun StringBuilder.appendColumns(df: DataFrame<*>, indent: Int) {
    val pad = "    ".repeat(indent)
    df.getColumns { colsAtAnyDepth().simplify() }.forEach { col ->
        if (col is ColumnGroup<*>) {
            append("$pad\"${col.name()}\" to columnOf(\n")
            appendColumns(col, indent + 1)
            append("$pad),\n")
        } else {
            appendColumn(col, pad)
        }
    }
}

private fun StringBuilder.appendColumn(column: DataColumn<Any?>, pad: String) {
    append("$pad\"${column.name()}\" to columnOf(")
    append(column.values().joinToString(", ") { it.toLiteral() })
    append("),\n")
}

private fun Any?.toLiteral(): String =
    when (this) {
        null -> "null"
        is String -> "\"${escape()}\""
        is Char -> "'$this'"
        is Long -> "${this}L"
        is Float -> "${this}f"
        else -> toString()
    }

private fun String.escape() =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\t", "\\t")

package org.jetbrains.kotlinx.dataframe

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.types.UtilTests
import java.net.URL

fun testResource(resourcePath: String): URL = UtilTests::class.java.classLoader.getResource(resourcePath)!!
fun testCsv(csvName: String) = testResource("$csvName.csv")
fun testJson(jsonName: String) = testResource("$jsonName.json")

fun <T : DataFrame<*>> T.toDebugString(rowsLimit: Int = 20) = """
    ${renderToString(borders = true, title = true, columnTypes = true, valueLimit = -1, rowsLimit = rowsLimit)}
    
    ${schema()}
""".trimIndent()

/**
 * Prints dataframe to console with borders, title, column types and schema
 */
fun <T : DataFrame<*>> T.alsoDebug(println: String? = null, rowsLimit: Int = 20): T = apply {
    println?.let { println(it) }
    print(borders = true, title = true, columnTypes = true, valueLimit = -1, rowsLimit = rowsLimit)
    schema().print()
}

fun parseJsonStr(jsonStr: String): JsonObject {
    val parser = Parser.default()
    return parser.parse(StringBuilder(jsonStr)) as JsonObject
}

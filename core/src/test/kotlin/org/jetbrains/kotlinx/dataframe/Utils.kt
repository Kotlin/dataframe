package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.types.UtilTests
import java.net.URL

fun testResource(resourcePath: String): URL = UtilTests::class.java.classLoader.getResource(resourcePath)!!
fun testCsv(csvName: String) = testResource("$csvName.csv")
fun testJson(jsonName: String) = testResource("$jsonName.json")

fun <T : DataFrame<*>> T.alsoDebug(println: String? = null): T = apply {
    println?.let { println(it) }
    print(borders = true, title = true, columnTypes = true, valueLimit = -1)
    schema().print()
}

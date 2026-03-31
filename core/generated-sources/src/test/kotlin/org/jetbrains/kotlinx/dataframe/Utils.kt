package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.jetbrains.kotlinx.dataframe.types.UtilTests
import java.net.URL
import kotlin.reflect.typeOf

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

fun DataFrame<*>.shouldHaveColumnGroup(name: String, block: (ColumnGroup<*>) -> Unit = { }): ColumnGroup<*> =
    getColumnOrNull(name).shouldBeInstanceOf<ColumnGroup<*>>(block)

fun DataFrame<*>.shouldHaveFrameColumn(name: String, block: (FrameColumn<*>) -> Unit = { }): FrameColumn<*> =
    getColumnOrNull(name).shouldBeInstanceOf<FrameColumn<*>>(block)

inline fun <reified T> DataFrame<*>.shouldHaveColumn(
    name: String,
    block: (DataColumn<T>) -> Unit = { },
): DataColumn<T> {
    val shouldBeInstanceOf = getColumnOrNull(name).shouldBeInstanceOf<DataColumn<*>>()
    shouldBeInstanceOf.type() shouldBe typeOf<T>()
    val cast = shouldBeInstanceOf.cast<T>()
    block(cast)
    return cast
}

package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.StringBorderStyle
import org.jetbrains.kotlinx.dataframe.io.renderToString
import org.junit.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("ktlint:standard:argument-list-wrapping")
class RenderToStringTests {
    private val df = dataFrameOf("name", "age", "city")(
        "Alice", 30, "Berlin",
        "Bob", 25, "Paris",
        "Charlie", 35, "London",
    )

    @Test
    fun `default rendering`() {
        val result = df.renderToString(columnTypes = false)
        val expected = """
            |        name  age    city
            |        ---- ----    ----
            |  0    Alice   30  Berlin
            |  1      Bob   25   Paris
            |  2  Charlie   35  London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `no index rendering`() {
        val result = df.renderToString(rowIndex = false, columnTypes = false)
        val expected = """
            |     name  age    city
            |     ---- ----    ----
            |    Alice   30  Berlin
            |      Bob   25   Paris
            |  Charlie   35  London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with borders`() {
        val result = df.renderToString(borders = true, rowIndex = false, columnTypes = false)
        val expected = """
            |⌌------------------------⌍
            ||     name|  age|    city|
            ||---------|-----|--------|
            ||    Alice|   30|  Berlin|
            ||      Bob|   25|   Paris|
            ||  Charlie|   35|  London|
            |⌎------------------------⌏
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `align left`() {
        val result = df.renderToString(alignLeft = true, rowIndex = false, columnTypes = false)
        val expected = """
            |name     age  city    
            |----     ---- ----    
            |Alice    30   Berlin  
            |Bob      25   Paris   
            |Charlie  35   London  
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with column types`() {
        val result = df.take(1).renderToString(columnTypes = true)
        val expected =
            """
            |       name  age    city
            |     String  Int  String
            |       ---- ----    ----
            |  0   Alice   30  Berlin
            |
            """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `columngroup with column types`() {
        val result = dataFrameOf("a" to columnOf(0.0)).add {
            "group" {
                "l" from { "a"<Double>() }
                "r" from { "a"<Double>() }
            }
        }.renderToString(columnTypes = true)
        val expected =
            """
            |          a                 group
            |     Double  {l:Double, r:Double}
            |       ----                  ----
            |  0     0.0      { l:0.0, r:0.0 }
            |
            """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `framecol with column types`() {
        val result = dataFrameOf("a" to columnOf(0.0)).add {
            "group" {
                "l" from { "a"<Double>() }
                "r" from { "a"<Double>() }
            }
        }.groupBy("a").toDataFrame().renderToString(columnTypes = true)
        val expected =
            """
            |          a                                     group
            |     Double    [a:Double, group:{l:Double, r:Double}]
            |       ----                                      ----
            |  0     0.0  [1 x 2] { a:0.000000, group:{ l:0.000...
            |
            """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with types and borders`() {
        val result = df.renderToString(borders = true, rowIndex = false, columnTypes = true)
        val expected = """
            |⌌------------------------⌍
            ||     name|  age|    city|
            ||   String|  Int|  String|
            ||---------|-----|--------|
            ||    Alice|   30|  Berlin|
            ||      Bob|   25|   Paris|
            ||  Charlie|   35|  London|
            |⌎------------------------⌏
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with title`() {
        val result = df.renderToString(title = true, rowIndex = false, columnTypes = false)
        val expected = """
            |DataFrame [3 x 3]
            |
            |     name  age    city
            |     ---- ----    ----
            |    Alice   30  Berlin
            |      Bob   25   Paris
            |  Charlie   35  London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun renderDoubles() {
        val result = dataFrameOf("a" to columnOf(0.0)).add {
            "group" {
                "l" from { "a"<Double>() }
                "r" from { "a"<Double>() }
            }
        }.renderToString(columnTypes = false)
        val expected = """
            |       a             group
            |    ----              ----
            |  0  0.0  { l:0.0, r:0.0 }
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `rows limit truncates`() {
        val result = df.renderToString(rowsLimit = 2, rowIndex = false, columnTypes = false)
        val expected = """
            |   name  age    city
            |   ---- ----    ----
            |  Alice   30  Berlin
            |    Bob   25   Paris
            |...
            |3 columns x 3 rows
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `nullable values`() {
        val nullDf = dataFrameOf("a", "b")(
            1, "hello",
            2, null,
        )
        val result = nullDf.renderToString(rowIndex = false, columnTypes = false)
        val expected = """
         |  a      b
         |---   ----
         |  1  hello
         |  2   null
         |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `render rows unlimited`() {
        val result = List(500) { it }.toDataFrame {
            "i" from { it }
            "hash" from { it.hashCode().toString() }
        }.renderToString(rowsLimit = null)

        // header + separator + data + empty newline
        result.lines().size shouldBe 504
    }

    @Test
    fun `render values unlimited`() {
        val result = dataFrameOf("a")("abc".repeat(100)).renderToString(valueLimit = null)
        result.lines().maxOf { it.length } shouldBeGreaterThan 300
    }

    @Test
    fun `cell values replaced with just ellipsis when valueLimit is from 1 to 3`() {
        val result = dataFrameOf("a")("abcdefgh").renderToString(valueLimit = 3)
        val expected = """
            |          a
            |     String
            |       ----
            |  0     ...
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `cell values are not limited when valueLimit is zero or negative`() {
        val df = dataFrameOf("a")("abcdefgh")
        val expected = """
            |            a
            |       String
            |         ----
            |  0  abcdefgh
            |
        """.trimMargin()
        assertEquals(expected, df.renderToString(valueLimit = 0))
        assertEquals(expected, df.renderToString(valueLimit = -5))
    }

    @Test
    fun `long cell values cut to valueLimit characters ending in ellipsis`() {
        val result = dataFrameOf("a")("abcdefgh").renderToString(valueLimit = 5)
        val expected = """
            |          a
            |     String
            |       ----
            |  0   ab...
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `borders drawn in the given border style`() {
        val result = dataFrameOf("a")(1, 2).renderToString(
            borders = true,
            borderStyle = StringBorderStyle.ContinuousWithRoundedCorners,
        )
        val expected = """
            |╭─────────╮
            |│   │    a│
            |│   │  Int│
            |│───┼─────┼
            |│  0│    1│
            |│  1│    2│
            |╰─────────╯
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    // TODO(<issue link>): remove @Ignore when renderToString rejects a negative rowsLimit
    @Ignore
    @Test
    fun `renderToString rejects a negative rowsLimit with IllegalArgumentException`() {
        // Expected behaviour once the bug is fixed. Today the call throws IndexOutOfBoundsException
        // (with rowIndex = true) or IllegalArgumentException (with rowIndex = false) from subList bounds.
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
        )
        assertFailsWith<IllegalArgumentException> { df.renderToString(rowsLimit = -1) }
    }
}

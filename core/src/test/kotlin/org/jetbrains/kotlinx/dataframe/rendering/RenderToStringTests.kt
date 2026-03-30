package org.jetbrains.kotlinx.dataframe.rendering

import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.io.renderToString
import kotlin.test.Test
import kotlin.test.assertEquals

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
            |  Alice   30  Berlin
            |    Bob   25   Paris
            |...
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
         |  1  hello
         |  2   null
         |
        """.trimMargin()
        assertEquals(expected, result)
    }
}

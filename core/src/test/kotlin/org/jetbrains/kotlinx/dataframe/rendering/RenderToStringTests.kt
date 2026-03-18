package org.jetbrains.kotlinx.dataframe.rendering

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
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
        val result = df.renderToString()
        val expected = """
            |      name age   city
            | 0   Alice  30 Berlin
            | 1     Bob  25  Paris
            | 2 Charlie  35 London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `no index rendering`() {
        val result = df.renderToString(rowIndex = false)
        val expected = """
            |    name age   city
            |   Alice  30 Berlin
            |     Bob  25  Paris
            | Charlie  35 London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with borders`() {
        val result = df.renderToString(borders = true, rowIndex = false)
        val expected = """
            |⌌---------------------⌍
            ||    name| age|   city|
            ||--------|----|-------|
            ||   Alice|  30| Berlin|
            ||     Bob|  25|  Paris|
            || Charlie|  35| London|
            |⌎---------------------⌏
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `align left`() {
        val result = df.renderToString(alignLeft = true, rowIndex = false)
        val expected = """
            |name    age city   
            |Alice   30  Berlin 
            |Bob     25  Paris  
            |Charlie 35  London 
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `with column types`() {
        val result = df.renderToString(columnTypes = true, rowIndex = false)
        val header = result.lines().first()
        assertEquals(""" name:String age:Int city:String""", header)
    }

    @Test
    fun `with title`() {
        val result = df.renderToString(title = true, rowIndex = false)
        val expected = """
            |DataFrame [3 x 3]
            |
            |    name age   city
            |   Alice  30 Berlin
            |     Bob  25  Paris
            | Charlie  35 London
            |
        """.trimMargin()
        assertEquals(expected, result)
    }

    @Test
    fun `rows limit truncates`() {
        val result = df.renderToString(rowsLimit = 2, rowIndex = false)
        val expected = """
            |  name age   city
            | Alice  30 Berlin
            |   Bob  25  Paris
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
        val result = nullDf.renderToString(rowIndex = false)
        val expected = """
         | a     b
         | 1 hello
         | 2  null
         |
        """.trimMargin()
        assertEquals(expected, result)
    }
}

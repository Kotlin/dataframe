package org.jetbrains.kotlinx.dataframe.rendering

import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.renderToMarkdown
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("ktlint:standard:argument-list-wrapping")
class RenderToMarkdownTests {
    private val df = dataFrameOf("name", "age", "city")(
        "Alice", 30, "Berlin",
        "Bob", 25, "Paris",
        "Charlie", 35, "London",
    )

    @Test
    fun `markdown basic structure`() {
        val result = df.renderToMarkdown()
        val expected =
            """
            |  | name | age | city |
            |---:|---:|---:|---:|
            | 0 | Alice | 30 | Berlin |
            | 1 | Bob | 25 | Paris |
            | 2 | Charlie | 35 | London |

            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown basic structure without index`() {
        val result = df.renderToMarkdown(rowIndex = false)
        val expected =
            """
            | name | age | city |
            |---:|---:|---:|
            | Alice | 30 | Berlin |
            | Bob | 25 | Paris |
            | Charlie | 35 | London |
            
            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown with types structure`() {
        val result = df.renderToMarkdown(columnTypes = true)
        val expected =
            """
            |  | name:String | age:Int | city:String |
            |---:|---:|---:|---:|
            | 0 | Alice | 30 | Berlin |
            | 1 | Bob | 25 | Paris |
            | 2 | Charlie | 35 | London |

            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown align left`() {
        val result = df.renderToMarkdown(alignLeft = true, rowIndex = false)
        val expected =
            """
            | name | age | city |
            |:---|:---|:---|
            | Alice | 30 | Berlin |
            | Bob | 25 | Paris |
            | Charlie | 35 | London |
            
            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown align right`() {
        val result = df.renderToMarkdown(alignLeft = false, rowIndex = false)
        val expected =
            """
            | name | age | city |
            |---:|---:|---:|
            | Alice | 30 | Berlin |
            | Bob | 25 | Paris |
            | Charlie | 35 | London |
            
            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown with title`() {
        val result = df.renderToMarkdown(title = true, rowIndex = false)
        val expected =
            """
            **DataFrame [3 x 3]**

            | name | age | city |
            |---:|---:|---:|
            | Alice | 30 | Berlin |
            | Bob | 25 | Paris |
            | Charlie | 35 | London |
            
            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown truncation footer`() {
        val result = df.renderToMarkdown(rowsLimit = 1, rowIndex = false)
        val expected =
            """
            | name | age | city |
            |---:|---:|---:|
            | Alice | 30 | Berlin |

            *... 2 more rows*
            
            """.trimIndent()
        assertEquals(expected, result)
    }

    @Test
    fun `markdown escapes pipes in values`() {
        val pipeDf = dataFrameOf("cmd")("a|b")
        val result = pipeDf.renderToMarkdown(rowIndex = false)
        val expected =
            """
            | cmd |
            |---:|
            | a\|b |

            """.trimIndent()
        assertEquals(expected, result)
    }
}

package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Tests the behavior of the [print] functions:
 * each of them prints the rendered receiver to standard output.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class PrintTests {

    // `println` ends with the platform line separator; normalize it to make the expectations platform-independent
    private fun captureStdout(block: () -> Unit): String {
        val originalOut = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer, true))
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return buffer.toString().replace(System.lineSeparator(), "\n")
    }

    @Test
    fun `print prints the dataframe rendered as a text table to standard output`() {
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
        )
        val printed = captureStdout { df.print() }
        printed shouldBe
            "       a       b\n" +
            "     Int  String\n" +
            "    ----    ----\n" +
            "  0    1       x\n" +
            "  1    2       y\n" +
            "\n"
    }

    @Test
    fun `print applies the given rendering options`() {
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
            3, "z",
        )
        val printed = captureStdout { df.print(rowsLimit = 1, title = true) }
        printed shouldBe
            "DataFrame [3 x 2]\n" +
            "\n" +
            "       a       b\n" +
            "     Int  String\n" +
            "    ----    ----\n" +
            "  0    1       x\n" +
            "...\n" +
            "2 columns x 3 rows\n" +
            "\n"
    }

    @Test
    fun `print prints the data column as a one-column table with its type`() {
        val column = columnOf(1, 2).named("a")
        val printed = captureStdout { column.print() }
        printed shouldBe
            "       a\n" +
            "     Int\n" +
            "    ----\n" +
            "  0    1\n" +
            "  1    2\n" +
            "\n"
    }

    @Test
    fun `print prints the data row as a curly-braced list of values`() {
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
        )
        val printed = captureStdout { df[0].print() }
        printed shouldBe "{ a:1, b:x }\n"
    }

    @Test
    fun `print prints the data row without null and empty values`() {
        val nullDf = dataFrameOf("a", "b")(
            1, "x",
            2, null,
        )
        val listDf = dataFrameOf("a", "b")(
            1, listOf("x"),
            2, emptyList<String>(),
        )
        captureStdout { nullDf[1].print() } shouldBe "{ a:2 }\n"
        captureStdout { listDf[1].print() } shouldBe "{ a:2 }\n"
    }

    @Test
    fun `print prints the key columns and the groups column of the group by`() {
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
            1, "z",
        )
        val printed = captureStdout { df.groupBy("a").print() }
        printed shouldBe
            "       a                 group\n" +
            "     Int     [a:Int, b:String]\n" +
            "    ----                  ----\n" +
            "  0    1               [2 x 2]\n" +
            "  1    2  [1 x 2] { a:2, b:y }\n" +
            "\n"
    }

    @Test
    fun `print prints the schema as a list of columns with types`() {
        val df = dataFrameOf("a", "b")(
            1, "x",
            2, "y",
        )
        val printed = captureStdout { df.schema().print() }
        printed shouldBe
            "a: Int\n" +
            "b: String\n"
    }

    @Test
    fun `print prints the wrapped kotlin code`() {
        val code = CodeString("val a = 1")
        val printed = captureStdout { code.print() }
        printed shouldBe "val a = 1\n"
    }
}

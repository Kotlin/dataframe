package org.jetbrains.dataframe.jupyter

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.intellij.lang.annotations.Language
import org.jetbrains.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.jupyter.api.MimeTypedResult
import org.jetbrains.kotlinx.jupyter.testkit.JupyterReplTestCase
import org.junit.Test

class JupyterCodegenTests : JupyterReplTestCase() {
    @Test
    fun `codegen for enumerated frames`() {
        @Language("kts")
        val res1 = exec(
            """
            val names = (0..2).map { it.toString() }
            val df = dataFrameOf(names)(1, 2, 3)
            """.trimIndent()
        )
        res1 shouldBe Unit

        val res2 = execRaw("df.`1`")
        res2.shouldBeInstanceOf<ValueColumn<*>>()
    }

    @Test
    fun `codegen for complex column names`() {
        @Language("kts")
        val res1 = exec(
            """
            val df = DataFrame.readDelimStr("[a], (b), {c}\n1, 2, 3")
            df
            """.trimIndent()
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()

        val res2 = exec(
            """listOf(df.`{a}`[0], df.`(b)`[0], df.`{c}`[0])"""
        )
        res2 shouldBe listOf(1, 2, 3)
    }

    @Test
    fun `codegen for '$' that is interpolator in kotlin string literals`() {
        @Language("kts")
        val res1 = exec(
            """
            val df = DataFrame.readDelimStr("\${'$'}id\n1")
            df
            """.trimIndent()
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        val res2 = exec(
            "listOf(df.`\$id`[0])"
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for backtick that is forbidden in kotlin identifiers`() {
        @Language("kts")
        val res1 = exec(
            """
            val df = DataFrame.readDelimStr("Day`s\n1")
            df
            """.trimIndent()
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())
        val res2 = exec(
            "listOf(df.`Day's`[0])"
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for chars that is forbidden in JVM identifiers`() {
        val forbiddenChar = ";"
        @Language("kts")
        val res1 = exec(
            """
            val df = DataFrame.readDelimStr("Test$forbiddenChar\n1")
            df
            """.trimIndent()
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())
        val res2 = exec(
            "listOf(df.`Test `[0])"
        )
        res2 shouldBe listOf(1)
    }

    @Test
    fun `codegen for chars that is forbidden in JVM identifiers 1`() {
        val forbiddenChar = "\\\\"
        @Language("kts")
        val res1 = exec(
            """
            val df = DataFrame.readDelimStr("Test$forbiddenChar\n1")
            df
            """.trimIndent()
        )
        res1.shouldBeInstanceOf<MimeTypedResult>()
        println(res1.entries.joinToString())
        val res2 = exec(
            "listOf(df.`Test `[0])"
        )
        res2 shouldBe listOf(1)
    }
}

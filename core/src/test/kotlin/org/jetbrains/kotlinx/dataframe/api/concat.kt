package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

class ConcatTests {

    @Test
    fun `different types`() {
        val a by columnOf(1, 2)
        val b by columnOf(3.0, null)
        a.concat(b) shouldBe columnOf<Number?>(1, 2, 3.0, null).named("a")
    }

    @Test
    fun `concat with keys`() {
        val df = dataFrameOf(
            "value" to listOf(1, 2, 3, 3),
            "type" to listOf("a", "b", "a", "b"),
        )
        val gb = df.groupBy { expr { "Category: ${(this["type"] as String).uppercase()}" } named "category" }
        val dfWithCategory = gb.concatWithKeys()

        dfWithCategory.columnNames() shouldBe listOf("value", "type", "category")
    }

    @Test
    fun `concat empty DataFrames no rows`() {
        val dfWithSchema = DataFrame.emptyOf<Pair<Int, String>>()
        (dfWithSchema concat dfWithSchema).let { concatenated ->
            concatenated shouldBe dfWithSchema
            concatenated.schema() shouldBe dfWithSchema.schema()
        }

        val dfNothingCols = dataFrameOf(
            "a" to DataColumn.empty(),
            "b" to DataColumn.empty(),
        )
        (dfNothingCols concat dfNothingCols).let { concatenated ->
            concatenated shouldBe dfNothingCols
            concatenated.schema() shouldBe dfNothingCols.schema()
        }
    }

    @Test
    fun `concat empty DataFrames no cols`() {
        val dfNoCols = DataFrame.empty(5)
        (dfNoCols concat dfNoCols) shouldBe DataFrame.empty(10)
    }
}

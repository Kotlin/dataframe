package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ConcatTests {

    @Test
    fun `different types`() {
        val a by columnOf(1, 2)
        val b by columnOf(3.0, null)
        a.concat(b) shouldBe columnOf(1, 2, 3.0, null).named("a")
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
}

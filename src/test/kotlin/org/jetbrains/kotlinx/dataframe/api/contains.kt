package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ContainsTests {

    @Test
    fun `column contains`() {
        val col by columnOf(1, 3, 5)
        col.contains(3) shouldBe true
        col.contains(2) shouldBe false
    }

    @Test
    fun `column group contains`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        val col = df.asColumnGroup("col")
        col.contains(df[0]) shouldBe true
        col.contains(df.update("b").withValue(0)[0]) shouldBe false
    }
}

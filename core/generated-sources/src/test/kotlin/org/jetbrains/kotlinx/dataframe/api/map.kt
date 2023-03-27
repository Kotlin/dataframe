package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class MapTests {

    @Test
    fun `map frame column with empty frames`() {
        val frames by columnOf(dataFrameOf("a")(1), emptyDataFrame())
        frames.map { it.firstOrNull() }.size() shouldBe frames.size()
    }

    @Test
    fun `map ColumnsContainer`() {
        val df = dataFrameOf("a")(1, 2).add {
            expr { "a"<Int>() + 1 }.cumSum() into "b"
        }
        df["b"][1] shouldBe 5
    }
}

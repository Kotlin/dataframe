package org.jetbrains.kotlinx.dataframe

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.add
import org.junit.Test

class TipsAndTricksTests {

    @Test
    fun `distance from last zero`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        val added = df.add("Y") { if (x() == 0) 0 else (prev?.new<Int>() ?: 0) + 1 }
        val expected = listOf(1, 2, 0, 1, 2, 3, 4, 0, 1, 2)
        added["Y"].values() shouldBe expected
    }
}

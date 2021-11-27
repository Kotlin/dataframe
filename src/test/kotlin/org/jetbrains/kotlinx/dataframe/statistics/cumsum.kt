package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class CumsumTests {

    val col by columnOf(1, 2, null, 3, 4)
    val expected = listOf(1, 3, null, 6, 10)
    val expectedNoSkip = listOf(1, 3, null, null, null)

    @Test
    fun `int column`() {
        col.cumSum().toList() shouldBe expected
        col.cumSum(skipNA = false).toList() shouldBe expectedNoSkip
    }

    @Test
    fun frame() {
        val str by columnOf("a", "b", "c", "d", "e")
        val df = dataFrameOf(col, str)

        df.cumSum()[col].toList() shouldBe expected
        df.cumSum(skipNA = false)[col].toList() shouldBe expectedNoSkip

        df.cumSum { col }[col].toList() shouldBe expected
        df.cumSum(skipNA = false) { col }[col].toList() shouldBe expectedNoSkip

        df.cumSum(col)[col].toList() shouldBe expected
        df.cumSum(col, skipNA = false)[col].toList() shouldBe expectedNoSkip
    }

    @Test
    fun `double column`() {
        val doubles by columnOf(1.0, 2.0, null, Double.NaN, 4.0)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }

    @Test
    fun `number column`() {
        val doubles by columnOf(1, 2, null, Double.NaN, 4)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }
}

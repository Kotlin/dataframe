package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ExplodeTests {

    @Test
    fun `explode into`() {
        val df = dataFrameOf("a" to listOf(1), "b" to listOf(listOf(2, 3)))
        val exploded = df.explode { "b" into "c" }
        val expected = dataFrameOf("a" to listOf(1, 1), "c" to listOf(2, 3))
        exploded shouldBe expected
    }

    @Test
    fun `explode list and duplicate value`() {
        val exploded = dataFrameOf("a", "b")(1, listOf(2, 3)).explode()
        exploded shouldBe dataFrameOf("a", "b")(1, 2, 1, 3)
    }

    @Test
    fun `explode list and frame column`() {
        val exploded = dataFrameOf("a", "b", "c", "d")(1, listOf(2, 3), dataFrameOf("x", "y")(4, 5, 6, 7), listOf(8))
            .explode().ungroup("c")
        exploded shouldBe dataFrameOf("a", "b", "x", "y", "d")(
            1, 2, 4, 5, 8,
            1, 3, 6, 7, null
        )
    }

    @Test
    fun `explode nothing`() {
        val df = dataFrameOf("a", "b")(1, 2)
        df.explode() shouldBe df
    }
}

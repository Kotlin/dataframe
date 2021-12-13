package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ExplodeTests {

    @Test
    fun `explode into`() {
        val df = dataFrameOf("a" to listOf(1), "b" to listOf(listOf(2, 3)))
        val imploded = df.explode { "b" into "c" }
        val expected = dataFrameOf("a" to listOf(1, 1), "c" to listOf(2, 3))
        imploded shouldBe expected
    }
}

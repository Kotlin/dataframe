package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class ImplodeTests {

    @Test
    fun `implode into`() {
        val df = dataFrameOf("a" to listOf(1, 1), "b" to listOf(2, 3))
        val imploded = df.implode { "b" into "c" }
        val expected = dataFrameOf("a" to listOf(1), "c" to listOf(listOf(2, 3)))
        imploded shouldBe expected
    }

    @Test
    fun `implode all`() {
        val df = dataFrameOf("a" to listOf(1, 1), "b" to listOf(2, 3))
        df.implode() shouldBe df.implode { all() }[0]
    }
}

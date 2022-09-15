package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class ReverseTests {

    @Test
    fun dataframe() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)
        df.reverse() shouldBe dataFrameOf("a", "b")(3, 4, 1, 2)
    }

    @Test
    fun column() {
        val col by columnOf(1, 2, 3)
        col.reverse() shouldBe col.withValues(listOf(3, 2, 1))
    }

    @Test
    fun columnGroup() {
        val a by columnOf(1, 2)
        val b by columnOf(3, 4)
        val col by columnOf(a, b)
        col.reverse() shouldBe columnOf(a.reverse(), b.reverse()).named("col")
    }
}

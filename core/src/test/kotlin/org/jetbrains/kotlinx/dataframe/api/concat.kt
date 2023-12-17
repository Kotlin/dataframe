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
}

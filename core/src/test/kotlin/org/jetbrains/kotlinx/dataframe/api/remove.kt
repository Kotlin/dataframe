package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.junit.Test

class RemoveTests {

    val df = dataFrameOf("a", "b")(1, 2)
    val b by column<Int>()
    data class C(val b: Int)

    @Test
    fun `simple remove`() {
        val e = df.select("a")
        df.remove("b") shouldBe e
        df.remove { b } shouldBe e
        df.remove(C::b) shouldBe e
    }

    @Test
    fun `remove renamed`() {
        val (_, removed) = df.removeImpl { "a" named "c" }
        removed[0].data.column!!.name shouldBe "c"
    }

    @Test
    fun `remove missing column`() {
        val d = df.remove { b }

        d.remove("b") shouldBe d
        d.remove { b } shouldBe d
        d.remove(C::b) shouldBe d
    }
}

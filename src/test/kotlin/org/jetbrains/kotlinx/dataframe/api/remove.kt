package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.junit.Test

class RemoveTests {

    @Test
    fun `remove renamed`() {
        val df = dataFrameOf("a", "b")(1, 2)
        val (_, removed) = df.removeImpl { "a" named "c" }
        removed[0].data.column!!.name shouldBe "c"
    }
}

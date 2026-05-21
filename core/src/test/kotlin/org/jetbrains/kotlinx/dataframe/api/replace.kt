package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import kotlin.reflect.typeOf
import org.junit.Test

class ReplaceTests {

    @Test
    fun `replace named`() {
        val df = dataFrameOf("a")(1)
        val conv = df.replace { "a"<Int>() named "b" }.with { it.convertToDouble() }
        conv.columnNames() shouldBe listOf("b")
        conv.columnTypes() shouldBe listOf(typeOf<Double>())
    }
}

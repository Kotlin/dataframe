package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.junit.Test
import java.lang.IllegalArgumentException

class CastTests {

    @Test
    fun safeUnsafeCast() {
        @DataSchema
        data class Data(val a: Int, val b: String)

        val df = dataFrameOf("a", "b", "c")(1, "s", 2)
        df.cast<Data>(verify = true) shouldBe df

        shouldThrow<IllegalArgumentException> {
            df.convert("a").toDouble().cast<Data>(verify = true)
        }
        val converted = df.convert("a").toDouble()
        converted.cast<Data>(verify = false) shouldBe converted
        converted.cast<Data>() shouldBe converted
    }
}

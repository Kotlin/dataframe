package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toDouble
import org.junit.Test
import kotlin.reflect.*

class JsonTests {

    @Test
    fun `NaN double serialization`() {
        val df = dataFrameOf("v")(1.1, Double.NaN)
        df["v"].type() shouldBe typeOf<Double>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }

    @Test
    fun `NaN float serialization`() {
        val df = dataFrameOf("v")(1.1f, Float.NaN)
        df["v"].type() shouldBe typeOf<Float>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df.convert("v").toDouble()
    }

    @Test
    fun `NaN string serialization`() {
        val df = dataFrameOf("v")("NaM", "NaN")
        df["v"].type() shouldBe typeOf<String>()
        DataFrame.readJsonStr(df.toJson()) shouldBe df
    }
}

package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.math.std
import org.junit.Test
import kotlin.reflect.typeOf

class StdTests {

    @Test
    fun `std one column`() {
        val value by columnOf(1, 2, 3)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.values().std() shouldBe expected
        value.values().std(typeOf<Int>()) shouldBe expected
        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
    }

    @Test
    fun `std one double column`() {
        val value by columnOf(1.0, 2.0, 3.0)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.values().std() shouldBe expected
        value.values().std(typeOf<Double>()) shouldBe expected
        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
    }
}

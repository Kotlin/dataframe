package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.impl.precision
import org.jetbrains.kotlinx.dataframe.io.format
import org.junit.Test

class PrecisionTests {

    @Test
    fun precision() {
        columnOf(1.2, 3.2).precision() shouldBe 1
        columnOf(1.1232, 3.2).precision() shouldBe 4
        columnOf(1.1220001, 12313).precision() shouldBe 7
        columnOf(1, 2).precision() shouldBe 0
        columnOf(1.0, 2).precision() shouldBe 1
        columnOf(123121.0, -1231.0).precision() shouldBe 1
        columnOf(123121.00001, -1231.120).precision() shouldBe 5
    }

    @Test
    fun format() {
        val value = 1.2341
        val expected = "1.23"
        val digits = 2
        value.format(digits) shouldBe expected
        value.toFloat().format(digits) shouldBe expected
        value.toBigDecimal().format(digits) shouldBe expected
    }
}

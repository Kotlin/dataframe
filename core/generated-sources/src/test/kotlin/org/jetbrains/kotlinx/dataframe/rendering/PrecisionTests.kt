package org.jetbrains.kotlinx.dataframe.rendering

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.impl.scale
import org.jetbrains.kotlinx.dataframe.io.RendererDecimalFormat
import org.jetbrains.kotlinx.dataframe.io.defaultPrecision
import org.jetbrains.kotlinx.dataframe.io.format
import org.junit.Test
import java.text.DecimalFormatSymbols

class PrecisionTests {

    @Test
    fun precision() {
        columnOf(1.2, 3.2).scale() shouldBe 1
        columnOf(1.1232, 3.2).scale() shouldBe 4
        columnOf(1.1220001, 12313).scale() shouldBe defaultPrecision
        columnOf(1, 2).scale() shouldBe 0
        columnOf(1.0, 2).scale() shouldBe 1
        columnOf(123121.0, -1231.0).scale() shouldBe 1
        columnOf(123121.00001, -1231.120).scale() shouldBe 5
        columnOf(0.000343434343434343434343).scale() shouldBe defaultPrecision
        columnOf(1E24).scale() shouldBe -23
    }

    @Test
    fun format() {
        val d = DecimalFormatSymbols.getInstance().decimalSeparator
        val value = 1.2341
        val expected = "1${d}23"
        val digits = 2
        val formatter = RendererDecimalFormat.fromPrecision(digits)
        value.format(formatter) shouldBe expected
        value.toFloat().format(formatter) shouldBe expected
        value.toBigDecimal().format(formatter) shouldBe expected
    }

    @Test
    fun emptyColPrecision() {
        val col by columnOf(1.0)
        col.filter { false }.scale() shouldBe 0
    }
}

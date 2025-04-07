package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asSequence
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.ddof_default
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class StdTests {

    @Test
    fun `std one column`() {
        val value by columnOf(1, 2, 3)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Int>(), false, ddof_default) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
        df.std().columnTypes().single() shouldBe typeOf<Double>()
    }

    @Test
    fun `std one byte column`() {
        val value by columnOf(1.toByte(), 2.toByte(), 3.toByte())
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Byte>(), false, ddof_default) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
        df.std().columnTypes().single() shouldBe typeOf<Double>()
    }

    @Test
    fun `std one double column`() {
        val value by columnOf(1.0, 2.0, 3.0)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.asSequence().std(typeOf<Double>(), false, ddof_default) shouldBe expected

        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
    }

    @Test
    fun `std on empty or nullable column`() {
        val empty = DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false))
        val nullable = DataColumn.createValueColumn("", listOf(null), nothingType(true))

        empty.asSequence().std(empty.type, false, ddof_default).shouldBeNaN()
        shouldThrow<IllegalStateException> {
            nullable.asSequence().std(nullable.type, false, ddof_default).shouldBeNaN()
        }
        empty.std().shouldBeNaN()
        nullable.std().shouldBeNaN()
    }
}

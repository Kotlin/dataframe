package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.std
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.jetbrains.kotlinx.dataframe.math.std
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.TypeOf
import org.junit.Test

class StdTests {

    @Test
    fun `std one column`() {
        val value by columnOf(1, 2, 3)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.values().std() shouldBe expected
        value.values().std(TypeOf.INT) shouldBe expected
        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
        df.std().columnTypes().single() shouldBe TypeOf.DOUBLE
    }

    @Test
    fun `std one double column`() {
        val value by columnOf(1.0, 2.0, 3.0)
        val df = dataFrameOf(value)
        val expected = 1.0

        value.values().std() shouldBe expected
        value.values().std(TypeOf.DOUBLE) shouldBe expected
        value.std() shouldBe expected
        df[value].std() shouldBe expected
        df.std { value } shouldBe expected
    }

    @Test
    fun `std on empty or nullable column`() {
        val empty = DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false))
        val nullable = DataColumn.createValueColumn("", listOf(null), nothingType(true))

        empty.values().std(empty.type) shouldBe Double.NaN
        nullable.values().std(nullable.type) shouldBe Double.NaN

        empty.std() shouldBe Double.NaN
        nullable.std() shouldBe Double.NaN
    }
}

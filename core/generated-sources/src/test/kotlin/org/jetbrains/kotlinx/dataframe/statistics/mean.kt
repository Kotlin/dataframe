package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Test
import kotlin.reflect.typeOf

class MeanTests {

    @Test
    fun `type for column with mixed numbers`() {
        val col = columnOf<Number?>(10, 10.0, null)
        col.type() shouldBe typeOf<Number?>()
    }

    @Test
    fun `mean with nans and nulls`() {
        columnOf<Number?>(10, 20, Double.NaN, null).mean().shouldBeNaN()
        columnOf<Number?>(10, 20, Double.NaN, null).mean(skipNaN = true) shouldBe 15

        DataColumn.createValueColumn("", emptyList<Nothing>(), nothingType(false)).mean().shouldBeNaN()
        DataColumn.createValueColumn("", listOf(null), nothingType(true)).mean().shouldBeNaN()
    }
}

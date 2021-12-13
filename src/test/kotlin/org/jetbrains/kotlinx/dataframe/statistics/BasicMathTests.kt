package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.junit.Test

class BasicMathTests {

    @Test
    fun `type for column with mixed numbers`() {
        val col = columnOf(10, 10.0, null)
        col.type() shouldBe getType<Number?>()
    }

    @Test
    fun `mean with nans and nulls`() {
        columnOf(10, 20, Double.NaN, null).mean() shouldBe Double.NaN
        columnOf(10, 20, Double.NaN, null).mean(skipNA = true) shouldBe 15
    }
}

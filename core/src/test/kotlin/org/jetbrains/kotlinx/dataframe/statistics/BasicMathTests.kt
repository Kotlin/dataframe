package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.meanOrNull
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.junit.Test
import kotlin.reflect.typeOf

class BasicMathTests {

    @Test
    fun `type for column with mixed numbers`() {
        val col = columnOf(10, 10.0, null)
        col.type() shouldBe typeOf<Number?>()
    }

    @Test
    fun `mean with nans and nulls`() {
        columnOf(10, 20, Double.NaN, null).meanOrNull() shouldBe null
        columnOf(10, 20, Double.NaN, null).mean(skipNA = true) shouldBe 15

        DataColumn.createValueColumn("", emptyList<Nothing>(), nullableNothingType)
            .cast<Number?>()
            .meanOrNull() shouldBe null
        DataColumn.createValueColumn("", emptyList<Nothing>(), typeOf<Double?>())
            .cast<Double?>()
            .meanOrNull() shouldBe null
        DataColumn.createValueColumn("", listOf(null), typeOf<Double?>())
            .cast<Double?>()
            .meanOrNull() shouldBe null
    }
}

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumProd
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class CumProdSamples : DataFrameSampleHelper("cumProd", "api") {

    @Test
    fun cumProdOnColumn() {
        // SampleStart
        val products = columnOf(2, null, 3, 4).cumProd().toList()
        // SampleEnd
        products shouldBe listOf(2, null, 6, 24)
        products.toString().saveTextSample()
    }

    @Test
    fun cumProdWithoutSkipping() {
        // SampleStart
        val products = columnOf(2, null, 3, 4).cumProd(skipNA = false).toList()
        // SampleEnd
        products shouldBe listOf(2, null, null, null)
        products.toString().saveTextSample()
    }

    @Test
    fun cumProdBytePromotion() {
        // SampleStart
        val products: DataColumn<Int> = columnOf<Byte>(100, 2, 3).cumProd()
        // SampleEnd
        products.toList() shouldBe listOf(100, 200, 600)
        products.toList().toString().saveTextSample()
    }

    @Test
    fun cumProdGrowthFactors() {
        // SampleStart
        val growth = columnOf(1.25, 0.5, 2.0).cumProd().toList()
        // SampleEnd
        growth shouldBe listOf(1.25, 0.625, 1.25)
        growth.toString().saveTextSample()
    }

    @Test
    fun cumProdWithColumnConversion() {
        // SampleStart
        val factor by column<Int?>()
        val df = dataFrameOf("symbol", "factor")("A", 2, "A", null, "A", 3, "A", 4)
        val products = df.convert { factor }.asColumn { it.cumProd() }
        // SampleEnd
        products["factor"].toList() shouldBe listOf(2, null, 6, 24)
        products["factor"].toList().toString().saveTextSample()
    }

    @Test
    fun cumProdWithGroups() {
        // SampleStart
        val factor by column<Int?>()
        val df = dataFrameOf("symbol", "factor")("A", 2, "B", 7, "A", null, "B", 4, "A", 3)
        val products = df.groupBy("symbol")
            .updateGroups { convert { factor }.asColumn { it.cumProd() } }
            .concat()
        // SampleEnd
        products["factor"].toList() shouldBe listOf(2, null, 6, 7, 28)
        products["factor"].toList().toString().saveTextSample()
    }
}

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumMax
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class CumMaxSamples : DataFrameSampleHelper("cumMax", "api") {

    @Test
    fun cumMaxOnColumn() {
        // SampleStart
        val highs = columnOf(2, 5, null, 3, 8).cumMax().toList()
        // SampleEnd
        highs shouldBe listOf(2, 5, null, 5, 8)
        highs.toString().saveTextSample()
    }

    @Test
    fun cumMaxWithoutSkipping() {
        // SampleStart
        val highs = columnOf(2, 5, null, 3, 8).cumMax(skipNA = false).toList()
        // SampleEnd
        highs shouldBe listOf(2, 5, null, null, null)
        highs.toString().saveTextSample()
    }

    @Test
    fun cumMaxWithColumnConversion() {
        // SampleStart
        val price by column<Int?>()
        val df = dataFrameOf("symbol", "price")("A", 2, "A", null, "A", 5, "A", 3)
        val highs = df.convert { price }.asColumn { it.cumMax() }
        // SampleEnd
        highs["price"].toList() shouldBe listOf(2, null, 5, 5)
        highs["price"].toList().toString().saveTextSample()
    }

    @Test
    fun cumMaxWithGroups() {
        // SampleStart
        val price by column<Int?>()
        val df = dataFrameOf("symbol", "price")("A", 2, "B", 7, "A", null, "B", 4, "A", 5)
        val highs = df.groupBy("symbol")
            .updateGroups { convert { price }.asColumn { it.cumMax() } }
            .concat()
        // SampleEnd
        highs["price"].toList() shouldBe listOf(2, null, 5, 7, 7)
        highs["price"].toList().toString().saveTextSample()
    }
}

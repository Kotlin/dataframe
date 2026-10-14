package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.medianBy
import org.jetbrains.kotlinx.dataframe.api.medianFor
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.percentileFor
import org.jetbrains.kotlinx.dataframe.api.percentileOf
import org.jetbrains.kotlinx.dataframe.math.QuantileEstimationMethod
import org.junit.Test

class EstimationMethodSamples {

    @Test
    fun estimationMethodColumn() {
        // SampleStart
        val values = columnOf(4, 1, 3, 2)
        val selected: Int = values.percentile(25.0, method = QuantileEstimationMethod.R1)
        val interpolated: Double = values.percentile(25.0, method = QuantileEstimationMethod.R7)
        // SampleEnd
        selected shouldBe 1
        interpolated shouldBe 1.75
    }

    @Test
    fun estimationMethodMedian() {
        // SampleStart
        val words = columnOf("e", "a", "c", "b", "d")
        val first = words.median(method = QuantileEstimationMethod.R1)
        val nearest = words.median(method = QuantileEstimationMethod.R3)
        // SampleEnd
        first shouldBe "c"
        nearest shouldBe "b"
    }

    @Test
    fun estimationMethodDataFrame() {
        // SampleStart
        val df = dataFrameOf("x", "label")(4, "d", 1, "a", 3, "c", 2, "b")
        val perColumn = df.percentileFor(25.0, QuantileEstimationMethod.R1) { "x"<Int>() and "label"<String>() }
        val numeric = df.percentileFor(25.0, QuantileEstimationMethod.R7, "x")
        val expression = df.percentileOf(25.0, QuantileEstimationMethod.R7) { "x"<Int>() * 2 }
        // SampleEnd
        perColumn["label"] shouldBe "a"
        numeric["x"] shouldBe 1.75
        expression shouldBe 3.5
    }

    @Test
    fun estimationMethodGrouped() {
        // SampleStart
        val df = dataFrameOf("group", "x")("a", 4, "a", 1, "a", 3, "a", 2)
        val result = df.groupBy("group").medianFor(QuantileEstimationMethod.R1, "x")
        // SampleEnd
        result[0]["x"] shouldBe 2
    }

    @Test
    fun estimationMethodBy() {
        // SampleStart
        val df = dataFrameOf("id", "score")("a", Double.NaN, "b", 1.0, "c", 4.0, "d", 2.0)
        val selectedRow = df.medianBy(QuantileEstimationMethod.R1, "score", skipNaN = true)
        // SampleEnd
        selectedRow["id"] shouldBe "d"
    }
}

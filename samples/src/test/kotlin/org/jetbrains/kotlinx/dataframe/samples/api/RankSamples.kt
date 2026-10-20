package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.RankMethod
import org.jetbrains.kotlinx.dataframe.api.RankNAOption
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.rank
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class RankSamples : DataFrameSampleHelper("rank", "api") {

    @Test
    fun rankOnColumn() {
        // SampleStart
        val ranks = columnOf(20, null, 10, 20).rank().toList()
        // SampleEnd
        ranks shouldBe listOf(2.5, null, 1.0, 2.5)
        ranks.toString().saveTextSample()
    }

    @Test
    fun rankFirstDescending() {
        // SampleStart
        val ranks = columnOf(20, 10, 20, 30).rank(RankMethod.First, ascending = false).toList()
        // SampleEnd
        ranks shouldBe listOf(2.0, 4.0, 3.0, 1.0)
        ranks.toString().saveTextSample()
    }

    @Test
    fun rankMissingAtTop() {
        // SampleStart
        val ranks = columnOf(20.0, null, 10.0, Double.NaN, 20.0).rank(naOption = RankNAOption.Top).toList()
        // SampleEnd
        ranks shouldBe listOf(4.5, 1.5, 3.0, 1.5, 4.5)
        ranks.toString().saveTextSample()
    }

    @Test
    fun rankDenseFractions() {
        // SampleStart
        val ranks = columnOf(20, null, 10, 20).rank(RankMethod.Dense, pct = true).toList()
        // SampleEnd
        ranks shouldBe listOf(1.0, null, 0.5, 1.0)
        ranks.toString().saveTextSample()
    }

    @Test
    fun rankStrings() {
        // SampleStart
        val ranks = columnOf("pear", "apple", "pear").rank().toList()
        // SampleEnd
        ranks shouldBe listOf(2.5, 1.0, 2.5)
        ranks.toString().saveTextSample()
    }

    @Test
    fun rankWithColumnConversion() {
        // SampleStart
        val score by column<Int?>()
        val df = dataFrameOf("label", "score")("A", 20, "B", null, "C", 10, "D", 20)
        val ranks = df.convert { score }.asColumn { it.rank() }
        // SampleEnd
        ranks["score"].toList() shouldBe listOf(2.5, null, 1.0, 2.5)
        ranks["score"].toList().toString().saveTextSample()
    }

    @Test
    fun rankWithGroups() {
        // SampleStart
        val score by column<Int?>()
        val df = dataFrameOf("group", "score")("A", 20, "B", 7, "A", null, "B", 4, "A", 10, "A", 20)
        val ranks = df.groupBy("group")
            .updateGroups { convert { score }.asColumn { it.rank(pct = true) } }
            .concat()
        // SampleEnd
        ranks["score"].toList() shouldBe listOf(2.5 / 3, null, 1.0 / 3, 2.5 / 3, 1.0, 0.5)
        ranks["score"].toList().toString().saveTextSample()
    }
}

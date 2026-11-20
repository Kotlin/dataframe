package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.max
import org.jetbrains.kotlinx.dataframe.api.maxOrNull
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.api.min
import org.jetbrains.kotlinx.dataframe.api.minOrNull
import org.jetbrains.kotlinx.dataframe.api.sum
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregator
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.multipleColumnsHandlers.NoMultipleColumnsHandler
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.nothingType
import org.junit.Test

/** https://github.com/Kotlin/dataframe/issues/1693 */
class SingleColumnAggregateTests {

    @Test
    fun `DataFrame aggregateAll with one column skips multiple-columns path`() {
        val min = Aggregators.min<Int>(false)
        // an aggregator that throws when asked to aggregate multiple columns
        val singleOnly = Aggregator(
            aggregationHandler = min.aggregationHandler,
            inputHandler = min.inputHandler,
            multipleColumnsHandler = NoMultipleColumnsHandler(),
            name = min.name,
            statisticsParameters = min.statisticsParameters,
        )
        try {
            val df = dataFrameOf("a" to columnOf(3, 1, 2))
            df.aggregateAll(singleOnly) { "a"<Int>() } shouldBe 1
        } finally {
            // handlers are shared with the cached aggregator, so point them back
            min.aggregationHandler.init(min)
            min.inputHandler.init(min)
        }
    }

    @Test
    fun `single column results are unchanged`() {
        val ints = columnOf(5, 2, null, 9)
        val df = dataFrameOf("a" to ints)
        df.min { "a"<Int?>() } shouldBe 2
        df.max { "a"<Int?>() } shouldBe 9
        df.sum { "a"<Int?>() } shouldBe 16
        df.mean { "a"<Int?>() } shouldBe 16.0 / 3

        // mixed number types in one column
        val mixed = dataFrameOf("a" to columnOf<Number>(5, 2L, 8.0f, 1.0, 9.toShort()))
        mixed.sum { "a"<Number>() } shouldBe 25.0
        mixed.mean { "a"<Number>() } shouldBe 5.0

        // empty and all-null columns
        val empty = DataColumn.createValueColumn("a", emptyList<Nothing>(), nothingType(false))
        dataFrameOf(empty).minOrNull { "a"<Nothing>() }.shouldBeNull()
        val nulls = DataColumn.createValueColumn("a", listOf(null, null), nothingType(true))
        dataFrameOf(nulls).maxOrNull { "a"<Nothing?>() }.shouldBeNull()

        // skipNaN variants
        val doubles = dataFrameOf("a" to columnOf(1.0, Double.NaN, 3.0))
        doubles.min { "a"<Double>() }.shouldBeNaN()
        doubles.min(skipNaN = true) { "a"<Double>() } shouldBe 1.0
        doubles.max(skipNaN = true) { "a"<Double>() } shouldBe 3.0
        doubles.mean(skipNaN = true) { "a"<Double>() } shouldBe 2.0
        doubles.mean { "a"<Double>() }.shouldBeNaN()
    }
}

package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.percentile
import org.jetbrains.kotlinx.dataframe.api.rowPercentileOf
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class PercentileTests {

    @Test
    fun `percentile of two columns`() {
        val df = dataFrameOf("a", "b")(
            1, 4,
            2, 6,
            7, 7,
        )
        df.percentile(60.0, "a", "b") shouldBe 6.133333333333333
    }

    @Test
    fun `row percentile`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 3, 3,
            2, 4, 10,
            7, 7, 1,
        )
        df.mapToColumn("", Infer.Type) { it.rowPercentileOf<Int>(25.0) } shouldBe
            columnOf(1, 2, 2)
    }
}

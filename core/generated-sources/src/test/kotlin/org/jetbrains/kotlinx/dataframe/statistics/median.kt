package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.mapToColumn
import org.jetbrains.kotlinx.dataframe.api.median
import org.jetbrains.kotlinx.dataframe.api.rowMedian
import org.junit.Test

class MedianTests {

    @Test
    fun `median of two columns`() {
        val df = dataFrameOf("a", "b")(
            1, 4,
            2, 6,
            7, 7
        )
        df.median("a", "b") shouldBe 5
    }

    @Test
    fun `row median`() {
        val df = dataFrameOf("a", "b")(
            1, 3,
            2, 4,
            7, 7
        )
        df.mapToColumn("", Infer.Type) { it.rowMedian() } shouldBe columnOf(2, 3, 7)
    }
}

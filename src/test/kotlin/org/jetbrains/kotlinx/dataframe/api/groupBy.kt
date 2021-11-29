package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class GroupByTests {

    @Test
    fun `groupBy values with nulls`() {
        val df = dataFrameOf("a", "b")(
            1, 1,
            1, null,
            2, null,
            3, 1
        )
        df.groupBy("a").values { "b" into "c" } shouldBe dataFrameOf("a", "c")(
            1, listOf(1, null),
            2, listOf(null),
            3, listOf(1)
        )
        df.groupBy("a").values(dropNA = true) { "b" into "c" } shouldBe dataFrameOf("a", "c")(
            1, listOf(1),
            2, listOf<Int>(),
            3, listOf(1)
        )
    }
}

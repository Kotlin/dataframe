package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.junit.Test
import kotlin.reflect.typeOf

class GroupByTests {

    @Test
    fun `groupBy values with nulls`() {
        val df = dataFrameOf(
            "a", "b"
        )(
            1, 1,
            1, null,
            2, null,
            3, 1,
        )

        df.groupBy("a").values { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c"
            )(
                1, listOf(1, null),
                2, listOf(null),
                3, listOf(1),
            )

        df.groupBy("a").values(dropNA = true) { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c"
            )(
                1, listOf(1),
                2, emptyList<Int>(),
                3, listOf(1),
            )
    }

    @Test
    fun `aggregate FrameColumns into new column`() {
        val df = dataFrameOf(
            "a", "b", "c"
        )(
            1, 2, 3,
            4, 5, 6,
        )
        val grouped = df.groupBy("a", "b").into("d")

        grouped.groupBy("a").aggregate {
            getColumn("d") into "e"
        }["e"].type() shouldBe typeOf<List<AnyFrame>>()

        grouped.groupBy("a").aggregate {
            getFrameColumn("d") into "e"
        }["e"].type() shouldBe typeOf<List<AnyFrame>>()
    }
}

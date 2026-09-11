package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class GroupByTests {

    @Test
    fun `groupBy values with nulls`() {
        val df = dataFrameOf(
            "a", "b",
        )(
            1, 1,
            1, null,
            2, null,
            3, 1,
        )

        df.groupBy("a").values { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c",
            )(
                1, listOf(1, null),
                2, listOf(null),
                3, listOf(1),
            )

        df.groupBy("a").values(dropNA = true) { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c",
            )(
                1, listOf(1),
                2, emptyList<Int>(),
                3, listOf(1),
            )
    }

    @Test
    fun `aggregate FrameColumns into new column`() {
        val df = dataFrameOf(
            "a", "b", "c",
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

    @Test
    fun `groupBy preserves value rows and group order`() {
        val df = dataFrameOf(
            "key1" to listOf(2, 1, 2, 1, null, null),
            "key2" to listOf("b", "a", "b", "c", "a", "a"),
            "value" to listOf(20, null, 21, 11, 30, null),
            "group" to listOf("existing", "column", "name", "collision", "is", "covered"),
        )

        val singleKey = df.groupBy { cols("key1") }
        singleKey.keys["key1"].toList() shouldBe listOf(2, 1, null)
        singleKey.groups.toList() shouldBe listOf(df[0, 2], df[1, 3], df[4, 5])

        val multipleKeys = df.groupBy { "key1" and "key2" }
        multipleKeys.keys["key1"].toList() shouldBe listOf(2, 1, 1, null)
        multipleKeys.keys["key2"].toList() shouldBe listOf("b", "a", "c", "a")
        multipleKeys.groups.toList() shouldBe listOf(df[0, 2], df[listOf(1)], df[listOf(3)], df[4, 5])

        val noKeys = df.groupBy { none() }
        noKeys.groups.toList() shouldBe listOf(df)
    }

    @Test
    fun `groupBy preserves hierarchical and frame columns`() {
        val nestedFrameColumn = DataColumn.createFrameColumn(
            name = "nestedFrames",
            groups = List(4) { index -> dataFrameOf("nestedInner")(index, index + 1) },
        )
        val topLevelFrameColumn = DataColumn.createFrameColumn(
            name = "frames",
            groups = List(4) { index -> dataFrameOf("inner")(index, index + 1) },
        )
        val df = dataFrameOf(
            "nestedKey" to listOf(1, 2, 1, 2),
            "nestedValue" to listOf("a", "b", "c", "d"),
            "value" to listOf(10, 20, 30, 40),
        ).addAll(nestedFrameColumn)
            .group { "nestedValue" and "nestedFrames" }.into("details")
            .group { "nestedKey" and "details" }.into("nested")
            .addAll(topLevelFrameColumn)

        listOf(false, true).forEach { moveToTop ->
            val grouped = df.groupBy(moveToTop) { it["nested"]["nestedKey"] }
            grouped.groups.toList() shouldBe listOf(df[0, 2], df[1, 3])
            grouped.groups.toList().forEach { it.schema() shouldBe df.schema() }
        }
    }

    @Test
    fun `groupBy handles nullable dataframe value columns`() {
        val frame = dataFrameOf("inner")(1)
        val df = dataFrameOf(
            DataColumn.createValueColumn("key", listOf(1, 2)),
            DataColumn.createValueColumn("frame", listOf(frame, null)),
        ).group { cols("frame") }.into("nested")

        shouldNotThrowAny {
            df.groupBy("key")
        }
    }

    @Test
    fun `groupBy handles empty dataframes`() {
        val df = dataFrameOf(
            "key" to emptyList<Int>(),
            "value" to emptyList<String?>(),
        )

        df.groupBy { cols("key") }.groups.size() shouldBe 0
        df.groupBy { none() }.groups.size() shouldBe 0
    }
}

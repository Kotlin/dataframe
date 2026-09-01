package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.junit.Test

class GroupByOptimizationTest {

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
        val frameColumn = DataColumn.createFrameColumn(
            name = "frames",
            groups = List(4) { index -> dataFrameOf("inner")(index, index + 1) },
        )
        val df = dataFrameOf(
            "nestedKey" to listOf(1, 2, 1, 2),
            "nestedValue" to listOf("a", "b", "c", "d"),
            "value" to listOf(10, 20, 30, 40),
        ).group { "nestedKey" and "nestedValue" }.into("nested")
            .addAll(frameColumn)

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
        )

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

package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl1
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl2
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl3
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl4
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl5
import org.junit.Test

class GroupByVariantsTest {

    @Test
    fun `all variants match the baseline for value columns`() {
        val df = dataFrameOf(
            "key1" to listOf(2, 1, 2, 1, null, null),
            "key2" to listOf("b", "a", "b", "c", "a", "a"),
            "value" to listOf(20, null, 21, 11, 30, null),
            "group" to listOf("existing", "column", "name", "collision", "is", "covered"),
        )

        df.assertVariantsEqual { cols("key1") }
        df.assertVariantsEqual { "key1" and "key2" }
        df.assertVariantsEqual { none() }
    }

    @Test
    fun `all variants match the baseline for hierarchical columns`() {
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

        df.assertVariantsEqual(moveToTop = false) { it["nested"]["nestedKey"] }
        df.assertVariantsEqual(moveToTop = true) { it["nested"]["nestedKey"] }
    }

    @Test
    fun `all variants match the baseline for an empty dataframe`() {
        val df = dataFrameOf(
            "key" to emptyList<Int>(),
            "value" to emptyList<String?>(),
        )

        df.assertVariantsEqual { cols("key") }
        df.assertVariantsEqual { none() }
    }

    private fun <T> DataFrame<T>.assertVariantsEqual(moveToTop: Boolean = true, columns: ColumnsSelector<T, *>) {
        val expected = groupBy(moveToTop, columns)
        val variants = listOf(
            "groupBy1" to groupByImpl1(moveToTop, columns),
            "groupBy2" to groupByImpl2(moveToTop, columns),
            "groupBy3" to groupByImpl3(moveToTop, columns),
            "groupBy4" to groupByImpl4(moveToTop, columns),
            "groupBy5" to groupByImpl5(moveToTop, columns),
        )

        variants.forEach { (name, actual) ->
            withClue(name) {
                actual.toDataFrame() shouldBe expected.toDataFrame()
                actual.keys.schema() shouldBe expected.keys.schema()
                actual.groups.size() shouldBe expected.groups.size()
                for (index in 0 until expected.groups.size()) {
                    actual.groups[index].schema() shouldBe expected.groups[index].schema()
                }
            }
        }
    }
}

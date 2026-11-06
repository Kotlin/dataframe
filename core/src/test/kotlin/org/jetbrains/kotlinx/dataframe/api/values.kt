package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class ValuesTests {

    @Test
    fun `values traverses the frame column by column by default`() {
        val df = dataFrameOf(
            "a" to listOf(1, 2),
            "b" to listOf(3, 4),
        )

        df.values().toList() shouldBe listOf(1, 2, 3, 4)
    }

    @Test
    fun `values with byRows traverses the frame row by row`() {
        val df = dataFrameOf(
            "a" to listOf(1, 2),
            "b" to listOf(3, 4),
        )

        df.values(byRows = true).toList() shouldBe listOf(1, 3, 2, 4)
    }

    @Test
    fun `values extracts from the selected columns in selector order`() {
        val df = dataFrameOf(
            "a" to listOf(1, 2),
            "b" to listOf(3, 4),
        )

        df.values { cols("b", "a") }.toList() shouldBe listOf(3, 4, 1, 2)
    }

    @Test
    fun `valuesNotNull skips nulls`() {
        val df = dataFrameOf(
            "a" to listOf(null, 2),
            "b" to listOf(3, null),
        )

        df.valuesNotNull().toList() shouldBe listOf(2, 3)
    }

    @Test
    fun `values in groupBy aggregation with distinct keeps only distinct values`() {
        val df = dataFrameOf(
            "a" to listOf(1, 1, 2),
            "b" to listOf(1, 1, 1),
        )

        df.groupBy("a").values(distinct = true) { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c",
            )(
                1, listOf(1),
                2, listOf(1),
            )
    }

    @Test
    fun `values in pivot aggregation with dropNA and distinct filters each cell`() {
        val df = dataFrameOf(
            "k" to listOf("x", "x", "x", "y"),
            "v" to listOf(1, 1, 2, null),
        )

        val result = df.pivot("k").values("v", dropNA = true, distinct = true).toDataFrame()

        result.columnNames() shouldBe listOf("x", "y")
        result.getColumn("x").values() shouldBe listOf(listOf(1, 2))
        result.getColumn("y").values() shouldBe listOf(null)
    }

    @Test
    fun `values in pivotGroupBy aggregation with distinct deduplicates each cell`() {
        val df = dataFrameOf(
            "k" to listOf("x", "x", "y"),
            "g" to listOf("a", "a", "a"),
            "v" to listOf(1, 1, 2),
        )

        val result = df.pivot("k").groupBy("g").values("v", distinct = true)

        result.columnNames() shouldBe listOf("g", "k")
        result.getColumnGroup("k").columnNames() shouldBe listOf("x", "y")
        result["g"].values() shouldBe listOf("a")
        result["k"]["x"].values() shouldBe listOf(1)
        result["k"]["y"].values() shouldBe listOf(2)
    }
}

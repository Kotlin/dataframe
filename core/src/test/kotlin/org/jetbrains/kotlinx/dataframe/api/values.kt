package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
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

    @Test
    fun `values in pivotGroupBy keeps a one-value cell in a list when a sibling cell holds several`() {
        val df = dataFrameOf(
            "name" to listOf("Alice", "Alice", "Bob", "Bob", "Bob"),
            "isHappy" to listOf(true, false, true, true, false),
            "age" to listOf(15, 20, 45, 30, 20),
        )

        val result = df.pivot("name").groupBy("isHappy").values("age")

        // no cell of the "Alice" column holds several values: the single values are unwrapped
        result["name"]["Alice"].values() shouldBe listOf(15, 20)
        // the "Bob" column has a two-value cell: every cell of that column holds a list
        result["name"]["Bob"].values() shouldBe listOf(listOf(45, 30), listOf(20))
    }

    @Test
    fun `values in groupBy collects a column group as a nested frame`() {
        val df = dataFrameOf(
            "firstName" to listOf("Alice", "Bob", "Charlie", "Alice"),
            "lastName" to listOf("Cooper", "Dylan", "Daniels", "Wolf"),
            "isHappy" to listOf(true, true, false, false),
            "age" to listOf(15, 45, 20, 20),
        ).group("firstName", "lastName").into("name")

        val result = df.groupBy("isHappy").values("name", "age")

        result["age"].values() shouldBe listOf(listOf(15, 45), listOf(20, 20))
        // the selected column group is collected as a nested frame, not as a list
        val nameColumn = result.getColumn("name")
        nameColumn.kind() shouldBe ColumnKind.Frame
        val frames = nameColumn.values().toList()
        (frames[0] as DataFrame<*>)["firstName"].values() shouldBe listOf("Alice", "Bob")
        (frames[1] as DataFrame<*>)["firstName"].values() shouldBe listOf("Charlie", "Alice")
    }

    @Test
    fun `values in pivot collects a column group as a nested frame`() {
        val df = dataFrameOf(
            "firstName" to listOf("Alice", "Bob", "Charlie", "Alice"),
            "lastName" to listOf("Cooper", "Dylan", "Daniels", "Wolf"),
            "isHappy" to listOf(true, true, false, false),
            "age" to listOf(15, 45, 20, 20),
        ).group("firstName", "lastName").into("name")

        val result = df.pivot("isHappy").values("name", "age")

        val happyCell = result.df().getColumnGroup("true")
        happyCell.getColumn("age").values() shouldBe listOf(listOf(15, 45))
        val nameFrame = happyCell.getColumn("name").values().first() as DataFrame<*>
        nameFrame["firstName"].values() shouldBe listOf("Alice", "Bob")
    }

    @Test
    fun `values in reducedGroupBy gives null when the reduction picked no row`() {
        val df = dataFrameOf(
            "city" to listOf("London", "Milan"),
            "age" to listOf(15, 25),
        )

        val result = df.groupBy("city").first { getValue<Int>("age") > 100 }.values("age")

        result["age"].values() shouldBe listOf(null, null)
    }
}

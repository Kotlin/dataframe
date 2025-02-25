package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
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
    fun sum() {
        val personsDf = dataFrameOf("name", "age", "city", "weight", "height")(
            "Alice", 15, "London", 99.5, "1.85",
            "Bob", 20, "Paris", 140.0, "1.35",
            "Charlie", 100, "Dubai", 75, "1.95",
            "Rose", 1, "Moscow", 45.3, "0.79",
            "Dylan", 35, "London", 23.4, "1.83",
            "Eve", 40, "Paris", 56.7, "1.85",
            "Frank", 55, "Dubai", 78.9, "1.35",
            "Grace", 29, "Moscow", 67.8, "1.65",
            "Hank", 60, "Paris", 80.2, "1.75",
            "Isla", 22, "London", 75.1, "1.85",
            )

        // scenario #0: all numerical columns
        val res0 = personsDf.groupBy ( "city" ).sum()
        res0.columnNames() shouldBe listOf("city", "age", "weight")

        val sum01 = res0["age"][0] as Int
        sum01 shouldBe 72
        val sum02 = res0["weight"][0] as Double
        sum02 shouldBe 198.0

        // scenario #1: particular column
        val res1 = personsDf.groupBy ( "city" ).sumFor("age")
        res1.columnNames() shouldBe listOf("city", "age")

        val sum11 = res1["age"][0] as Int
        sum11 shouldBe 72

        // scenario #1.1: particular column via sum
        val res11 = personsDf.groupBy ( "city" ).sum("age")
        res11.columnNames() shouldBe listOf("city", "age")

        val sum111 = res11["age"][0] as Int
        sum111 shouldBe 72

        // scenario #2: particular column with new name - schema changes
        val res2 = personsDf.groupBy ( "city" ).sum("age", name = "newAge")
        res2.columnNames() shouldBe listOf("city", "newAge")
        res2.print()
        val sum21 = res2["newAge"][0] as Int
        sum21 shouldBe 72

        // scenario #3: create new column via expression
        val res3 = personsDf.groupBy ( "city" ).sumOf(resultName = "ageSum") { "age"<Int>() * 10 }
        res3.columnNames() shouldBe listOf("city", "ageSum")

        val sum31 = res3["ageSum"][0] as Int
        sum31 shouldBe 720
    }
}

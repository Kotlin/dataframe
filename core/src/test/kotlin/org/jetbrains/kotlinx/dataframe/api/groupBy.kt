package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators.mean
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


    @Test
    fun `aggregate and check column names`() {
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", 35, false,
            "Charlie", "Chaplin", 40, "Milan", 41, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, "Milan", 55, false,
            "Charlie", "Byrd", 30, "Moscow", 90, true
        ).cast<Person>()


        val aggregate = df.groupBy("city")
            .aggregate {
                mean() into "mean"
                std() into "std"
            }

        aggregate
            .flatten()
            .columnNames() shouldBe listOf("city", "age.mean", "weight.mean", "age.std", "weight.std")
    }




    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val firstName: String
        val lastName: String
        val weight: Int?
        val isHappy: Boolean
    }
}

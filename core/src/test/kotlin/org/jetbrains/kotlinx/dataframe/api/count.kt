package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test

class CountTests {

    // Test data

    val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Charlie"),
        "age" to columnOf(15, 20, 25),
        "group" to columnOf(1, 1, 2),
    )
    val age = df["age"].cast<Int>()
    val name = df["name"].cast<String>()
    val grouped = df.groupBy("group")
    val pivot = df.pivot("group")

    val emptyDf = df.drop(df.nrow)

    val dfWithNulls = df.append("Martin", null, null)
    val ageWithNulls = dfWithNulls["age"].cast<Int?>()
    val groupedWithNulls = dfWithNulls.groupBy("group")
    val pivotWithNulls = dfWithNulls.pivot("group")

    // DataColumn

    @Test
    fun `count on DataColumn`() {
        age.count() shouldBe 3
        age.count { it > 18 } shouldBe 2
        name.count { it.startsWith("A") } shouldBe 1
    }

    @Test
    fun `count on empty DataColumn`() {
        emptyDf["name"].count() shouldBe 0
        emptyDf["name"].count { it == "Alice" } shouldBe 0
    }

    @Test
    fun `count on DataColumn with nulls`() {
        ageWithNulls.count() shouldBe 4
        ageWithNulls.count { it == null } shouldBe 1
    }

    // DataRow

    @Test
    fun `count on DataRow`() {
        val row = df[0]
        row.count() shouldBe 3
        (row.count { it is Number }) shouldBe 2
    }

    @Test
    fun `count on DataRow with nulls`() {
        val row = dfWithNulls[3]
        row.count() shouldBe 3
        row.count { it == null } shouldBe 2
    }

    // DataFrame

    @Test
    fun `count on DataFrame`() {
        df.count() shouldBe 3
        df.count { age > 18 } shouldBe 2
        df.count { it["name"] == "Alice" } shouldBe 1
    }

    @Test
    fun `count on empty DataFrame`() {
        emptyDf.count() shouldBe 0
    }

    @Test
    fun `count on DataFrame with nulls`() {
        dfWithNulls.count() shouldBe 4
        dfWithNulls.count { it["age"] != null } shouldBe 3
    }

    // GroupBy

    @Test
    fun `count on grouped DataFrame`() {
        val groupedCount = grouped.count()
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "count" to columnOf(2, 1),
        )
        groupedCount shouldBe expected
    }

    @Test
    fun `count on grouped DataFrame with predicate`() {
        val groupedCount = grouped.count { "age"<Int>() > 18 }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2),
            "count" to columnOf(1, 1),
        )
        groupedCount shouldBe expected
    }

    @Test
    fun `count on empty grouped DataFrame`() {
        emptyDf.groupBy("group").count().count() shouldBe 0
    }

    @Test
    fun `count on grouped DataFrame with nulls`() {
        val groupedWithNullsCount = groupedWithNulls.count()
        val expected = dataFrameOf(
            "group" to columnOf(1, 2, null),
            "count" to columnOf(2, 1, 1),
        )
        groupedWithNullsCount shouldBe expected
    }

    @Test
    fun `count on grouped DataFrame with nulls and predicate`() {
        val groupedWithNullsCount = groupedWithNulls.count { it["age"] != null }
        val expected = dataFrameOf(
            "group" to columnOf(1, 2, null),
            "count" to columnOf(2, 1, 0),
        )
        groupedWithNullsCount shouldBe expected
    }

    // Pivot

    @Test
    fun `count on Pivot`() {
        val counted = pivot.count()
        val expected = dataFrameOf(
            "1" to columnOf(2),
            "2" to columnOf(1),
        )[0]
        counted shouldBe expected
    }

    @Test
    fun `count on Pivot with predicate`() {
        val counted = pivot.count { "group"<Int>() != 1 }
        val expected = dataFrameOf(
            "1" to columnOf(0),
            "2" to columnOf(1),
        )[0]
        counted shouldBe expected
    }

    @Test
    fun `count on Pivot with nulls`() {
        val counted = pivotWithNulls.count()
        val expected = dataFrameOf(
            "1" to columnOf(2),
            "2" to columnOf(1),
            "null" to columnOf(1),
        )[0]
        counted shouldBe expected
    }

    @Test
    fun `count on Pivot with nulls and predicate`() {
        val counted = pivotWithNulls.count { it["age"] != null }
        val expected = dataFrameOf(
            "1" to columnOf(2),
            "2" to columnOf(1),
            "null" to columnOf(0),
        )[0]
        counted shouldBe expected
    }

    // PivotGroupBy

    @Test
    fun `count on PivotGroupBy`() {
        val pivotGrouped = pivot.groupBy("age")
        val counted = pivotGrouped.count()
        val expected = dataFrameOf(
            "age" to columnOf(15, 20, 25),
            "group" to columnOf(
                "1" to columnOf(1, 1, 0),
                "2" to columnOf(0, 0, 1),
            ),
        )
        counted shouldBe expected
    }

    @Test
    fun `count on PivotGroupBy with predicate`() {
        val pivotGrouped = pivot.groupBy("age")
        val counted = pivotGrouped.count { "name"<String>() == "Alice" }
        val expected = dataFrameOf(
            "age" to columnOf(15, 20, 25),
            "group" to columnOf(
                "1" to columnOf(1, 0, 0),
                "2" to columnOf(0, 0, 0),
            ),
        )
        counted shouldBe expected
    }

    @Test
    fun `count on PivotGroupBy with nulls`() {
        val pivotGrouped = pivotWithNulls.groupBy("age")
        val counted = pivotGrouped.count()
        val expected = dataFrameOf(
            "age" to columnOf(15, 20, 25, null),
            "group" to columnOf(
                "1" to columnOf(1, 1, 0, 0),
                "2" to columnOf(0, 0, 1, 0),
                "null" to columnOf(0, 0, 0, 1),
            ),
        )
        counted shouldBe expected
    }

    @Test
    fun `count PivotGroupBy with nulls and predicate`() {
        val pivotGrouped = pivotWithNulls.groupBy("age")
        val counted = pivotGrouped.count { it["age"] != null && "age"<Int>() > 15 }
        val expected = dataFrameOf(
            "age" to columnOf(15, 20, 25, null),
            "group" to columnOf(
                "1" to columnOf(0, 1, 0, 0),
                "2" to columnOf(0, 0, 1, 0),
                "null" to columnOf(0, 0, 0, 0),
            ),
        )
        counted shouldBe expected
    }
}

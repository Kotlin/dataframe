package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.BeforeClass
import org.junit.Test

/**
 * Tests the behavior of the [count] function across different [DataFrame] structures:
 *
 * - [DataColumn]: counting all elements or elements matching a predicate,
 * including behavior on empty columns and columns with `null` values.
 *
 * - [DataRow]: counting elements or elements matching a predicate,
 * including rows containing `null` values.
 *
 * - [DataFrame]: counting all rows or rows matching a predicate in this [DataFrame],
 * including behavior on empty DataFrames and DataFrames with `null` values.
 *
 * - [GroupBy]: counting rows per group in the [GroupBy], with and without a predicate,
 * including behavior on grouped empty [DataFrame] and groups with `null` values.
 *
 * - [Pivot]: counting rows in each group of the [Pivot],
 * including handling of `null` values and predicates.
 *
 * - [PivotGroupBy]: counting rows in each combined [pivot] + [groupBy] group,
 * with and without predicates, including handling of `null` values and predicates.
 */
class CountTests {

    // region Test data

    companion object {
        lateinit var df: DataFrame<*>
        lateinit var age: DataColumn<Int>
        lateinit var name: DataColumn<String>
        lateinit var grouped: GroupBy<*, *>
        lateinit var pivoted: Pivot<*>
        lateinit var emptyDf: DataFrame<*>
        lateinit var dfWithNulls: DataFrame<*>
        lateinit var ageWithNulls: DataColumn<Int?>
        lateinit var groupedWithNulls: GroupBy<*, *>
        lateinit var pivotWithNulls: Pivot<*>

        @BeforeClass
        @JvmStatic
        fun setupTestData() {
            df = dataFrameOf(
                "name" to columnOf("Alice", "Bob", "Charlie"),
                "age" to columnOf(15, 20, 25),
                "group" to columnOf(1, 1, 2),
            )
            age = df["age"].cast()
            name = df["name"].cast()
            grouped = df.groupBy("group")
            pivoted = df.pivot("group")
            emptyDf = df.drop(df.nrow)
            dfWithNulls = df.append("Martin", null, null)
            ageWithNulls = dfWithNulls["age"].cast()
            groupedWithNulls = dfWithNulls.groupBy("group")
            pivotWithNulls = dfWithNulls.pivot("group")
        }
    }

    // endregion

    // region DataColumn

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

    // endregion

    // region DataRow

    @Test
    fun `count on DataRow`() {
        val row = df[0]
        row.count() shouldBe 3
        row.count { it is Number } shouldBe 2
    }

    @Test
    fun `count on DataRow with nulls`() {
        val row = dfWithNulls[3]
        row.count() shouldBe 3
        row.count { it == null } shouldBe 2
    }

    // endregion

    // region DataFrame

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

    // endregion

    // region GroupBy

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

    // `emptyDf.groupBy("group").count()` results in a dataframe without the column `count`
    // Issue #1531
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

    // endregion

    // region Pivot

    @Test
    fun `count on Pivot`() {
        val counted = pivoted.count()
        val expected = dataFrameOf(
            "1" to columnOf(2),
            "2" to columnOf(1),
        )[0]
        counted shouldBe expected
    }

    @Test
    fun `count on Pivot with predicate`() {
        val counted = pivoted.count { "group"<Int>() != 1 }
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

    // endregion

    // region PivotGroupBy

    @Test
    fun `count on PivotGroupBy`() {
        val pivotGrouped = pivoted.groupBy("age")
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
        val pivotGrouped = pivoted.groupBy("age")
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

    // endregion
}

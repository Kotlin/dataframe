package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

/**
 * Tests the behavior of the `frames` pivot aggregation:
 *
 * - on a [Pivot]: each pivot group stays a frame, one frame per pivot key value
 * (`df.pivot { isHappy }.frames()`, the call the `pivot` documentation page shows),
 * through both the Extension Properties API and the String API.
 *
 * - on a [PivotGroupBy]: one row per unique grouping value, with the pivot keys
 * inside a column group named after the pivoted column, and every cell holding
 * the rows of its grouping value and pivot key; a combination with no rows is
 * an empty frame, not `null`.
 *
 * - on a [Pivot] with nested keys (`then`): the key values nest in column groups
 * reflecting the key structure.
 *
 * - on a [Pivot] with `and` keys or an explicit `inward`: one column group per pivot key
 * with `and`; `inward = true` wraps a single key's values in a column group named after
 * the pivoted column; `inward = false` does not change the `then` nesting.
 */
class FramesTests : TestBase() {

    // `df` comes from `TestBase`: seven rows, five of them happy, and
    // "Alice", "Bob" and "Charlie" as the three first names.

    // the return type annotation asserts the contract of `PivotGroupBy.frames`: no original schema type
    private fun framesByFirstName(): DataFrame<*> = df.pivot { isHappy }.groupBy { name.firstName }.frames()

    @Test
    fun `pivot frames keeps each pivot group as a frame value`() {
        // the type annotation asserts the return type, which is part of the contract (no original schema type)
        val pivoted: DataRow<*> = df.pivot { isHappy }.frames()
        pivoted.columnNames() shouldBe listOf("true", "false")
        (pivoted["true"] as DataFrame<*>) shouldBe df.filter { isHappy }
        (pivoted["false"] as DataFrame<*>) shouldBe df.filter { !isHappy }
    }

    @Test
    fun `pivot frames written through the String API`() {
        val pivoted = df.pivot("isHappy").frames()
        pivoted.columnNames() shouldBe listOf("true", "false")
        (pivoted["true"] as DataFrame<*>) shouldBe df.filter { isHappy }
        (pivoted["false"] as DataFrame<*>) shouldBe df.filter { !isHappy }
    }

    @Test
    fun `pivot frames with nested pivot keys nests the key values in column groups`() {
        val pivoted = df.pivot { name.firstName then isHappy }.frames()
        // one top-level column group per value of the first key
        pivoted.columnNames() shouldBe listOf("Alice", "Bob", "Charlie")
        // inside each, one frame column per value of the second key present in that group
        pivoted.getColumnGroup("Alice").columnNames() shouldBe listOf("true", "false")
        pivoted.getColumnGroup("Bob").columnNames() shouldBe listOf("true")
        pivoted.getColumnGroup("Charlie").columnNames() shouldBe listOf("false", "true")
        // the nested frame columns hold the rows of the corresponding pivot group
        (pivoted["Bob"] as DataRow<*>)["true"] shouldBe df.filter { name.firstName == "Bob" && isHappy }
    }

    @Test
    fun `pivot frames with a single key and inward true wraps the key values in a column group`() {
        val pivoted = df.pivot(inward = true) { isHappy }.frames()
        pivoted.columnNames() shouldBe listOf("isHappy")
        val isHappyGroup = pivoted.df().getColumnGroup("isHappy")
        isHappyGroup.columnNames() shouldBe listOf("true", "false")
        isHappyGroup.columns().map { it.kind() } shouldBe listOf(ColumnKind.Frame, ColumnKind.Frame)
        // the group columns hold the frames of the corresponding pivot groups
        (pivoted["isHappy"] as DataRow<*>)["true"] shouldBe df.filter { isHappy }
    }

    @Test
    fun `pivot frames with several keys joined by and creates a column group per pivot key`() {
        val pivoted = df.pivot { isHappy and name.firstName }.frames()
        pivoted.columnNames() shouldBe listOf("isHappy", "name")
        val isHappyGroup = pivoted.df().getColumnGroup("isHappy")
        isHappyGroup.columnNames() shouldBe listOf("true", "false")
        isHappyGroup.columns().map { it.kind() } shouldBe listOf(ColumnKind.Frame, ColumnKind.Frame)
        (pivoted["isHappy"] as DataRow<*>)["true"] shouldBe df.filter { isHappy }
        val nameGroup = pivoted.df().getColumnGroup("name")
        nameGroup.columnNames() shouldBe listOf("firstName")
        // the `name.firstName` key path stays nested inside the `name` group
        val firstNameGroup = nameGroup.getColumnGroup("firstName")
        firstNameGroup.columnNames() shouldBe listOf("Alice", "Bob", "Charlie")
        firstNameGroup.columns().map { it.kind() } shouldBe
            listOf(ColumnKind.Frame, ColumnKind.Frame, ColumnKind.Frame)
    }

    @Test
    fun `pivot frames with nested pivot keys keeps the key value nesting with inward false`() {
        // `inward` does not change the nesting of `then` keys, so the shape is the same as with the default
        val pivoted = df.pivot(inward = false) { name.firstName then isHappy }.frames()
        pivoted.columnNames() shouldBe listOf("Alice", "Bob", "Charlie")
        pivoted.getColumnGroup("Alice").columnNames() shouldBe listOf("true", "false")
        (pivoted["Bob"] as DataRow<*>)["true"] shouldBe df.filter { name.firstName == "Bob" && isHappy }
    }

    @Test
    fun `pivot groupBy frames returns one row per grouping value with the pivot keys inside a column group`() {
        // the type annotation asserts the return type, which is part of the contract (no original schema type)
        val grouped: DataFrame<*> = framesByFirstName()
        grouped.rowsCount() shouldBe 3
        grouped.columnNames() shouldBe listOf("firstName", "isHappy")
        grouped["firstName"].values() shouldBe listOf("Alice", "Bob", "Charlie")
        val pivotKeys = grouped.getColumnGroup("isHappy")
        pivotKeys.columnNames() shouldBe listOf("true", "false")
        // the pivot keys hold the groups, so both columns inside the group are frame columns
        pivotKeys.columns().map { it.kind() } shouldBe listOf(ColumnKind.Frame, ColumnKind.Frame)
    }

    @Test
    fun `each frame in pivot groupBy frames holds the rows of its grouping value and pivot key`() {
        val grouped = framesByFirstName()
        // Alice has one happy row (Cooper) and one unhappy one (Wolf)
        (grouped[0]["isHappy"] as DataRow<*>)["true"] shouldBe df.filter { name.firstName == "Alice" && isHappy }
        (grouped[0]["isHappy"] as DataRow<*>)["false"] shouldBe df.filter { name.firstName == "Alice" && !isHappy }
        // Bob has two happy rows (Dylan, Marley) and no unhappy one
        (grouped[1]["isHappy"] as DataRow<*>)["true"] shouldBe df.filter { name.firstName == "Bob" && isHappy }
        // Charlie has one unhappy row (Daniels) and two happy ones (Chaplin, Byrd)
        (grouped[2]["isHappy"] as DataRow<*>)["true"] shouldBe df.filter { name.firstName == "Charlie" && isHappy }
        (grouped[2]["isHappy"] as DataRow<*>)["false"] shouldBe df.filter { name.firstName == "Charlie" && !isHappy }
    }

    @Test
    fun `a pivot key with no rows for a grouping value is an empty frame`() {
        val grouped = framesByFirstName()
        // Bob has no unhappy rows; the cell is a frame with no rows, not `null`
        val bobUnhappy = (grouped[1]["isHappy"] as DataRow<*>)["false"] as DataFrame<*>
        bobUnhappy.rowsCount() shouldBe 0
    }
}

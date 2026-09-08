package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test
import kotlin.reflect.typeOf

class MapTests {

    @Test
    fun `map frame column with empty frames`() {
        val frames by columnOf(dataFrameOf("a")(1), emptyDataFrame())
        frames.map { it.firstOrNull() }.size() shouldBe frames.size()
    }

    @Test
    fun `map ColumnsContainer`() {
        val df = dataFrameOf("a")(1, 2).add {
            expr { "a"<Int>() + 1 }.cumSum() into "b"
        }
        df["b"][1] shouldBe 5
    }

    @Test
    fun `ColumnGroup map`() {
        val group = dataFrameOf("x", "y")(1, 10, 2, 20, 3, 30).asColumnGroup("g")
        val sums = group.asDataFrame().map { row -> row["x"] as Int + row["y"] as Int }
        sums shouldBe listOf(11, 22, 33)
    }

    @Test
    fun `ColumnGroup asDataColumn map`() {
        val group = dataFrameOf("x", "y")(1, 10, 2, 20, 3, 30).asColumnGroup("g")
        val col: DataColumn<Int> = group.asDataColumn().map { it["x"] as Int + it["y"] as Int }
        col.name() shouldBe "g"
        col.toList() shouldBe listOf(11, 22, 33)
    }

    // region DataColumn.map / mapIndexed

    // "age" is not sorted and has a repeated value, so a lost or reordered value would be visible
    private val age by columnOf(15, 45, 20, 20)

    @Test
    fun `KDoc example - map keeps the name and the order of the values`() {
        val doubled = age.map { it * 2 }

        // the name of the new column is the name of the original one
        doubled.name() shouldBe "age"
        doubled.values() shouldBe listOf(30, 90, 40, 40)
    }

    @Test
    fun `map with the default infer drops the nullability when no null is computed`() {
        // R is Int?, but not a single computed value is null
        age.map<Int, Int?> { it }.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `map with the default infer keeps the nullability when a null is computed`() {
        age.map<Int, Int?> { if (it == 15) null else it }.type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `map with Infer None uses the type argument as it is`() {
        // no null is computed, yet the declared nullable type is kept: the values are not looked at
        age.map<Int, Int?>(Infer.None) { it }.type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `map with Infer Type takes the type from the computed values`() {
        // R is Any?, but the values are all Int, so the column type is Int
        age.map<Int, Any?>(Infer.Type) { it }.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `map returns a column group for rows and a frame column for dataframes`() {
        // a value of the new column is a DataRow, so the new column is a ColumnGroup
        val rows = age.map { dataFrameOf("doubled")(it * 2)[0] }
        rows.kind() shouldBe ColumnKind.Group
        rows.asColumnGroup()["doubled"].values() shouldBe listOf(30, 90, 40, 40)

        // a value of the new column is a DataFrame, so the new column is a FrameColumn
        val frames = age.map { dataFrameOf("doubled")(it * 2) }
        frames.kind() shouldBe ColumnKind.Frame

        // any other value gives an ordinary ValueColumn
        age.map { it * 2 }.kind() shouldBe ColumnKind.Value
    }

    @Test
    fun `map with an explicit type uses that type for the new column`() {
        // the receiver is built explicitly, so the call cannot resolve to another overload:
        // `typeOf<Number>()` as the first argument can only match the KType overload
        val asNumber: DataColumn<Int> = age.map(typeOf<Number>()) { it }

        // the given type is the type of the column, even though every value is an Int
        asNumber.type() shouldBe typeOf<Number>()

        // the values themselves are put into the column unchanged, without any conversion
        asNumber.values() shouldBe listOf(15, 45, 20, 20)
        asNumber.values().map { it::class } shouldBe List(4) { Int::class }
    }

    @Test
    fun `map with an explicit type and Infer Type uses that type as an upper bound only`() {
        // Number is only the upper bound here; the actual type comes from the values
        age.map(typeOf<Number>(), Infer.Type) { it }.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `KDoc example - mapIndexed numbers the values starting at zero`() {
        val names by columnOf("Alice", "Bob", "Charlie")

        val numbered = names.mapIndexed { i, name -> "${i + 1}. $name" }

        numbered.values() shouldBe listOf("1. Alice", "2. Bob", "3. Charlie")
    }

    @Test
    fun `mapIndexed with an explicit type uses that type for the new column`() {
        val indices: DataColumn<Int> = age.mapIndexed(typeOf<Number>()) { i, _ -> i }

        indices.type() shouldBe typeOf<Number>()
        // the index of the first value is 0
        indices.values() shouldBe listOf(0, 1, 2, 3)
    }

    // endregion

    // region DataFrame.map / mapToColumn / mapToFrame

    // the same data and the same calls as on the `map` page of the documentation website
    private val df = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie"),
        "lastName" to listOf("Cooper", "Dylan", "Daniels"),
        "age" to listOf(15, 45, 20),
        "city" to listOf("London", "Dubai", "Moscow"),
    ).group("firstName", "lastName").into("name")

    @Test
    fun `KDoc example - map over rows returns a list in row order`() {
        // the explicit type asserts that the result is a plain List, not a DataFrame or a DataColumn
        val yearsOfBirth: List<Int> = df.map { 2021 - "age"<Int>() }

        yearsOfBirth shouldBe listOf(2006, 1976, 2001)
    }

    @Test
    fun `the row expression of map gets the same row as receiver and as argument`() {
        // this is why `age` (through the receiver) and `it.age` mean the same thing inside the lambda
        df.map { it === this } shouldBe listOf(true, true, true)
    }

    @Test
    fun `map over the rows of an empty dataframe returns an empty list`() {
        df.filter { false }.map { 1 } shouldBe emptyList()
    }

    @Test
    fun `on a column group map goes over the rows while asDataColumn gives a column of rows`() {
        val group = df.getColumnGroup("name")

        // `asDataFrame` only widens the static type of the group; `map` is then DataFrame.map,
        // and the explicit type asserts that the result is a List with one element per row of the group
        val fullNames: List<String> = group.asDataFrame().map { "${it["firstName"]} ${it["lastName"]}" }
        fullNames shouldBe listOf("Alice Cooper", "Bob Dylan", "Charlie Daniels")

        // a ColumnGroup is not a DataColumn statically, so DataColumn.map needs `asDataColumn` first;
        // it gives a column of the rows of the group, with the same name and size as the group
        val rows: DataColumn<DataRow<*>> = group.asDataColumn().map { it }
        rows.name() shouldBe "name"
        rows.size() shouldBe 3
    }

    @Test
    fun `KDoc example - mapToColumn builds a standalone column and leaves the dataframe alone`() {
        val yearOfBirth = df.mapToColumn("year of birth") { 2021 - "age"<Int>() }

        yearOfBirth.name() shouldBe "year of birth"
        yearOfBirth.values() shouldBe listOf(2006, 1976, 2001)

        // the new column is not part of `df`
        df.columnNames() shouldBe listOf("name", "age", "city")
    }

    @Test
    fun `mapToColumn can read the values it has already computed`() {
        // `newValue()` of an AddExpression gives the value computed for a preceding row,
        // which is what makes running totals expressible
        val runningTotal = df.mapToColumn("total") {
            val previous = prev()?.newValue<Int>() ?: 0
            previous + "age"<Int>()
        }

        runningTotal.values() shouldBe listOf(15, 60, 80)
    }

    @Test
    fun `KDoc example - mapToFrame keeps only the described columns in the described order`() {
        val mapped = df.mapToFrame {
            "year of birth" from { 2021 - "age"<Int>() }
            expr { "age"<Int>() > 18 } into "is adult"
            "name"["lastName"]<String>().map { it.length } into "last name length"
            +"city"
        }

        // "name" and "age" of `df` are gone, "city" is there because of `+`,
        // and the order is the order of the description, not the order in `df`
        mapped.columnNames() shouldBe listOf("year of birth", "is adult", "last name length", "city")
        mapped["year of birth"].values() shouldBe listOf(2006, 1976, 2001)
        mapped["is adult"].values() shouldBe listOf(false, true, true)
        mapped["last name length"].values() shouldBe listOf(6, 5, 7)
        mapped["city"].values() shouldBe listOf("London", "Dubai", "Moscow")

        // `add` would have kept the original columns as well; `mapToFrame` does not
        df.add { "is adult" from { "age"<Int>() > 18 } }.columnNames() shouldBe
            listOf("name", "age", "city", "is adult")
    }

    @Test
    fun `mapToFrame with an empty body gives a frame with no columns and no rows`() {
        val empty = df.mapToFrame { }

        // no column is described, so nothing carries the row count of `df` over
        empty.columnsCount() shouldBe 0
        empty.rowsCount() shouldBe 0
    }

    @Test
    fun `mapToFrame can describe a column group`() {
        val mapped = df.mapToFrame {
            group("details") {
                "last name length" from { "name"["lastName"]<String>().length }
            }
        }

        mapped.columnNames() shouldBe listOf("details")
        mapped.getColumnGroup("details")["last name length"].values() shouldBe listOf(6, 5, 7)
    }

    // endregion

    // region GroupBy.map / mapToRows / mapToFrames

    // "a" occurs twice and is not the alphabetically last key, so both the group order
    // and the group contents are visible in the assertions
    private val grouped = dataFrameOf(
        "k" to listOf("b", "a", "b"),
        "v" to listOf(1, 2, 3),
    ).groupBy("k")

    @Test
    fun `KDoc example - GroupBy map returns one element per key-group pair in group order`() {
        // the explicit type asserts that the result is a plain List
        val sizes: List<Int> = grouped.map { it.group.rowsCount() }

        // "b" comes first because its first row comes first in the original dataframe
        sizes shouldBe listOf(2, 1)

        // the key is available as a DataRow, the group as a DataFrame
        grouped.map { it.key["k"] as String to it.group["v"].values() } shouldBe
            listOf("b" to listOf(1, 3), "a" to listOf(2))
    }

    @Test
    fun `GroupBy map leaves out the null results`() {
        // there are two key-group pairs, but only one of them produces an element
        grouped.map { if (it.key["k"] == "b") null else 1 } shouldBe listOf(1)
    }

    @Test
    fun `KDoc example - mapToRows returns one row per key-group pair`() {
        val oldest = grouped.mapToRows { it.group.sortByDesc("v").firstOrNull() }

        // one row per group, in group order; the columns are the columns of the returned rows
        oldest.columnNames() shouldBe listOf("k", "v")
        oldest shouldBe dataFrameOf("k", "v")("b", 3, "a", 2)
    }

    @Test
    fun `mapToRows skips the pairs for which null is returned`() {
        // only the group of "b" has more than one row, so only it gives a row
        val rows = grouped.mapToRows { if (it.group.rowsCount() > 1) it.group[0] else null }

        rows shouldBe dataFrameOf("k", "v")("b", 1)
    }

    @Test
    fun `mapToRows loses the columns too when every pair returns null`() {
        val rows = grouped.mapToRows { null }

        // the columns of the result are the columns of the returned rows,
        // and there are no returned rows here — so the schema is gone, without an exception
        rows.rowsCount() shouldBe 0
        rows.columnNames() shouldBe emptyList()
    }

    @Test
    fun `KDoc example - mapToFrames returns a frame column named like the groups`() {
        val firstRows: FrameColumn<*> = grouped.mapToFrames { it.group.take(1) }

        // the name of the new column is the name of GroupBy.groups
        firstRows.name() shouldBe grouped.groups.name()
        firstRows.name() shouldBe "group"

        // one dataframe per key-group pair, in group order
        firstRows.size() shouldBe 2
        firstRows.toList().map { it["v"].values() } shouldBe listOf(listOf(1), listOf(2))

        // `concat()` puts those dataframes back together into one dataframe
        firstRows.concat() shouldBe dataFrameOf("k", "v")("b", 1, "a", 2)
    }

    @Test
    fun `concat of the whole groups restores every row, regrouped`() {
        // the frame column carries the whole groups, so concatenating it gives all the rows back,
        // in group order: the rows of "b" first, then the rows of "a"
        val all: DataFrame<*> = grouped.mapToFrames { it.group }.concat()

        all shouldBe dataFrameOf("k", "v")("b", 1, "b", 3, "a", 2)
    }

    // endregion
}

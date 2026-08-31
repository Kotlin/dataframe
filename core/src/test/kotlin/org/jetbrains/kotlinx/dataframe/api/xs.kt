package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import kotlin.reflect.typeOf

class XsTests {

    private interface Person

    // Compact data for the contract tests. The KDoc examples use `sample` at the bottom instead.
    // "Charlie" appears twice, and the row with the greater age comes first,
    // so a lost row order would be visible in the assertions.
    private val people = dataFrameOf(
        "name" to listOf("Alice", "Charlie", "Bob", "Charlie"),
        "age" to listOf(15, 30, 20, 20),
        "city" to listOf("London", "Moscow", "Tokyo", "Paris"),
    )

    // typed receiver: it lets the assertions below check the schema marker of the result
    private val df: DataFrame<Person> = people.cast()

    @Test
    fun `default key columns are the first columns and are removed`() {
        // the explicit type asserts that the operation returns a DataFrame with the same schema marker
        val res: DataFrame<Person> = df.xs("Charlie")

        // "name" is the only key column here, so only it is removed;
        // both matching rows are kept in their original order
        res shouldBe dataFrameOf("age", "city")(30, "Moscow", 20, "Paris")
    }

    @Test
    fun `key values are paired with key columns by position`() {
        // "Charlie" goes to "name", 20 goes to "age"
        df.xs("Charlie", 20) shouldBe dataFrameOf("city")("Paris")

        // the same values in the other order are compared with the same two columns,
        // so nothing matches
        df.xs(20, "Charlie").rowsCount() shouldBe 0
    }

    @Test
    fun `a column group is not counted as a key column but its columns are`() {
        // columns: "name", "info" { "age", "city" }
        val nested = df.group("age", "city").into("info")

        val res = nested.xs("Charlie", 30)

        // the second key value was matched against "info"/"age", not against the "info" group itself
        res.rowsCount() shouldBe 1
        res.columnNames() shouldBe listOf("info")
        res.getColumnGroup("info").columnNames() shouldBe listOf("city")
        res.getColumnGroup("info")["city"].values() shouldBe listOf("Moscow")
    }

    @Test
    fun `explicit key columns are paired in selection order`() {
        // "Paris" goes to "city" and "Charlie" goes to "name" — the selection order,
        // which is the reverse of the column order in the dataframe
        val res: DataFrame<Person> = df.xs("Paris", "Charlie") { "city"<String>() and "name"<String>() }

        res shouldBe dataFrameOf("age")(20)
    }

    @Test
    fun `number of key values must be equal to the number of key columns`() {
        // 4 key values, but the dataframe has only 3 columns to take as key columns
        shouldThrow<IllegalArgumentException> {
            df.xs("Charlie", 20, "Paris", 1)
        }
        // 1 key value, 2 selected key columns
        shouldThrow<IllegalArgumentException> {
            df.xs(20) { "name"<Any?>() and "age"<Any?>() }
        }
        // 2 key values, 1 selected key column
        shouldThrow<IllegalArgumentException> {
            df.groupBy("name").xs("Charlie", 20) { "name"<Any?>() }
        }
        // 2 key values, but the keys have only 1 column.
        // The message is asserted here because the frame column that holds the groups is part of
        // the receiver the default selector is resolved against: when it is counted as a key column,
        // the size check passes and the operation fails later with "Column not found" instead.
        shouldThrow<IllegalArgumentException> {
            df.groupBy("city").xs("Moscow", "Charlie")
        }.message shouldBe "Number of key values 2 doesn't equal to number of key columns 1"
    }

    @Test
    fun `groupBy xs filters keys and groups and removes the key columns from both`() {
        // the explicit type asserts that the operation returns a GroupBy, not a DataFrame
        val res: GroupBy<Person, Person> = df.groupBy("name", "city").xs("Charlie")

        // "name" is removed from the keys, and only the "Charlie" key-group pairs are kept
        res.keys shouldBe dataFrameOf("city")("Moscow", "Paris")

        // "name" is removed from the groups as well
        res.concat() shouldBe dataFrameOf("age", "city")(30, "Moscow", 20, "Paris")
    }

    @Test
    fun `groupBy xs default key columns are the first key columns`() {
        // grouped by "city" first, so the single key value is matched against "city"
        val res = df.groupBy("city", "name").xs("Moscow")

        res.keys shouldBe dataFrameOf("name")("Charlie")
    }

    @Test
    fun `groupBy xs explicit key columns are paired in selection order`() {
        // "Charlie" goes to "name" and "Moscow" goes to "city" — the selection order,
        // which is the reverse of the order of the grouping keys
        val res = df.groupBy("city", "name").xs("Charlie", "Moscow") { "name"<String>() and "city"<String>() }

        // both key columns are removed from the keys, so nothing is left there
        res.keys.columnsCount() shouldBe 0

        // if the values were paired with the key columns in frame order instead,
        // "Charlie" would be compared with "city" and no row would match
        res.concat() shouldBe dataFrameOf("age")(30)
    }

    @Test
    fun `groupBy xs key column present only in the keys`() {
        // "name" is dropped from every group, so it is left only in the keys
        val grouped = df.groupBy("name", "city").updateGroups { it.remove("name") }

        val res = grouped.xs("Charlie") { "name"<String>() }

        // the keys are filtered by "name" and "name" is removed from them
        res.keys shouldBe dataFrameOf("city")("Moscow", "Paris")

        // the groups have no "name" column, so they are left untouched
        res.concat() shouldBe dataFrameOf("age", "city")(30, "Moscow", 20, "Paris")
    }

    @Test
    fun `groupBy xs key column present only in the groups`() {
        // "name" is not a grouping key, so it exists only inside the groups
        val res = df.groupBy("city").xs("Charlie") { "name"<String>() }

        // the keys are left untouched: all four key-group pairs are still there
        res.keys shouldBe dataFrameOf("city")("London", "Moscow", "Tokyo", "Paris")

        // inside the groups the rows are filtered and "name" is removed,
        // so the "London" and "Tokyo" groups contribute nothing here
        res.concat() shouldBe dataFrameOf("age", "city")(30, "Moscow", 20, "Paris")
    }

    // region KDoc examples
    //
    // `sample` is the input table of the `xs` KDoc examples, and every result table there is
    // an expected value below. This is the same data as `samples/api/TestBase.kt`, so the KDoc,
    // the `xs` page on the documentation website and these tests all show one example.
    // Changing the data or a call here means changing the KDoc tables too.

    private val sample = dataFrameOf(
        "firstName" to listOf("Alice", "Bob", "Charlie", "Charlie", "Bob", "Alice", "Charlie"),
        "lastName" to listOf("Cooper", "Dylan", "Daniels", "Chaplin", "Marley", "Wolf", "Byrd"),
        "age" to listOf(15, 45, 20, 40, 30, 20, 30),
        "city" to listOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null, "Moscow"),
        "weight" to listOf(54, 87, null, null, 68, 55, 90),
        "isHappy" to listOf(true, true, false, true, true, false, true),
    ).group("firstName", "lastName").into("name")

    @Test
    fun `KDoc example - DataFrame xs with default key columns`() {
        // only "name"/"firstName" is a key column here, so the "name" group survives
        // with "lastName" still inside it
        sample.xs("Charlie") shouldBe
            dataFrameOf(
                "lastName" to listOf("Daniels", "Chaplin", "Byrd"),
                "age" to listOf(20, 40, 30),
                "city" to listOf("Moscow", "Milan", "Moscow"),
                "weight" to listOf(null, null, 90),
                "isHappy" to listOf(false, true, true),
            ).group("lastName").into("name")

        // both columns of the "name" group are removed, so the group itself is gone.
        // "weight" needs an explicit type: inference over a list of only nulls gives
        // `Nothing?`, while the real result keeps the original `Int?`
        sample.xs("Charlie", "Chaplin") shouldBe
            dataFrameOf(
                columnOf(40) named "age",
                columnOf("Milan") named "city",
                DataColumn.createValueColumn("weight", listOf(null), typeOf<Int?>()),
                columnOf(true) named "isHappy",
            )
    }

    @Test
    fun `KDoc example - DataFrame xs with selected key columns`() {
        sample.xs("Moscow", true) { "city"<String?>() and "isHappy"<Boolean>() } shouldBe
            dataFrameOf(
                "firstName" to listOf("Charlie"),
                "lastName" to listOf("Byrd"),
                "age" to listOf(30),
                "weight" to listOf(90),
            ).group("firstName", "lastName").into("name")
    }

    @Test
    fun `KDoc example - GroupBy xs with default key columns`() {
        val gb = sample.groupBy("city", "isHappy").xs("Moscow")

        gb.keys shouldBe dataFrameOf("isHappy" to listOf(false, true))

        gb.concat() shouldBe
            dataFrameOf(
                "firstName" to listOf("Charlie", "Charlie"),
                "lastName" to listOf("Daniels", "Byrd"),
                "age" to listOf(20, 30),
                "weight" to listOf(null, 90),
                "isHappy" to listOf(false, true),
            ).group("firstName", "lastName").into("name")
    }

    @Test
    fun `KDoc example - GroupBy xs with selected key columns`() {
        val gb = sample.groupBy("city").xs(true) { "isHappy"<Boolean>() }

        gb.keys shouldBe
            dataFrameOf("city" to listOf("London", "Dubai", "Moscow", "Milan", "Tokyo", null))

        gb.concat() shouldBe
            dataFrameOf(
                "firstName" to listOf("Alice", "Bob", "Charlie", "Charlie", "Bob"),
                "lastName" to listOf("Cooper", "Dylan", "Byrd", "Chaplin", "Marley"),
                "age" to listOf(15, 45, 30, 40, 30),
                "city" to listOf("London", "Dubai", "Moscow", "Milan", "Tokyo"),
                "weight" to listOf(54, 87, 90, null, 68),
            ).group("firstName", "lastName").into("name")
    }

    // endregion
}

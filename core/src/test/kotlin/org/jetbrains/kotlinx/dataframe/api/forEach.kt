package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

/**
 * Tests the behavior of the `forEach` API:
 *
 * - on a [DataColumn]: `forEach` and `forEachIndexed` go over the values in order,
 * and `forEachIndexed` gives positions starting at `0`.
 *
 * - on a [DataFrame]: the rows in order, each given both as the receiver and as the argument.
 *
 * - on a [GroupBy]: the key–group pairs in order, given as a destructurable [GroupBy.Entry].
 *
 * The callbacks collect into lists instead of printing, so the KDoc examples can be asserted.
 */
class ForEachTests : TestBase() {

    // `df` comes from `TestBase` and has seven rows;
    // "Moscow" is the city in rows 2 and 6, the city in row 5 is `null`.

    // region DataColumn

    @Test
    fun `KDoc example - forEach on a column goes over the values from the first to the last`() {
        val ages: DataColumn<Int> = df.age
        val seen = mutableListOf<Int>()
        ages.forEach { seen += it }
        seen shouldBe listOf(15, 45, 20, 40, 30, 20, 30)
    }

    @Test
    fun `KDoc example - forEachIndexed on a column numbers the values`() {
        val seen = mutableListOf<String>()
        df.name.firstName.forEachIndexed { i, firstName -> seen += "${i + 1}. $firstName" }
        seen shouldBe listOf(
            "1. Alice",
            "2. Bob",
            "3. Charlie",
            "4. Charlie",
            "5. Bob",
            "6. Alice",
            "7. Charlie",
        )
    }

    @Test
    fun `forEachIndexed gives positions starting at 0`() {
        val positions = mutableListOf<Int>()
        df.age.forEachIndexed { i, _ -> positions += i }
        positions shouldBe listOf(0, 1, 2, 3, 4, 5, 6)
    }

    // endregion

    // region DataFrame

    @Test
    fun `KDoc example - forEach on a dataframe goes over the rows from the first to the last`() {
        val frame: DataFrame<TestBase.Person> = df
        val seen = mutableListOf<Int>()
        frame.forEach { seen += it.age }
        seen shouldBe listOf(15, 45, 20, 40, 30, 20, 30)
    }

    @Test
    fun `forEach on a dataframe gives the row both as the receiver and as the argument`() {
        val seen = mutableListOf<Pair<Int, Int>>()
        // `age` is read from the receiver, `it.age` from the argument
        df.forEach { seen += age to it.age }
        seen shouldBe listOf(15 to 15, 45 to 45, 20 to 20, 40 to 40, 30 to 30, 20 to 20, 30 to 30)
    }

    // endregion

    // region GroupBy

    @Test
    fun `KDoc example - forEach on a GroupBy goes over the key-group pairs in their order`() {
        val seen = mutableListOf<String>()
        df.groupBy { city }.forEach { (key, group) -> seen += "${key.city}: ${group.rowsCount()}" }
        seen shouldBe listOf("London: 1", "Dubai: 1", "Moscow: 2", "Milan: 1", "Tokyo: 1", "null: 1")
    }

    @Test
    fun `forEach on a GroupBy gives the key values and the rows of each group`() {
        val keys = mutableListOf<List<Any?>>()
        val groups = mutableListOf<DataFrame<*>>()
        df.groupBy { city }.forEach {
            keys += it.key.values()
            groups += it.group
        }
        keys shouldBe listOf(
            listOf("London"),
            listOf("Dubai"),
            listOf("Moscow"),
            listOf("Milan"),
            listOf("Tokyo"),
            listOf(null),
        )
        // the "Moscow" group holds rows 2 and 6 of `df`
        groups[2] shouldBe df[2, 6]
    }

    // endregion
}

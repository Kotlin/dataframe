package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataRow
import org.junit.Test

/**
 * Tests the behavior of [associate] and [associateBy]:
 * the shape of the resulting [Map], how duplicate keys are resolved,
 * what happens on a dataframe without rows, how `null` keys and values are treated,
 * and how a failing row expression propagates.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class AssociateTests {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Charlie"),
        "age" to columnOf(15, 45, 20),
    )

    /** Two rows share the name `Alice`: the first one of the two comes before `Bob`, the second one after. */
    private val dfWithDuplicates = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Alice", "Dave"),
        "age" to columnOf(15, 45, 20, 50),
    )

    private val dfWithNulls = dataFrameOf(
        "name" to columnOf("Alice", null, "Charlie"),
        "age" to columnOf(15, 45, null),
    )

    // region associate

    @Test
    fun `associate builds a map from the pair returned for each row`() {
        // the declared type is part of the contract: the pair's components become the key and the value types
        val map: Map<String, Int> = df.associate { "name"<String>() to "age"<Int>() }

        map shouldBe mapOf("Alice" to 15, "Bob" to 45, "Charlie" to 20)
    }

    @Test
    fun `associate computes keys and values from several columns`() {
        val map = df.associate { ("${"name"<String>()} (${"age"<Int>()})") to "age"<Int>() + 1 }

        map shouldBe mapOf("Alice (15)" to 16, "Bob (45)" to 46, "Charlie (20)" to 21)
    }

    @Test
    fun `associate keeps the last value for a duplicate key`() {
        val map = dfWithDuplicates.associate { "name"<String>() to "age"<Int>() }

        // the first Alice (15) is overwritten by the last one (20)
        map shouldBe mapOf("Alice" to 20, "Bob" to 45, "Dave" to 50)
    }

    @Test
    fun `associate returns the keys in the order of the rows`() {
        val map = dfWithDuplicates.associate { "name"<String>() to "age"<Int>() }

        // Alice sits where its first row is, before Bob, and carries the value of its last row
        map.entries.map { it.key to it.value } shouldBe listOf("Alice" to 20, "Bob" to 45, "Dave" to 50)
    }

    @Test
    fun `associate on a dataframe without rows returns an empty map`() {
        val noRows = df.filter { "age"<Int>() > 100 }

        noRows.associate { "name"<String>() to "age"<Int>() } shouldBe emptyMap()
    }

    @Test
    fun `associate accepts null keys and null values`() {
        val map = dfWithNulls.associate { "name"<String?>() to "age"<Int?>() }

        map shouldBe mapOf("Alice" to 15, null to 45, "Charlie" to null)
    }

    @Test
    fun `associate reports a missing column the same way as a row access`() {
        val exception = shouldThrow<IllegalArgumentException> {
            df.associate { "nope"<String>() to "age"<Int>() }
        }

        exception.message shouldBe "Column not found: 'nope'"
    }

    // endregion

    // region associateBy

    @Test
    fun `associateBy maps the key of each row to that row`() {
        // the declared type is part of the contract: the value is the row itself, not a cell of it
        val map: Map<String, DataRow<*>> = df.associateBy { "name"<String>() }

        map shouldBe mapOf("Alice" to df[0], "Bob" to df[1], "Charlie" to df[2])
    }

    @Test
    fun `associateBy computes keys from several columns`() {
        val map = df.associateBy { "${"name"<String>()} (${"age"<Int>()})" }

        map.keys shouldBe setOf("Alice (15)", "Bob (45)", "Charlie (20)")
        map["Bob (45)"] shouldBe df[1]
    }

    @Test
    fun `associateBy keeps the last row for a duplicate key`() {
        val map = dfWithDuplicates.associateBy { "name"<String>() }

        // the row of the first Alice (15) is overwritten by the row of the last one (20)
        map shouldBe mapOf(
            "Alice" to dfWithDuplicates[2],
            "Bob" to dfWithDuplicates[1],
            "Dave" to dfWithDuplicates[3],
        )
    }

    @Test
    fun `associateBy returns the keys in the order of the rows`() {
        val map = dfWithDuplicates.associateBy { "name"<String>() }

        // Alice sits where its first row is, before Bob, and carries the row of its last occurrence
        map.keys.toList() shouldBe listOf("Alice", "Bob", "Dave")
        map["Alice"] shouldBe dfWithDuplicates[2]
    }

    @Test
    fun `associateBy on a dataframe without rows returns an empty map`() {
        val noRows = df.filter { "age"<Int>() > 100 }

        noRows.associateBy { "name"<String>() } shouldBe emptyMap()
    }

    @Test
    fun `associateBy accepts null keys`() {
        val map = dfWithNulls.associateBy { "name"<String?>() }

        map shouldBe mapOf("Alice" to dfWithNulls[0], null to dfWithNulls[1], "Charlie" to dfWithNulls[2])
    }

    @Test
    fun `associateBy reports a missing column the same way as a row access`() {
        val exception = shouldThrow<IllegalArgumentException> {
            df.associateBy { "nope"<String>() }
        }

        exception.message shouldBe "Column not found: 'nope'"
    }

    // endregion
}

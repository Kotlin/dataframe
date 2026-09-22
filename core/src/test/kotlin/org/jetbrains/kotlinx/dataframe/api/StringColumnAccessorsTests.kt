package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.Test

/**
 * Tests what a `String` column accessor does with the type it is given in a row expression,
 * as described on the `String API` documentation page.
 */
class StringColumnAccessorsTests {

    private val df = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Charlie"),
        "age" to columnOf(15, 45, 20),
    )

    @Test
    fun `a String column accessor does not verify the type against the column`() {
        // "age" holds Int, the accessor is asked for String, and the Int values come through unchanged
        val map: Map<String, Int> = df.associate { "age"<String>() to 1 }

        map shouldBe mapOf(15 to 1, 45 to 1, 20 to 1)
    }

    @Test
    fun `a wrong accessor type fails with a ClassCastException where the value is used`() {
        shouldThrow<ClassCastException> {
            // the cast happens here, on isNotEmpty, not inside the accessor
            df.filter { "age"<String>().isNotEmpty() }
        }
    }

    @Test
    fun `a wrong accessor type is not reported at all in a column selection`() {
        // "age" holds Int, the accessor is asked for String, and the Int column comes back as it is
        df.select { col<String>("age") } shouldBe df.select { "age"() }
        df.select { valueCol<String>("age") } shouldBe df.select { "age"() }
    }

    @Test
    fun `a null value passes through a non-null accessor type`() {
        val withNulls = dataFrameOf("weight" to columnOf(null, 68))

        withNulls.associate { getValue<Int>("weight") to 1 } shouldBe mapOf(null to 1, 68 to 1)
    }

    @Test
    fun `a matching accessor type reads the column`() {
        df.associate { "name"<String>() to "age"<Int>() } shouldBe
            mapOf("Alice" to 15, "Bob" to 45, "Charlie" to 20)
    }
}

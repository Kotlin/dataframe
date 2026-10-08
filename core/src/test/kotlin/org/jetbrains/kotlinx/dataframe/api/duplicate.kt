package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class DuplicateTests {

    private val df = dataFrameOf("name", "age")(
        "Alice", 15,
        "Bob", 20,
        "Charlie", 30,
    )

    // region duplicateRows

    @Test
    fun `KDoc example - duplicateRows repeats every row n times, each copy next to its original`() {
        df.duplicateRows(3) shouldBe dataFrameOf("name", "age")(
            "Alice", 15,
            "Alice", 15,
            "Alice", 15,
            "Bob", 20,
            "Bob", 20,
            "Bob", 20,
            "Charlie", 30,
            "Charlie", 30,
            "Charlie", 30,
        )
    }

    @Test
    fun `KDoc example - duplicateRows with a condition repeats only matching rows and keeps the other rows once`() {
        df.duplicateRows(3) { "age"<Int>() > 18 } shouldBe dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
            "Bob", 20,
            "Bob", 20,
            "Charlie", 30,
            "Charlie", 30,
            "Charlie", 30,
        )
    }

    @Test
    fun `duplicateRows repeats column groups and frame columns together with their rows`() {
        val one = dataFrameOf("x")(1)
        val two = dataFrameOf("x")(2, 3)
        val nested = dataFrameOf("name", "age")("Alice", 15, "Bob", 20)
            .group("name", "age").into("person")
            .add(columnOf(one, two).rename("frames"))

        nested.duplicateRows(2) shouldBe dataFrameOf("name", "age")("Alice", 15, "Alice", 15, "Bob", 20, "Bob", 20)
            .group("name", "age").into("person")
            .add(columnOf(one, one, two, two).rename("frames"))

        nested.duplicateRows(2) { "person"["name"]<String>() == "Bob" } shouldBe
            dataFrameOf("name", "age")("Alice", 15, "Bob", 20, "Bob", 20)
                .group("name", "age").into("person")
                .add(columnOf(one, two, two).rename("frames"))
    }

    @Test
    fun `duplicateRows keeps column names and types, nullable ones included`() {
        val nullable = dataFrameOf("a", "b")(
            1, null,
            null, "x",
        )

        val duplicated = nullable.duplicateRows(2)

        duplicated.columnNames() shouldBe listOf("a", "b")
        duplicated["a"].type() shouldBe typeOf<Int?>()
        duplicated["b"].type() shouldBe typeOf<String?>()
        // the overload with a condition keeps the types too
        nullable.duplicateRows(2) { index() == 0 }["a"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `duplicateRows rejects n that is not positive`() {
        shouldThrow<IllegalArgumentException> { df.duplicateRows(0) }.message shouldBe
            "Number of duplicates must be greater than 0, but was 0"
        shouldThrow<IllegalArgumentException> { df.duplicateRows(-1) }.message shouldBe
            "Number of duplicates must be greater than 0, but was -1"
        // a dataframe without columns has no column to check n against
        shouldThrow<IllegalArgumentException> { DataFrame.empty(3).duplicateRows(0) }.message shouldBe
            "Number of duplicates must be greater than 0, but was 0"
    }

    @Test
    fun `duplicateRows with a condition rejects n that is not positive instead of removing matching rows`() {
        shouldThrow<IllegalArgumentException> { df.duplicateRows(0) { "age"<Int>() > 18 } }.message shouldBe
            "Number of duplicates must be greater than 0, but was 0"
        shouldThrow<IllegalArgumentException> { df.duplicateRows(-1) { "age"<Int>() > 18 } }.message shouldBe
            "Number of duplicates must be greater than 0, but was -1"
    }

    @Test
    fun `n = 1 keeps one copy of everything, the original included`() {
        df.duplicateRows(1) shouldBe df
        df.duplicateRows(1) { "age"<Int>() > 18 } shouldBe df
        df[1].duplicate(1) shouldBe dataFrameOf("name", "age")("Bob", 20)
        df.duplicate(1).values() shouldBe listOf(df)
    }

    // endregion

    // region DataRow.duplicate

    @Test
    fun `KDoc example - DataRow duplicate returns a dataframe with the row repeated n times`() {
        df[1].duplicate(3) shouldBe dataFrameOf("name", "age")(
            "Bob", 20,
            "Bob", 20,
            "Bob", 20,
        )
    }

    @Test
    fun `DataRow duplicate repeats values of column groups and frame columns`() {
        val one = dataFrameOf("x")(1)
        val two = dataFrameOf("x")(2, 3)
        val nested = dataFrameOf("name", "age")("Alice", 15, "Bob", 20)
            .group("name", "age").into("person")
            .add(columnOf(one, two).rename("frames"))

        nested[1].duplicate(2) shouldBe dataFrameOf("name", "age")("Bob", 20, "Bob", 20)
            .group("name", "age").into("person")
            .add(columnOf(two, two).rename("frames"))
    }

    @Test
    fun `DataRow duplicate makes a nullable column non-nullable when the value in the row is not null`() {
        val nullable = dataFrameOf("a", "b")(
            1, null,
            null, "x",
        )
        nullable["a"].type() shouldBe typeOf<Int?>()
        nullable["b"].type() shouldBe typeOf<String?>()

        val duplicated = nullable[0].duplicate(2)

        duplicated["a"].type() shouldBe typeOf<Int>()
        duplicated["b"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `DataRow duplicate rejects n that is not positive`() {
        shouldThrow<IllegalArgumentException> { df[1].duplicate(0) }.message shouldBe
            "Number of duplicates must be greater than 0, but was 0"
        shouldThrow<IllegalArgumentException> { df[1].duplicate(-1) }.message shouldBe
            "Number of duplicates must be greater than 0, but was -1"
    }

    // endregion

    // region DataFrame.duplicate

    @Test
    fun `KDoc example - DataFrame duplicate returns an unnamed frame column with n copies of the dataframe`() {
        val copies: FrameColumn<*> = df.duplicate(3)

        copies.name() shouldBe ""
        copies.values() shouldBe listOf(df, df, df)
    }

    @Test
    fun `KDoc example - DataFrame duplicate with concat has all rows followed by all rows again`() {
        df.duplicate(2).concat() shouldBe dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", 20,
            "Charlie", 30,
            "Alice", 15,
            "Bob", 20,
            "Charlie", 30,
        )
    }

    @Test
    fun `DataFrame duplicate rejects n that is not positive`() {
        shouldThrow<IllegalArgumentException> { df.duplicate(0) }.message shouldBe
            "Number of duplicates must be greater than 0, but was 0"
        shouldThrow<IllegalArgumentException> { df.duplicate(-1) }.message shouldBe
            "Number of duplicates must be greater than 0, but was -1"
    }

    // endregion
}

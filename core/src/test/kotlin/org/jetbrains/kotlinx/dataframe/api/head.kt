package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import kotlin.reflect.typeOf

/**
 * Tests the behavior of the `head` API on a [DataFrame]:
 * the first five rows by default, the first `numRows` rows when given,
 * the edge cases of `numRows` (equal to and more than the number of rows, zero, negative),
 * the nullability of the columns that are left, and the nested columns kept at zero rows.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class HeadTests {

    // seven rows, so the default of five takes a strict prefix
    private val df = dataFrameOf("n", "letter")(
        1, "a",
        2, "b",
        3, "c",
        4, "d",
        5, "e",
        6, "f",
        7, "g",
    )

    @Test
    fun `head without arguments returns the first five rows`() {
        df.head() shouldBe dataFrameOf("n", "letter")(
            1, "a",
            2, "b",
            3, "c",
            4, "d",
            5, "e",
        )
    }

    @Test
    fun `head returns the first numRows rows`() {
        df.head(3) shouldBe dataFrameOf("n", "letter")(
            1, "a",
            2, "b",
            3, "c",
        )
    }

    @Test
    fun `head with numRows greater than the number of rows returns the whole dataframe`() {
        df.head(10) shouldBe df
    }

    @Test
    fun `head with numRows equal to the number of rows returns the whole dataframe`() {
        df.head(7) shouldBe df
    }

    @Test
    fun `head without arguments on a dataframe with fewer than five rows returns the whole dataframe`() {
        val small = dataFrameOf("n", "letter")(
            1, "a",
            2, "b",
        )
        small.head() shouldBe small
    }

    @Test
    fun `head with zero rows returns an empty dataframe with the same columns`() {
        val empty = df.head(0)

        empty.rowsCount() shouldBe 0
        empty.columnNames() shouldBe listOf("n", "letter")
        empty.columnTypes() shouldBe listOf(typeOf<Int>(), typeOf<String>())
    }

    @Test
    fun `head with zero rows keeps the nested columns of column groups and frame columns`() {
        // `keys` is a column group, `group` is a frame column whose dataframes have the columns `k` and `v`
        val nested = dataFrameOf("k", "v")(1, "x", 2, "y").groupBy("k").toDataFrame().group("k").into("keys")

        val empty = nested.head(0)

        empty.getFrameColumn("group").schema.value.columns.keys shouldBe setOf("k", "v")
        empty.schema() shouldBe nested.schema()
    }

    @Test
    fun `nullable column becomes non-nullable when its first numRows values have no null`() {
        val nullable = dataFrameOf("w")(1, 2, null, 4)
        nullable["w"].type() shouldBe typeOf<Int?>()

        nullable.head(2)["w"].type() shouldBe typeOf<Int>()
        nullable.head(3)["w"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `head with numRows covering all rows keeps the column types as they are`() {
        // declared nullable, but holds no `null`
        val declared = dataFrameOf(DataColumn.createValueColumn("w", listOf(1, 2), typeOf<Int?>()))

        declared.head()["w"].type() shouldBe typeOf<Int?>()
        declared.head(2)["w"].type() shouldBe typeOf<Int?>()
        // with a row left out, the nullability follows the values
        declared.head(1)["w"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `head with negative numRows throws`() {
        shouldThrow<IllegalArgumentException> {
            df.head(-1)
        }.message shouldBe "Requested rows count -1 is less than zero."
    }
}

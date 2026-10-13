package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test
import kotlin.reflect.typeOf

/**
 * Tests the behavior of the `tail` API on a [DataFrame]:
 * the last five rows by default, the last `numRows` rows when given,
 * the edge cases of `numRows` (equal to and more than the number of rows, zero, negative),
 * the nullability of the columns that are left, and the nested columns kept at zero rows.
 */
@Suppress("ktlint:standard:argument-list-wrapping")
class TailTests {

    // seven rows, so the default of five takes a strict suffix
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
    fun `tail without arguments returns the last five rows`() {
        df.tail() shouldBe dataFrameOf("n", "letter")(
            3, "c",
            4, "d",
            5, "e",
            6, "f",
            7, "g",
        )
    }

    @Test
    fun `tail returns the last numRows rows`() {
        df.tail(2) shouldBe dataFrameOf("n", "letter")(
            6, "f",
            7, "g",
        )
    }

    @Test
    fun `tail with numRows greater than the number of rows returns the whole dataframe`() {
        df.tail(10) shouldBe df
    }

    @Test
    fun `tail with numRows equal to the number of rows returns the whole dataframe`() {
        df.tail(7) shouldBe df
    }

    @Test
    fun `tail without arguments on a dataframe with fewer than five rows returns the whole dataframe`() {
        val small = dataFrameOf("n", "letter")(
            1, "a",
            2, "b",
        )
        small.tail() shouldBe small
    }

    @Test
    fun `tail with zero rows returns an empty dataframe with the same columns`() {
        val empty = df.tail(0)

        empty.rowsCount() shouldBe 0
        empty.columnNames() shouldBe listOf("n", "letter")
        empty.columnTypes() shouldBe listOf(typeOf<Int>(), typeOf<String>())
    }

    @Test
    fun `tail with zero rows keeps the nested columns of column groups and frame columns`() {
        // `keys` is a column group, `group` is a frame column whose dataframes have the columns `k` and `v`
        val nested = dataFrameOf("k", "v")(1, "x", 2, "y").groupBy("k").toDataFrame().group("k").into("keys")

        val empty = nested.tail(0)

        empty.getFrameColumn("group").schema.value.columns.keys shouldBe setOf("k", "v")
        empty.schema() shouldBe nested.schema()
    }

    @Test
    fun `nullable column becomes non-nullable when its last numRows values have no null`() {
        val nullable = dataFrameOf("w")(1, null, 3, 4)
        nullable["w"].type() shouldBe typeOf<Int?>()

        nullable.tail(2)["w"].type() shouldBe typeOf<Int>()
        nullable.tail(3)["w"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `tail with numRows covering all rows keeps the column types as they are`() {
        // declared nullable, but holds no `null`
        val declared = dataFrameOf(DataColumn.createValueColumn("w", listOf(1, 2), typeOf<Int?>()))

        declared.tail()["w"].type() shouldBe typeOf<Int?>()
        declared.tail(2)["w"].type() shouldBe typeOf<Int?>()
        // with a row left out, the nullability follows the values
        declared.tail(1)["w"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `tail with negative numRows throws`() {
        shouldThrow<IllegalArgumentException> {
            df.tail(-1)
        }.message shouldBe "Requested rows count -1 is less than zero."
    }
}

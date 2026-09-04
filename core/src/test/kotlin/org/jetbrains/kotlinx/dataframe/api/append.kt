package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import kotlin.reflect.typeOf

class AppendTests {

    // region append

    private data class Person(val name: String, val age: Int) : DataRowSchema

    @Test
    fun `append adds one row assigning values by column order`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.append("Bill", 30)

        result shouldBe dataFrameOf("name", "age")("Alice", 20, "Bill", 30)
    }

    @Test
    fun `append adds multiple rows interpreting values row-wise`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.append("Mike", 15, "John", 17, "Bill", 30)

        result shouldBe dataFrameOf("name", "age")("Alice", 20, "Mike", 15, "John", 17, "Bill", 30)
    }

    @Test
    fun `append rejects wrong number of arguments`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val exception = shouldThrow<IllegalArgumentException> {
            df.append("Bill")
        }

        exception.message shouldBe "Invalid number of arguments. Multiple of 2 is expected, but actual was: 1"
    }

    @Test
    fun `append zero rows`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.append()

        (result === df) shouldBe true
    }

    @Test
    fun `append adds null as a value to a value column`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.append(null, 30)

        result shouldBe dataFrameOf("name", "age")("Alice", 20, null, 30)
    }

    @Test
    fun `append null to frame column replaces null with empty dataframe with schema`() {
        val frame = dataFrameOf("value")(1)
        val df = dataFrameOf(columnOf(frame) named "col")

        val result = df.append(null)

        result.nrow shouldBe 2
        result["col"].kind() shouldBe ColumnKind.Frame
        result["col"].type() shouldBe typeOf<DataFrame<*>>()
        result["col"].values() shouldBe listOf(frame, DataFrame.empty(frame.schema()))
    }

    @Test
    fun `append uses the DataRowSchema overload`() {
        val df = dataFrameOf(Person("Alice", 20))

        val result = df.append(Person("Bill", 30))

        result shouldBe dataFrameOf("name", "age")("Alice", 20, "Bill", 30)
    }

    @Test
    fun `append zero rows for a dataframe with no columns`() {
        val df = DataFrame.empty(nrow = 2)

        val result = df.append()

        (result === df) shouldBe true
    }

    @Test
    fun `append rejects values for a dataframe with no columns`() {
        val df = DataFrame.empty(nrow = 2)

        val exception = shouldThrow<IllegalArgumentException> {
            df.append(1)
        }

        exception.message shouldBe "Cannot append values to a DataFrame with no columns"
    }

    // endregion

    // region appendNulls

    @Test
    fun `appendNulls adds one row by default`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.appendNulls()

        result shouldBe dataFrameOf("name", "age")("Alice", 20, null, null)
    }

    @Test
    fun `appendNulls adds the requested number of rows`() {
        val df = dataFrameOf("name", "age")("Alice", 20)

        val result = df.appendNulls(3)

        result shouldBe dataFrameOf("name", "age")("Alice", 20, null, null, null, null, null, null)
    }

    @Test
    fun `appendNulls with zero rows returns the original dataframe`() {
        val df = dataFrameOf("value")(1)

        val result = df.appendNulls(0)

        (result === df) shouldBe true
    }

    @Test
    fun `appendNulls rejects a negative number of rows`() {
        val df = dataFrameOf("value")(1)

        shouldThrow<IllegalArgumentException> {
            df.appendNulls(-1)
        }
    }

    @Test
    fun `appendNulls adds rows to a dataframe with no columns`() {
        val df = DataFrame.empty(nrow = 2)

        val result = df.appendNulls(3)

        result.ncol shouldBe 0
        result.nrow shouldBe 5
    }

    @Test
    fun `appendNulls adds to a frame column empty dataframes`() {
        val frame = dataFrameOf("value")(1)
        val df = dataFrameOf(columnOf(frame) named "col")

        val result = df.appendNulls(2)

        result.nrow shouldBe 3
        result["col"].kind() shouldBe ColumnKind.Frame
        result["col"].type() shouldBe typeOf<DataFrame<*>>()
        result["col"].values() shouldBe listOf(
            frame,
            DataFrame.empty(frame.schema()),
            DataFrame.empty(frame.schema()),
        )
    }

    @Test
    fun `appendNulls with a column group`() {
        val df = dataFrameOf(
            "person" to columnOf(
                "name" to columnOf("Alice"),
                "age" to columnOf(20),
            ),
        )

        val result = df.appendNulls()
        val resultGroup = result["person"].asColumnGroup()

        result.nrow shouldBe 2
        resultGroup.kind() shouldBe ColumnKind.Group
        resultGroup["name"].values() shouldBe listOf("Alice", null)
        resultGroup["age"].values() shouldBe listOf(20, null)
    }

    // endregion
}

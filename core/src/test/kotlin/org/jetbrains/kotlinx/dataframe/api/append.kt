package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.junit.Test
import kotlin.reflect.typeOf

class AppendTests {

    private data class ExplicitSchemaPerson(val name: String, val age: Int) : DataRowSchema

    // region append

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
    fun `DataRowSchema append zero rows returns the original dataframe`() {
        val df = dataFrameOf(ExplicitSchemaPerson("Alice", 20))

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
    fun `append adds list values to a column group by column order`() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(20),
        )

        val result = df.append(listOf("Bob", "Dylan"), 30)

        result shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob"),
                "lastName" to columnOf("Cooper", "Dylan"),
            ),
            "age" to columnOf(20, 30),
        )
    }

    @Test
    fun `append matches data row values to column group columns by name`() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(20),
        )
        val row = dataFrameOf("lastName", "firstName")("Dylan", "Bob")[0]

        val result = df.append(row, 30)

        result shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", "Bob"),
                "lastName" to columnOf("Cooper", "Dylan"),
            ),
            "age" to columnOf(20, 30),
        )
    }

    @Test
    fun `append uses null for column group columns absent from a data row`() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(20),
        )
        val row = dataFrameOf("first", "last")("Bob", "Dylan")[0]

        val result = df.append(row, 30)

        result shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", null),
                "lastName" to columnOf("Cooper", null),
            ),
            "age" to columnOf(20, 30),
        )
    }

    @Test
    fun `append null to a column group adds null to every nested column`() {
        val df = dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice"),
                "lastName" to columnOf("Cooper"),
            ),
            "age" to columnOf(20),
        )

        val result = df.append(null, 30)

        result shouldBe dataFrameOf(
            "name" to columnOf(
                "firstName" to columnOf("Alice", null),
                "lastName" to columnOf("Cooper", null),
            ),
            "age" to columnOf(20, 30),
        )
    }

    @Test
    fun `append adds a dataframe to a frame column`() {
        val alice = dataFrameOf("name", "age")("Alice", 20)
        val bob = dataFrameOf("name", "age")("Bob", 30)
        val df = dataFrameOf(columnOf(alice) named "people")

        val result = df.append(bob)

        result shouldBe dataFrameOf(columnOf(alice, bob) named "people")
    }

    @Test
    fun `append null to frame column replaces null with empty dataframe with schema`() {
        val frame = dataFrameOf("value")(1)
        val df = dataFrameOf(columnOf(frame) named "col")

        val result = df.append(null)
        val resultColumn = result.getFrameColumn("col")

        result.nrow shouldBe 2
        resultColumn.kind() shouldBe ColumnKind.Frame
        resultColumn.type() shouldBe typeOf<DataFrame<*>>()
        resultColumn.values() shouldBe listOf(frame, DataFrame.empty(frame.schema()))
        resultColumn.values().toList() shouldForAll {
            shouldNotThrowAny {
                it["value"]
            }
        }
    }

    @Test
    fun `append null preserves frame column schema across repeated calls`() {
        val frame = dataFrameOf("value")(1)
        val schema = dataFrameOf(columnOf(frame) named "col").schema()
        val df = DataFrame.empty(schema)

        val result = df.append(null).append(null)
        val resultColumn = result.getFrameColumn("col")

        result.nrow shouldBe 2
        result.schema() shouldBe schema
        resultColumn.values().toList() shouldForAll {
            shouldNotThrowAny {
                it["value"]
            }
        }
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
        val resultColumn = result.getFrameColumn("col")

        result.nrow shouldBe 3
        resultColumn.kind() shouldBe ColumnKind.Frame
        resultColumn.type() shouldBe typeOf<DataFrame<*>>()
        resultColumn.values() shouldBe listOf(
            frame,
            DataFrame.empty(frame.schema()),
            DataFrame.empty(frame.schema()),
        )
        resultColumn.values().toList() shouldForAll {
            shouldNotThrowAny {
                it["value"]
            }
        }
    }

    @Test
    fun `appendNulls preserves frame column schema across repeated calls`() {
        val frame = dataFrameOf("value")(1)
        val schema = dataFrameOf(columnOf(frame) named "col").schema()
        val df = DataFrame.empty(schema)

        val result = df.appendNulls().appendNulls()
        val resultColumn = result.getFrameColumn("col")

        result.nrow shouldBe 2
        result.schema() shouldBe schema
        resultColumn.values().toList() shouldForAll {
            shouldNotThrowAny {
                it["value"]
            }
        }
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

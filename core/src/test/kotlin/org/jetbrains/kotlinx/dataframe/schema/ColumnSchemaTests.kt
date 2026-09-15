package org.jetbrains.kotlinx.dataframe.schema

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.junit.Test
import kotlin.reflect.typeOf

class ColumnSchemaTests {

    // one column of each kind: a value column, a column group (from a DataRow value)
    // and a frame column (from a DataFrame value)
    private val df = dataFrameOf("age", "name", "g")(
        15,
        dataFrameOf("first")("Alice").first(),
        dataFrameOf("x")(1, 2),
    )

    @Test
    fun `there is one column schema subclass per column kind`() {
        val columns = df.schema().columns
        columns.getValue("age").shouldBeInstanceOf<ColumnSchema.Value>().kind shouldBe ColumnKind.Value
        columns.getValue("name").shouldBeInstanceOf<ColumnSchema.Group>().kind shouldBe ColumnKind.Group
        columns.getValue("g").shouldBeInstanceOf<ColumnSchema.Frame>().kind shouldBe ColumnKind.Frame
    }

    @Test
    fun `a column group schema holds the schema of its nested columns`() {
        val group = df.schema().columns.getValue("name").shouldBeInstanceOf<ColumnSchema.Group>()
        group.schema.toString() shouldBe "first: String"
    }

    @Test
    fun `a frame column schema holds the schema of the dataframes in the column`() {
        val frame = df.schema().columns.getValue("g").shouldBeInstanceOf<ColumnSchema.Frame>()
        frame.schema.toString() shouldBe "x: Int"
    }

    @Test
    fun `column schemas of different kinds are never comparable`() {
        val value = df.schema().columns.getValue("age")
        val group = df.schema().columns.getValue("name")
        // the mode cannot make schemas of different kinds match
        value.compare(group, ComparisonMode.LENIENT) shouldBe CompareResult.None
        value.compare(group, ComparisonMode.STRICT) shouldBe CompareResult.None
    }

    @Test
    fun `a value column schema is compared by its type`() {
        val int = ColumnSchema.Value(typeOf<Int>())
        val number = ColumnSchema.Value(typeOf<Number>())
        // Int is a Number, so leniently the Int schema is derived from the Number one
        int.compare(number, ComparisonMode.LENIENT) shouldBe CompareResult.IsDerived
        // strictly, only the very same type matches
        int.compare(number, ComparisonMode.STRICT) shouldBe CompareResult.None
        int.compare(ColumnSchema.Value(typeOf<Int>()), ComparisonMode.STRICT) shouldBe CompareResult.Matches
    }
}

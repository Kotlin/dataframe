@file:Suppress("ktlint")

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
import org.jetbrains.kotlinx.dataframe.exceptions.DuplicateColumnNamesException
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test
import kotlin.reflect.typeOf

class CreateColumnSamples : DataFrameSampleHelper("createColumn", "api") {

    @Test
    fun createValueByColumnOf() {
        // SampleStart
        // Create ValueColumn with name 'student' and two elements of type String
        val student by columnOf("Alice", "Bob")
        // SampleEnd
    }

    @Test
    fun createColumnRenamed() {
        // SampleStart
        val column = columnOf("Alice", "Bob") named "student"
        // SampleEnd
    }

    @Test
    fun createColumnGroup() {
        // SampleStart
        val firstName by columnOf("Alice", "Bob")
        val lastName by columnOf("Cooper", "Marley")

        // Create ColumnGroup with two nested columns
        val fullName by columnOf(firstName, lastName)
        // SampleEnd
    }

    @Test
    fun createFrameColumn() {
        // SampleStart
        val df1 = dataFrameOf("name", "age")(
            "Alice", 20,
            "Bob", 25,
        )
        val df2 = dataFrameOf("name", "temp")(
            "Charlie", 36.6,
        )

        // Create FrameColumn with two elements of type DataFrame
        val frames by columnOf(df1, df2)
        // SampleEnd
    }

    @Test
    fun createColumnOfMixedValues() {
        // SampleStart
        // Mixed values need an explicit type argument
        val weights = columnOf<Number>(1, 2.5)
        // SampleEnd
        weights.type shouldBe typeOf<Number>()
    }

    @Test
    fun createColumnOfLists() {
        // SampleStart
        // Lists are stored as cell values: a ValueColumn of List<Int>, not a ColumnGroup
        val numbers by columnOf(listOf(1, 2), listOf(3, 4))
        // SampleEnd
        numbers.type shouldBe typeOf<List<Int>>()
    }

    @Test
    fun createColumnOfPairs() {
        // SampleStart
        // A ColumnGroup: each column is renamed to its key
        val personal = columnOf(
            "firstName" to columnOf("Alice", "Bob"),
            "lastName" to columnOf("Cooper", "Marley"),
        )
        // SampleEnd
        personal.columnNames() shouldBe listOf("firstName", "lastName")
        personal["firstName"].values shouldBe listOf("Alice", "Bob")
        shouldThrow<DuplicateColumnNamesException> {
            columnOf("a" to columnOf(1), "a" to columnOf(2))
        }
        shouldThrow<UnequalColumnSizesException> {
            columnOf("a" to columnOf(1, 2), "b" to columnOf(1))
        }
    }

    @Test
    fun createColumnPath() {
        // SampleStart
        val path = pathOf("personal", "age")
        // SampleEnd
        path.path shouldBe listOf("personal", "age")
        path.name() shouldBe "age"
    }

    @Test
    fun createComputedColumnReference_properties() {
        // SampleStart
        val isAdult = column(peopleDf, "isAdult") { age > 18 }
        val withColumn = peopleDf.add { add(isAdult) }
        // SampleEnd
        withColumn["isAdult"].values shouldBe listOf(false, true, true, true, true, true, true, true, false, false)
    }

    @Test
    fun createComputedColumnReference_strings() {
        // SampleStart
        val isAdult = column<Boolean>("isAdult") { getValue<Int>("age") > 18 }
        val withColumn = peopleDf.add { add(isAdult) }
        // SampleEnd
        withColumn["isAdult"].values shouldBe listOf(false, true, true, true, true, true, true, true, false, false)
    }

    @Test
    fun createValueByToColumn() {
        // SampleStart
        listOf("Alice", "Bob").toColumn("name")
        // SampleEnd
    }

    @Test
    fun createValueColumnInferred() {
        // SampleStart
        val values: List<Any?> = listOf(1, 2.5)

        values.toColumn("data") // type: Any?
        values.toColumn("data", Infer.Type) // type: Number
        values.toColumn("data", Infer.Nulls) // type: Any
        // SampleEnd
    }

    @Test
    fun createValueColumnOfType() {
        // SampleStart
        val values: List<Any?> = listOf(1, 2.5)

        values.toColumnOf<Number?>("data") // type: Number?
        // SampleEnd
    }
}

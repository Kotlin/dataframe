package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnGroup
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toColumnOf
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
        val df1 = dataFrameOf("name", "age")("Alice", 20, "Bob", 25)
        val df2 = dataFrameOf("name", "temp")("Charlie", 36.6)

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
    fun createColumnAccessor() {
        // SampleStart
        val df = dataFrameOf("age", "city")(20, "London", 15, "Milan")

        // Reference to the column named "age"
        val age = column<Int>("age")
        // The name is taken from the property
        val city by column<String>()

        val selected = df.select { age and city }
        // SampleEnd
        selected.columnNames() shouldBe listOf("age", "city")
        selected["age"].values shouldBe listOf(20, 15)
    }

    @Test
    fun createNestedColumnAccessor() {
        // SampleStart
        val personal = columnGroup("personal")
        // Reference to the nested column "personal"/"age"
        val age by personal.column<Int>()
        // SampleEnd
        age.path() shouldBe pathOf("personal", "age")
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

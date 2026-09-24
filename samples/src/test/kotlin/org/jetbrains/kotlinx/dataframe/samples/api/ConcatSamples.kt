package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.concatWithKeys
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ConcatSamples : DataFrameSampleHelper("concat", "api") {

    @DataSchema
    interface Person {
        val name: String
        val age: Int
        val city: String
    }

    @DataSchema
    interface BasicPerson {
        val name: String
        val age: Int
    }

    @DataSchema
    interface PersonWithCity {
        val name: String
        val age: Double
        val city: String
    }

    private val df: DataFrame<Person> = dataFrameOf(
        "name" to columnOf("Alice", "Bob", "Charlie"),
        "age" to columnOf(20, 15, 25),
        "city" to columnOf("London", "Paris", "London"),
    ).cast()

    private val peopleDf1: DataFrame<BasicPerson> = dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(20, 15),
    ).cast()

    private val peopleDf2: DataFrame<PersonWithCity> = dataFrameOf(
        "name" to columnOf("Charlie"),
        "age" to columnOf(25.0),
        "city" to columnOf("London"),
    ).cast()

    @Test
    fun concatDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataColumns() {
        // SampleStart
        peopleDf1.age.concat(peopleDf2.age)
            // SampleEnd
            .also { it shouldBe columnOf<Number>(20, 15, 25.0).named("age") }
            .saveDfHtmlSample()
    }

    @Test
    fun concatFrameColumn() {
        // SampleStart
        val frames = columnOf(peopleDf1, peopleDf2).named("people")

        frames.concat() // "age" now has type Number, "city" - String?
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to columnOf("Alice", "Bob", "Charlie"),
                    "age" to columnOf<Number>(20, 15, 25.0),
                    "city" to columnOf<String?>(null, null, "London"),
                )
            }.saveDfHtmlSample()
    }

    @Test
    fun concatCollectionColumn() {
        // SampleStart
        val nameGroups = columnOf<Collection<String>>(listOf("Alice", "Bob"), emptySet(), setOf("Charlie"))

        nameGroups.concat() // result is listOf("Alice", "Bob", "Charlie")
            // SampleEnd
            .also { it shouldBe listOf("Alice", "Bob", "Charlie") }
    }

    @Test
    fun concatDataRows() {
        // SampleStart
        df[0].concat(df[1], df[2])
            // SampleEnd
            .also { it shouldBe df }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFrames() {
        // SampleStart
        val df1 = dataFrameOf("name", "age")("Alice", 20)
        val df2 = dataFrameOf("name", "age")("Bob", 15)
        val df3 = dataFrameOf("name", "age")("Charlie", 25)

        df1.concat(df2, df3)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf("Alice", "Bob", "Charlie"),
                    "age" to columnOf(20, 15, 25),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFramesFirstInput() {
        // SampleStart
        peopleDf1
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFramesSecondInput() {
        // SampleStart
        peopleDf2
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFramesWithDifferentSchemas() {
        // SampleStart
        (peopleDf1 concat peopleDf2)
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to columnOf("Alice", "Bob", "Charlie"),
                    "age" to columnOf<Number>(20, 15, 25.0),
                    "city" to columnOf<String?>(null, null, "London"),
                )
            }.saveDfHtmlSample()
    }

    @Test
    fun concatDataFrameAndRows() {
        // SampleStart
        val registeredPeople = dataFrameOf("name", "age", "city")("Alice", 20, "London")
        val newPeople = dataFrameOf("name", "age", "city")("Bob", 15, "Paris", "Charlie", 25, "London")

        registeredPeople.concat(newPeople.rows())
            // SampleEnd
            .also { it shouldBe df }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFrameAndIterable() {
        // SampleStart
        val registeredPeople = dataFrameOf("name", "age", "city")("Alice", 20, "London")
        val newPeople = listOf(
            dataFrameOf("name", "age", "city")("Bob", 15, "Paris"),
            dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
        )

        registeredPeople.concat(newPeople)
            // SampleEnd
            .also { it shouldBe df }
            .saveDfHtmlSample()
    }

    @Test
    fun concatGroupBy() {
        // SampleStart
        val grouped = df.groupBy {
            expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
        }

        grouped.concat()
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to listOf("Alice", "Charlie", "Bob"),
                    "age" to listOf(20, 25, 15),
                    "city" to listOf("London", "London", "Paris"),
                )
            }.saveDfHtmlSample()
    }

    @Test
    fun concatGroupByWithKeys() {
        // SampleStart
        val grouped = df.groupBy {
            expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
        }

        grouped.concatWithKeys()
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to listOf("Alice", "Charlie", "Bob"),
                    "age" to listOf(20, 25, 15),
                    "city" to listOf("London", "London", "Paris"),
                    "ageGroup" to listOf("adult", "adult", "teen"),
                )
            }.saveDfHtmlSample()
    }

    @Test
    fun concatReducedGroupBy() {
        // SampleStart
        df.groupBy { city }.first().concat()
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to listOf("Alice", "Bob"),
                    "age" to listOf(20, 15),
                    "city" to listOf("London", "Paris"),
                )
            }.saveDfHtmlSample()
    }

    @Test
    fun concatIterableDataFrames() {
        // SampleStart
        val frames = listOf(df[0..0], df[1..1], df[2..2])

        frames.concat()
            // SampleEnd
            .also { it shouldBe df }
            .saveDfHtmlSample()
    }

    @Test
    fun concatIterableDataColumns() {
        // SampleStart
        val columns = listOf(df[0..1].name, df[2..2].name)

        columns.concat()
            // SampleEnd
            .also { it shouldBe df.name }
            .saveDfHtmlSample()
    }

    @Test
    fun concatIterableRows() {
        // SampleStart
        val rows = listOf(df[0], null, df[1])

        rows.concat()
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to columnOf<String?>("Alice", null, "Bob"),
                    "age" to columnOf<Int?>(20, null, 15),
                    "city" to columnOf<String?>("London", null, "Paris"),
                )
            }.saveDfHtmlSample()
    }
}

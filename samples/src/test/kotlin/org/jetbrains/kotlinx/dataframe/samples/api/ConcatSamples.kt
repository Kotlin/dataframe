package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.concatWithKeys
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
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

    private val adultColor = RgbColor(189, 206, 233)
    private val teenColor = RgbColor(198, 224, 198)
    private val nullRowColor = RgbColor(230, 230, 230)

    @Test
    fun concatDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataFrames() {
        // SampleStart
        val firstBatch = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
        val secondBatch = dataFrameOf("name", "age", "city")("Alice", 20, "London")
        val thirdBatch = dataFrameOf("name", "age", "city")("Charlie", 25, "London")

        firstBatch.concat(secondBatch, thirdBatch)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf("Bob", "Alice", "Charlie"),
                    "age" to columnOf(15, 20, 25),
                    "city" to columnOf("Paris", "London", "London"),
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
            }
            .defaultHeaderFormatting { "city"() }
            .saveDfHtmlSample()
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
        val registeredPeople = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
        val newPeople = listOf(
            dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
            dataFrameOf("name", "age", "city")("Alice", 20, "London"),
        )

        registeredPeople.concat(newPeople)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf("Bob", "Charlie", "Alice"),
                    "age" to columnOf(15, 25, 20),
                    "city" to columnOf("Paris", "London", "London"),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataRows() {
        // SampleStart
        val youngest = df.minBy { age }
        val oldest = df.maxBy { age }

        youngest.concat(oldest)
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to columnOf("Bob", "Charlie"),
                    "age" to columnOf(15, 25),
                    "city" to columnOf("Paris", "London"),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDataColumns() {
        // SampleStart
        peopleDf1.age.concat(peopleDf2.age)
            // SampleEnd
            .also { it shouldBe columnOf<Number>(20, 15, 25.0).named("age") }
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun concatFrameColumn() {
        // SampleStart
        val teams = dataFrameOf(
            "team" to columnOf("Engineering", "Design"),
            "members" to columnOf(
                dataFrameOf("name", "age")("Alice", 20, "Bob", 15),
                dataFrameOf("name", "age")("Charlie", 25),
            ),
        )

        teams.members.concat()
            // SampleEnd
            .also { result ->
                result shouldBe dataFrameOf(
                    "name" to columnOf("Alice", "Bob", "Charlie"),
                    "age" to columnOf(20, 15, 25),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatCollectionColumn() {
        // SampleStart
        val nameGroups = columnOf<Collection<String>>(listOf("Alice", "Bob"), emptySet(), setOf("Charlie"))

        nameGroups.concat()
            // SampleEnd
            .also { it shouldBe listOf("Alice", "Bob", "Charlie") }
            .toDataFrame("name")
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
            }.format("age").with {
                val age = it as Int
                background(if (age >= 20) adultColor else teenColor) and textColor(black)
            }
            .saveDfHtmlSample()
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
            }.format("ageGroup").with {
                background(if (it == "adult") adultColor else teenColor) and textColor(black)
            }
            .defaultHeaderFormatting { "ageGroup"() }
            .saveDfHtmlSample()
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
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatIterableDataFrames() {
        // SampleStart
        val batches = listOf(
            dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
            dataFrameOf("name", "age", "city")("Alice", 20, "London", "Bob", 15, "Paris"),
        )

        batches.concat()
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf("Charlie", "Alice", "Bob"),
                    "age" to columnOf(25, 20, 15),
                    "city" to columnOf("London", "London", "Paris"),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatIterableDataColumns() {
        // SampleStart
        val ageColumns = listOf(
            columnOf(20, 25).named("age"),
            columnOf(15).named("additionalAges"),
        )

        ageColumns.concat()
            // SampleEnd
            .also { it shouldBe columnOf(20, 25, 15).named("age") }
            .toDataFrame()
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
            }.format().perRowCol { row, _ ->
                if (row["name"] == null) background(nullRowColor) and textColor(black) else null
            }
            .saveDfHtmlSample()
    }

    @Test
    fun concatDfDataFrames() {
        val df1 = df
        val df2 = df
        // SampleStart
        df.concat(df1, df2)
            // SampleEnd
            .rowsCount() shouldBe 9
    }

    @Test
    fun concatDfInfix() {
        val df1 = df
        val result =
            // SampleStart
            df concat df1
        // SampleEnd
        result.rowsCount() shouldBe 6
    }

    @Test
    fun concatDfDataFrameIterable() {
        val df1 = df
        val df2 = df
        // SampleStart
        df.concat(listOf(df1, df2))
            // SampleEnd
            .rowsCount() shouldBe 9
    }

    @Test
    fun concatDfIterable() {
        val df1 = df
        val df2 = df
        // SampleStart
        listOf(df1, df2).concat()
            // SampleEnd
            .rowsCount() shouldBe 6
    }
}

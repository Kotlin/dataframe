package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.ExcessiveColumns
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.fill
import org.jetbrains.kotlinx.dataframe.api.getColumnGroup
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.exceptions.ExcessiveColumnsException
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

/**
 * Samples for the `convertTo` website page.
 */
class ConvertToSamples : DataFrameSampleHelper("convertTo", "api") {

    class MyType(val value: Int)

    @DataSchema
    class MySchema(val a: MyType, val b: MyType, val c: Int)

    fun customConvertersData() {
        // SampleStart
        class MyType(val value: Int)

        // SampleEnd
    }

    @Test
    fun customConverters_properties() {
        // SampleStart
        val df: AnyFrame = dataFrameOf(
            "a" to columnOf(1, 2, 3),
            "b" to columnOf("1", "2", "3"),
        )
        df.convertTo<MySchema> {
            // providing the converter: Int -> MyType, so column `a` can be converted
            convert<Int>().with { MyType(it) }
            // providing the parser: String -> MyType, so column `b` can be converted
            parser { MyType(it.toInt()) }
            // providing the filler for `c`, as it's missing in `df`
            fill { c }.with { a.value + b.value }
        }
            // SampleEnd
            .also {
                it.a.values().map { v -> v.value } shouldBe listOf(1, 2, 3)
                it.b.values().map { v -> v.value } shouldBe listOf(1, 2, 3)
                it.c.toList() shouldBe listOf(2, 4, 6)
            }
    }

    @Test
    fun customConverters_strings() {
        // SampleStart
        val df: AnyFrame = dataFrameOf(
            "a" to columnOf(1, 2, 3),
            "b" to columnOf("1", "2", "3"),
        )
        df.convertTo<MySchema> {
            // providing the converter: Int -> MyType, so column `a` can be converted
            convert<Int>().with { MyType(it) }
            // providing the parser: String -> MyType, so column `b` can be converted
            parser { MyType(it.toInt()) }
            // providing the filler for `c`, as it's missing in `df`
            fill { col<Int>("c") }.with { getValue<MyType>("a").value + getValue<MyType>("b").value }
        }
            // SampleEnd
            .also {
                it.a.values().map { v -> v.value } shouldBe listOf(1, 2, 3)
                it.b.values().map { v -> v.value } shouldBe listOf(1, 2, 3)
                it.c.toList() shouldBe listOf(2, 4, 6)
            }
    }

    @DataSchema
    interface Event {
        val name: String
        val date: LocalDate
    }

    @Test
    fun convertToWithDateParser() {
        // SampleStart
        val df: AnyFrame = dataFrameOf(
            "name" to columnOf("KotlinConf", "ADF Meetup"),
            "date" to columnOf("22.11.2025", "05.12.2025"),
        )
        // the default date parser can't read `dd.MM.yyyy`; a custom parser can
        df.convertTo<Event> {
            parser {
                LocalDate.parse(
                    it,
                    LocalDate.Format {
                        day()
                        chars(".")
                        monthNumber()
                        chars(".")
                        year()
                    },
                )
            }
        }
            // SampleEnd
            .also {
                it.date.toList() shouldBe listOf(LocalDate(2025, 11, 22), LocalDate(2025, 12, 5))
            }
            .saveDfHtmlSample()
    }

    @DataSchema
    interface PersonCore {
        val name: String
        val age: Int
    }

    @Test
    fun convertToExcessiveColumns() {
        // SampleStart
        val df: AnyFrame = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(15, 45),
            "weight" to columnOf(54, 87),
        )
        // `weight` is not in the schema:
        df.convertTo<PersonCore>() // kept, because `ExcessiveColumns.Keep` is the default
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("name", "age", "weight") }
            .saveDfHtmlSample()
        shouldThrow<ExcessiveColumnsException> {
            df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Fail)
        }
    }

    @Test
    fun convertToExcessiveColumnsRemove() {
        val df: AnyFrame = dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf(15, 45),
            "weight" to columnOf(54, 87),
        )
        // SampleStart
        df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Remove) // dropped
            // SampleEnd
            .also { it.columnNames() shouldBe listOf("name", "age") }
            .saveDfHtmlSample()
    }

    @DataSchema
    interface FullName {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Info {
        val city: String
        val weight: Int
    }

    @DataSchema
    interface Person {
        val name: DataRow<FullName>
        val info: DataRow<Info>
    }

    @Test
    fun convertToNestedSchema() {
        // SampleStart
        val df: AnyFrame = dataFrameOf(
            "firstName" to columnOf("Alice", "Bob"),
            "lastName" to columnOf("Cooper", "Dylan"),
            "city" to columnOf("London", "Dubai"),
            "weight" to columnOf("54", "87"),
        ).group("firstName", "lastName").into("name")
            .group("city", "weight").into("info")
        // `info.weight` is a String column; the schema requires Int, so it is converted inside the group
        df.convertTo<Person>()
            // SampleEnd
            .also { it.getColumnGroup("info")["weight"].values() shouldBe listOf(54, 87) }
            .saveDfHtmlSample()
    }

    @DataSchema
    interface PersonWithAge {
        val name: String
        val age: Int
    }

    @Test
    fun convertToMissingColumnFails() {
        // SampleStart
        // `age` is required by the schema but missing in the dataframe
        shouldThrow<IllegalArgumentException> {
            dataFrameOf("name")("Alice", "Bob").convertTo<PersonWithAge>()
        }
            // SampleEnd
            .message!!.saveTextSample()
    }
}

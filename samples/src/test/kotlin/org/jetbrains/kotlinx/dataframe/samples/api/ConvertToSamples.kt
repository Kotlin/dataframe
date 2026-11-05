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
        val converted = df.convertTo<MySchema> {
            convert<Int>().with { MyType(it) }
            parser { MyType(it.toInt()) }
            fill { c }.with { a.value + b.value }
        }
        converted.a.values().map { it.value } shouldBe listOf(1, 2, 3)
        converted.b.values().map { it.value } shouldBe listOf(1, 2, 3)
        converted.c.toList() shouldBe listOf(2, 4, 6)
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
            fill { c }.with { getValue<MyType>("a").value + getValue<MyType>("b").value }
        }
        // SampleEnd
        val converted = df.convertTo<MySchema> {
            convert<Int>().with { MyType(it) }
            parser { MyType(it.toInt()) }
            fill { c }.with { getValue<MyType>("a").value + getValue<MyType>("b").value }
        }
        converted.a.values().map { it.value } shouldBe listOf(1, 2, 3)
        converted.b.values().map { it.value } shouldBe listOf(1, 2, 3)
        converted.c.toList() shouldBe listOf(2, 4, 6)
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
                        dayOfMonth()
                        chars(".")
                        monthNumber()
                        chars(".")
                        year()
                    },
                )
            }
        }
        // SampleEnd
        df.convertTo<Event> {
            parser {
                LocalDate.parse(
                    it,
                    LocalDate.Format {
                        dayOfMonth()
                        chars(".")
                        monthNumber()
                        chars(".")
                        year()
                    },
                )
            }
        }.also {
            it.date.toList() shouldBe listOf(LocalDate(2025, 11, 22), LocalDate(2025, 12, 5))
        }.saveDfHtmlSample()
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
        df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Remove) // dropped
        // SampleEnd
        df.convertTo<PersonCore>().columnNames() shouldBe listOf("name", "age", "weight")
        val removed = df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Remove)
        removed.columnNames() shouldBe listOf("name", "age")
        removed.saveDfHtmlSample()
        shouldThrow<ExcessiveColumnsException> {
            df.convertTo<PersonCore>(excessiveColumnsBehavior = ExcessiveColumns.Fail)
        }
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
        val df: AnyFrame = dataFrameOf("firstName", "lastName", "city", "weight")(
            "Alice",
            "Cooper",
            "London",
            "54",
            "Bob",
            "Dylan",
            "Dubai",
            "87",
        ).group("firstName", "lastName").into("name")
            .group("city", "weight").into("info")
        // `info.weight` is a String column; the schema requires Int, so it is converted inside the group
        df.convertTo<Person>()
        // SampleEnd
        val converted = df.convertTo<Person>()
        converted.getColumnGroup("info")["weight"].values() shouldBe listOf(54, 87)
        converted.saveDfHtmlSample()
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
        shouldThrow<IllegalArgumentException> {
            dataFrameOf("name")("Alice", "Bob").convertTo<PersonWithAge>()
        }.message!!.saveTextSample()
    }
}

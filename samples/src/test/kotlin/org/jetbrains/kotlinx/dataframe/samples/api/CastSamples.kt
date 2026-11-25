package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asValueColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.castTo
import org.jetbrains.kotlinx.dataframe.api.castUnchecked
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.named
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.io.readJsonStr
import org.jetbrains.kotlinx.dataframe.io.toJson
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.AgeKeySchema.AgeKey
import org.jetbrains.kotlinx.dataframe.samples.api.CastSamples.PersonSchema.Person
import org.junit.Test

class CastSamples : DataFrameSampleHelper("cast", "api") {
    interface PersonSchema {
        // SampleStart
        @DataSchema
        interface Person {
            val name: String
            val age: Int
        }
        // SampleEnd
    }

    interface AgeKeySchema {
        // SampleStart
        @DataSchema
        data class AgeKey(val age: Int)
        // SampleEnd
    }

    interface CastToLocalSchema {
        // SampleStart
        // parse listOf("b:1:abc", "c:2:bca")
        // Return type: DataFrame<plugin generated local type>
        private fun convert(data: List<String>) =
            data.map { it.split(":") }.toDataFrame {
                "part1" from { it[0] }
                "part2" from { it[1].toInt() }
                "part3" from { it[2] }
            }

        fun serialize(data: List<String>): String = convert(data).toJson()

        fun deserializeAndUse(json: String): DataColumn<String> {
            val df = DataFrame.readJsonStr(json).castTo(schemaFrom = ::convert)
            // Possible to use properties
            return df.part1
        }
        // SampleEnd
    }

    private val df: DataFrame<*> = dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(20, 25),
    )

    @Test
    fun castDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun verifiedCast() {
        // SampleStart
        df.cast<Person>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs df }
    }

    @Test
    fun uncheckedCast() {
        // SampleStart
        df.castUnchecked<Person>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs df }
    }

    @Test
    fun castWithoutVerification() {
        // SampleStart
        df.cast<Person>(verify = false)
            // SampleEnd
            .also { it shouldBeSameInstanceAs df }
    }

    @Test
    fun castExtensionProperties() {
        // SampleStart
        df.cast<Person>(verify = true)
            .filter { age > 21 }
            .name
            // SampleEnd
            .also { it.toList() shouldBe listOf("Bob") }
            .saveDfHtmlSample()
    }

    @Test
    fun castExtensionPropertyFailsAtRuntime() {
        shouldThrow<IllegalStateException> {
            // SampleStart
            val people = dataFrameOf("name" to columnOf("Alice")).castUnchecked<Person>()
            // Throws IllegalStateException because there is no age column.
            people.age
            // SampleEnd
        }
    }

    @Test
    fun castKeepsExtraColumns() {
        // SampleStart
        dataFrameOf(
            "name" to columnOf("Alice"),
            "age" to columnOf(20),
            "city" to columnOf("London"),
        ).cast<Person>(verify = true)
            // SampleEnd
            .also {
                it shouldBe dataFrameOf(
                    "name" to columnOf("Alice"),
                    "age" to columnOf(20),
                    "city" to columnOf("London"),
                )
            }
            .saveDfHtmlSample()
    }

    @Test
    fun castIncompatibleColumnType() {
        shouldThrow<IllegalArgumentException> {
            // SampleStart
            // Throws IllegalArgumentException because age is String instead of Int.
            dataFrameOf(
                "name" to columnOf("Alice"),
                "age" to columnOf("20"),
            ).cast<Person>(verify = true)
            // SampleEnd
        }
    }

    @Test
    fun castMissingRequiredColumn() {
        shouldThrow<IllegalArgumentException> {
            // SampleStart
            // Throws IllegalArgumentException because the required age column is missing.
            dataFrameOf("name" to columnOf("Alice"))
                .cast<Person>(verify = true)
            // SampleEnd
        }
    }

    @Test
    fun castConvertValues() {
        // SampleStart
        // Converts age values from String to Int.
        dataFrameOf(
            "name" to columnOf("Alice", "Bob"),
            "age" to columnOf("20", "25"),
        ).convertTo<Person>()
            // SampleEnd
            .also { it shouldBe df }
    }

    @Test
    fun castToDataFrame() {
        // SampleStart
        val schemaSource = dataFrameOf(
            "name" to columnOf("Charlie"),
            "age" to columnOf(30),
        ).cast<Person>(verify = true)
        df.castTo(schemaFrom = schemaSource, verify = true)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice", "Bob") }
    }

    @Test
    fun castToInferredSchema() {
        // SampleStart
        val schemaSource = dataFrameOf(
            "name" to columnOf("Charlie"),
            "age" to columnOf(30),
        )
        df.castTo(schemaFrom = schemaSource)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice", "Bob") }
    }

    @Test
    fun castToFunction() {
        // SampleStart
        fun createPeopleDf(age: Int) =
            dataFrameOf(
                "age" to columnOf(age),
                "name" to columnOf("Sample"),
            )

        df.castTo(schemaFrom = ::createPeopleDf, verify = true)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice", "Bob") }
    }

    @Test
    fun castToLocalSchema() {
        val example = object : CastToLocalSchema {}
        val json = example.serialize(listOf("b:1:abc", "c:2:bca"))
        example.deserializeAndUse(json).toList() shouldBe listOf("b", "c")
    }

    @Test
    fun rowCast() {
        // SampleStart
        df[0].cast<Person>()
            // SampleEnd
            .also { it shouldBe df[0] }
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun uncheckedRowCast() {
        // SampleStart
        val row = df[0]
        row.castUnchecked<Person>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs row }
    }

    @Test
    fun dataColumnCast() {
        // SampleStart
        df["age"].cast<Int>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs df["age"] }
            .saveDfHtmlSample()
    }

    @Test
    fun valueColumnCast() {
        // SampleStart
        df["age"].asValueColumn().cast<Int>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs df["age"] }
    }

    @Test
    fun frameColumnCast() {
        // SampleStart
        columnOf(df).named("people").castFrameColumn<Person>()
            // SampleEnd
            .also { it shouldBe columnOf(df).named("people") }
            .toDataFrame()
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }

    @Test
    fun columnGroupCast() {
        // SampleStart
        df.asColumnGroup().cast<Person>()
            // SampleEnd
            .also { it shouldBe df.asColumnGroup() }
    }

    @Test
    fun groupByCast() {
        // SampleStart
        df.groupBy("age").cast<AgeKey, Person>()
            // SampleEnd
            .toHtmlWithOpenedNestedDfs()
            .saveDfHtmlSample()
    }
}

package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.asColumnGroup
import org.jetbrains.kotlinx.dataframe.api.asValueColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.castFrameColumn
import org.jetbrains.kotlinx.dataframe.api.castTo
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.junit.Test

class CastSamples {
    @DataSchema
    data class Person(val name: String, val age: Int)

    @DataSchema
    data class AgeKey(val age: Int)

    private val df: DataFrame<*> = dataFrameOf(
        "name" to columnOf("Alice"),
        "age" to columnOf(20),
    )

    private fun createPeopleDf(age: Int) =
        dataFrameOf(
            "age" to columnOf(age),
            "name" to columnOf("Sample"),
        )

    @Test
    fun verifiedCast() {
        // SampleStart
        df.cast<Person>(verify = true)
            // SampleEnd
            .also { it shouldBe df }
    }

    @Test
    fun uncheckedCast() {
        // SampleStart
        df.cast<Person>()
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
    fun castToDataFrame() {
        // SampleStart
        val schemaSource = dataFrameOf(
            "name" to columnOf("Bob"),
            "age" to columnOf(25),
        ).cast<Person>(verify = true)
        df.castTo(schemaFrom = schemaSource, verify = true)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice") }
    }

    @Test
    fun castToFunction() {
        // SampleStart
        df.castTo(schemaFrom = ::createPeopleDf, verify = true)
            // SampleEnd
            .also { it.name.toList() shouldBe listOf("Alice") }
    }

    @Test
    fun rowCast() {
        // SampleStart
        val row = df[0]
        row.cast<Person>(verify = true)
            // SampleEnd
            .also { it shouldBe row }
    }

    @Test
    fun uncheckedRowCast() {
        // SampleStart
        val row = df[0]
        row.cast<Person>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs row }
    }

    @Test
    fun dataColumnCast() {
        // SampleStart
        df["age"].cast<Int>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs df["age"] }
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
        columnOf(df).castFrameColumn<Person>()
            // SampleEnd
            .also { it shouldBe columnOf(df) }
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
        val grouped: GroupBy<*, *> = df.groupBy("age")
        // SampleStart
        grouped.cast<AgeKey, Person>()
            // SampleEnd
            .also { it shouldBeSameInstanceAs grouped }
    }
}

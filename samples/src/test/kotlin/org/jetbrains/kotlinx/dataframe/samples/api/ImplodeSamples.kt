package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.implode
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class ImplodeSamples : DataFrameSampleHelper("implode", "api") {

    @DataSchema
    interface Visit {
        val name: String
        val city: String?
        val year: Int
    }

    private val df: DataFrame<Visit> = dataFrameOf(
        "name" to columnOf("Alice", "Alice", "Bob", "Bob"),
        "city" to columnOf("London", "Milan", "Tokyo", null),
        "year" to columnOf(2020, 2021, 2020, 2022),
    ).cast()

    @Test
    fun implodeSelector_properties() {
        // SampleStart
        // Merges the "city" and "year" values of each person into lists,
        // one row per distinct "name"
        df.implode { city and year }
            // SampleEnd
            .let {
                it["name"].toList() shouldBe listOf("Alice", "Bob")
                it["city"].toList() shouldBe listOf(listOf("London", "Milan"), listOf("Tokyo", null))
                it["year"].toList() shouldBe listOf(listOf(2020, 2021), listOf(2020, 2022))
            }
    }

    @Test
    fun implodeDropNA_properties() {
        // SampleStart
        // Same as above, but `null` cities are removed from the lists
        df.implode(dropNA = true) { city and year }
            // SampleEnd
            .let {
                it["city"].toList() shouldBe listOf(listOf("London", "Milan"), listOf("Tokyo"))
                it["year"].toList() shouldBe listOf(listOf(2020, 2021), listOf(2020, 2022))
            }
    }

    @Test
    fun implodeSelector_strings() {
        // SampleStart
        // Merges the "city" and "year" values of each person into lists,
        // one row per distinct "name"
        df.implode("city", "year")
            // SampleEnd
            .let {
                it["name"].toList() shouldBe listOf("Alice", "Bob")
                it["city"].toList() shouldBe listOf(listOf("London", "Milan"), listOf("Tokyo", null))
            }
    }

    @Test
    fun implodeAll() {
        // SampleStart
        // Merges all values of every column into a single row of lists
        val row = df.implode()
        // SampleEnd
        row["name"] shouldBe listOf("Alice", "Alice", "Bob", "Bob")
        row["year"] shouldBe listOf(2020, 2021, 2020, 2022)
    }
}

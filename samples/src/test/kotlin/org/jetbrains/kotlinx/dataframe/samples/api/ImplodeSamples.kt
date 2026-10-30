package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.implode
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

// `df` is the input table of the `implode` KDoc examples, and every result table there is
// an expected value below. Changing the data or a call here means changing the KDoc tables too.
class ImplodeSamples : DataFrameSampleHelper("implode", "api") {

    @DataSchema
    interface Visit {
        val name: String
        val age: Int
        val city: String?
    }

    private val df: DataFrame<Visit> = dataFrameOf(
        "name" to columnOf("Alice", "Alice", "Bob", "Bob"),
        "age" to columnOf(15, 15, 45, 45),
        "city" to columnOf("London", "Milan", "Tokyo", null),
    ).cast()

    // The expected result of every `implode { city }` overload
    private fun DataFrame<*>.checkImplodedCity(dropNA: Boolean) {
        this["name"].toList() shouldBe listOf("Alice", "Bob")
        this["age"].toList() shouldBe listOf(15, 45)
        this["city"].toList() shouldBe listOf(
            listOf("London", "Milan"),
            if (dropNA) listOf("Tokyo") else listOf("Tokyo", null),
        )
    }

    @Test
    fun implodeSelector_properties() {
        // SampleStart
        // Merges the "city" values of each person into a list,
        // one row per distinct "name" and "age"
        df.implode { city }
            // SampleEnd
            .checkImplodedCity(dropNA = false)
    }

    @Test
    fun implodeDropNA_properties() {
        // SampleStart
        // Same as above, but `null` cities are removed from the lists
        df.implode(dropNA = true) { city }
            // SampleEnd
            .checkImplodedCity(dropNA = true)
    }

    @Test
    fun implodeSelector_strings() {
        // SampleStart
        // Merges the "city" values of each person into a list,
        // one row per distinct "name" and "age"
        df.implode("city")
            // SampleEnd
            .checkImplodedCity(dropNA = false)
    }

    @Test
    fun implodeDropNA_strings() {
        // SampleStart
        // Same as above, but `null` cities are removed from the lists
        df.implode("city", dropNA = true)
            // SampleEnd
            .checkImplodedCity(dropNA = true)
    }

    @Test
    fun implodeAll() {
        // SampleStart
        // Merges all values of every column into a single row of lists
        val row = df.implode()
        // SampleEnd
        row["name"] shouldBe listOf("Alice", "Alice", "Bob", "Bob")
        row["age"] shouldBe listOf(15, 15, 45, 45)
        row["city"] shouldBe listOf("London", "Milan", "Tokyo", null)
    }
}
